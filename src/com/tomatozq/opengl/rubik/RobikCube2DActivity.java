package com.tomatozq.opengl.rubik;

import java.util.ArrayList;
import java.util.Arrays;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tomatozq.opengl.R;
import com.tomatozq.opengl.tutorial.MainActivity;

public class RobikCube2DActivity extends Activity {
    private static final String TAG = "RobikCube2DActivity";
    private Button mBtn1;
    private Button mBtn2;
    private Button mBtn3;
    private Button mBtn4;
    private Button mBtn5;
    private Button mBtn6;
    private Button mBtn7;
    private Button mBtn8;
    private Button mBtn9;
    private LinearLayout mLayoutMain;
    private TextView mTxtRubikInfo;
    private Button mBtnSave;

    private GestureDetector mGesture;
    private GestureDetector mLayoutGesture;
    private Button mCurrentBtn;
    private String mCurrentBtnDirection;

    private String[] mTopButtonDirection = { "1L", "2L", "3L", "1R", "2R", "3R" };
    private String[] mEquatorButtonDirection = { "4L", "5L", "6L", "4R", "5R", "6R" };
    private String[] mBottomButtonDirection = { "7L", "8L", "9L", "7R", "8R", "9R" };
    private String[] mLeftButtonDirection = { "1U", "4U", "7U", "1D", "4D", "7D" };
    private String[] mMiddleButtonDirection = { "2U", "5U", "8U", "2D", "5D", "8D" };
    private String[] mRighttButtonDirection = { "3U", "6U", "9U", "3D", "6D", "9D" };

    private String[][] mButtonDirections = new String[][] { mTopButtonDirection,
            mEquatorButtonDirection, mBottomButtonDirection, mLeftButtonDirection,
            mMiddleButtonDirection, mRighttButtonDirection };
    private Button[] mButtons = new Button[9];
    private RobikCube2D mRobikCube2D;
    private ArrayList<String> mButtonDirectionTackerList = new ArrayList<String>();
    private ArrayList<String> mPreButtonDirectionTackerList = new ArrayList<String>();
    private SharedPreferences prefs;
    private int[] mColors = new int[9];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_robik_cube_2d);
        prefs = getPreferences(Context.MODE_PRIVATE);
        mRobikCube2D = new RobikCube2D();
        mGesture = new GestureDetector(this, new GestureListener());
        mLayoutGesture = new GestureDetector(this, new LayoutGestureListener());

        setupView();

        mButtons = new Button[] { mBtn1, mBtn2, mBtn3, mBtn4, mBtn5, mBtn6, mBtn7, mBtn8, mBtn9 };
        for (int i = 0; i < 9; i++) {
            mButtons[i].setTag(mButtons[i].getId(), i + 1);
            mButtons[i].setOnTouchListener(mOnButtonTouchListener);
            LayoutParams params = mButtons[i].getLayoutParams();
            params.height = getWindowManager().getDefaultDisplay().getWidth() / 3;
        }
        mBtnSave.setOnClickListener(mOnBtnSaveClickListener);

        mLayoutMain.setOnTouchListener(mOnLayoutTouchListener);

        if (!prefs.getBoolean("ro.inited.color", false)) {
            mRobikCube2D.initButtonColors(prefs);
        } else {
            mRobikCube2D.restoreButtonColors(prefs);
        }

        Editor editor = prefs.edit();
        editor.putBoolean("ro.inited.color", true);
        editor.commit();

        updateFrontButtonText();
        // setButtonForgroundColors();
        checkRobikPassword();
    }

    private void checkRobikPassword() {
        SharedPreferences prefs = getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);
        boolean isPwdSet = prefs.getBoolean("ro.set.password", false);
        if (isPwdSet) {
            // password has been set
            mTxtRubikInfo.setVisibility(View.GONE);
            mBtnSave.setVisibility(View.GONE);
        } else {
            // let user set the password for twice
            mTxtRubikInfo.setText(R.string.txt_set_pwd_and_save);
        }
    }

    private OnClickListener mOnBtnSaveClickListener = new OnClickListener() {

        @Override
        public void onClick(View arg0) {
            if (mPreButtonDirectionTackerList.size() == 0) {
                // set pwd first
                for (String str : mButtonDirectionTackerList) {
                    mPreButtonDirectionTackerList.add(str);
                }
                mButtonDirectionTackerList.clear();
                mTxtRubikInfo.setText(R.string.txt_confirm_set_pwd_and_save);
            } else {
                // confirm the pwd, make sure if it is the same.
                if (mButtonDirectionTackerList.equals(mPreButtonDirectionTackerList)) {
                    Log.d(TAG, "mOnBtnSaveClickListener: two password are the same");
                    saveCurrentState();
                    // navigate to the next activity
                    SharedPreferences prefs = getSharedPreferences("shared_prefs",
                            Context.MODE_PRIVATE);
                    Editor editor = prefs.edit();
                    editor.putBoolean("ro.set.password", true);
                    editor.commit();
                    launchGallery();
                    finish();
                } else {
                    // the two password is not the same:
                    Toast.makeText(getApplicationContext(), "两次密码输入不一致，请重新输入", Toast.LENGTH_LONG)
                            .show();
                    Intent intent = new Intent(RobikCube2DActivity.this, RobikCube2DActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        }
    };

    private void launchGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivity(intent);
    }

    private void setupView() {
        mBtn1 = (Button) findViewById(R.id.button1);
        mBtn2 = (Button) findViewById(R.id.button2);
        mBtn3 = (Button) findViewById(R.id.button3);
        mBtn4 = (Button) findViewById(R.id.button4);
        mBtn5 = (Button) findViewById(R.id.button5);
        mBtn6 = (Button) findViewById(R.id.button6);
        mBtn7 = (Button) findViewById(R.id.button7);
        mBtn8 = (Button) findViewById(R.id.button8);
        mBtn9 = (Button) findViewById(R.id.button9);
        mLayoutMain = (LinearLayout) findViewById(R.id.layout_main);
        mTxtRubikInfo = (TextView) findViewById(R.id.txt_robik_info);
        mBtnSave = (Button) findViewById(R.id.btn_save);
    }

    private void setButtonForgroundColors() {
        randomColors();
        for (int i = 0; i < 9; i++) {
            mButtons[i].setBackgroundColor(mColors[i]);
        }
    }

    private void randomColors() {
        mColors[0] = Color.CYAN;
        mColors[1] = Color.BLUE;
        mColors[2] = Color.GREEN;
        mColors[3] = Color.RED;
        mColors[4] = Color.WHITE;
        mColors[5] = Color.YELLOW;
        mColors[6] = Color.YELLOW;
        mColors[7] = Color.BLUE;
        mColors[8] = Color.RED;
    }

    OnTouchListener mOnTouchListener = new OnTouchListener() {

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            Button btn = (Button) view;
            Log.d(TAG,
                    "faceid : "
                            + btn.getText().toString()
                            + " color : "
                            + mRobikCube2D.findColorByFaceId(Integer.valueOf(btn.getText()
                                    .toString())));
            return false;
        }
    };

    OnTouchListener mOnButtonTouchListener = new OnTouchListener() {

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            mCurrentBtn = (Button) view;
            Log.d(TAG, mCurrentBtn.getText().toString());
            mGesture.onTouchEvent(event);
            return false;
        }
    };

    OnTouchListener mOnLayoutTouchListener = new OnTouchListener() {

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            Log.d(TAG, "mOnLayoutTouchListener");
            mLayoutGesture.onTouchEvent(event);
            return false;
        }
    };

    class LayoutGestureListener extends SimpleOnGestureListener {

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Log.d(TAG, "onFling velocityX:" + velocityX + " velocityY:" + velocityY);
            if (Math.abs(velocityX) > Math.abs(velocityY)) {
                // L or R
                if (velocityX > 0) {
                    // Right
                    mRobikCube2D.rotateTopLayerRight();
                    mRobikCube2D.rotateEquatorLayerRight();
                    mRobikCube2D.rotateBottomLayerRight();
                } else {
                    mRobikCube2D.rotateTopLayerLeft();
                    mRobikCube2D.rotateEquatorLayerLeft();
                    mRobikCube2D.rotateBottomLayerLeft();
                }
            } else {
                // T or D
                if (velocityY > 0) {
                    // Down:
                    mRobikCube2D.rotateLeftLayerBottom();
                    mRobikCube2D.rotateMiddleLayerBottom();
                    mRobikCube2D.rotateRightLayerBottom();
                } else {
                    mRobikCube2D.rotateLeftLayerTop();
                    mRobikCube2D.rotateMiddleLayerTop();
                    mRobikCube2D.rotateRightLayerTop();
                }
            }
            updateFrontButtonText();
            return super.onFling(e1, e2, velocityX, velocityY);
        }
    }

    private String mCurrentColorAndDrection;

    class GestureListener extends SimpleOnGestureListener {

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            String color = mRobikCube2D.findColorByFaceId(Integer.valueOf(mCurrentBtn.getTag()
                    .toString()));
            Log.d(TAG, "onFling velocityX:" + velocityX + " velocityY:" + velocityY + " color: "
                    + color);
            if (Math.abs(velocityX) > Math.abs(velocityY)) {
                // L or R
                if (velocityX > 0) {
                    // Right
                    mCurrentBtnDirection = mCurrentBtn.getTag(mCurrentBtn.getId()) + "R";
                    mCurrentColorAndDrection = color + "R";
                } else {
                    mCurrentBtnDirection = mCurrentBtn.getTag(mCurrentBtn.getId()) + "L";
                    mCurrentColorAndDrection = color + "L";
                }
            } else {
                // U or D
                if (velocityY > 0) {
                    // Right
                    mCurrentBtnDirection = mCurrentBtn.getTag(mCurrentBtn.getId()) + "D";
                    mCurrentColorAndDrection = color + "D";
                } else {
                    mCurrentBtnDirection = mCurrentBtn.getTag(mCurrentBtn.getId()) + "U";
                    mCurrentColorAndDrection = color + "U";
                }
            }
            // mButtonDirectionTackerList.add(mCurrentBtnDirection);
            // only save color and direction:
            mButtonDirectionTackerList.add(mCurrentColorAndDrection);
            Log.d(TAG, "onFling mCurrentBtnDirection:" + mCurrentBtnDirection
                    + " mCurrentColorAndDrection:" + mCurrentColorAndDrection
                    + " mButtonDirectionTackerList:" + mButtonDirectionTackerList.toString());
            updateFrontlayer();
            updateFrontButtonText();
            if (isValidateColorAndDirection()) {
                Log.d(TAG, "onFling Validated start new Activity:");
                launchGallery();
            }
            return super.onFling(e1, e2, velocityX, velocityY);
        }

    }

    private void updateFrontlayer() {
        int index = findButtonDirection();
        Log.d(TAG, "findButtonDirection:" + index);
        switch (index) {
        case 1:
            // Top
            if (mCurrentBtnDirection.contains("L")) {
                mRobikCube2D.rotateTopLayerLeft();
                Log.d(TAG, "rotateTopLayerLeft");
            } else {
                mRobikCube2D.rotateTopLayerRight();
                Log.d(TAG, "rotateTopLayerRight");
            }
            break;
        case 2:
            // Equator
            if (mCurrentBtnDirection.contains("L")) {
                mRobikCube2D.rotateEquatorLayerLeft();
                Log.d(TAG, "rotateEquatorLayerLeft");
            } else {
                mRobikCube2D.rotateEquatorLayerRight();
                Log.d(TAG, "rotateEquatorLayerRight");
            }
            break;
        case 3:
            // Bottom
            if (mCurrentBtnDirection.contains("L")) {
                mRobikCube2D.rotateBottomLayerLeft();
                Log.d(TAG, "rotateBottomLayerLeft");
            } else {
                mRobikCube2D.rotateBottomLayerRight();
                Log.d(TAG, "rotateBottomLayerRight");
            }
            break;
        case 4:
            // Left
            if (mCurrentBtnDirection.contains("U")) {
                mRobikCube2D.rotateLeftLayerTop();
                Log.d(TAG, "rotateLeftLayerTop");
            } else {
                mRobikCube2D.rotateLeftLayerBottom();
                Log.d(TAG, "rotateLeftLayerBottom");
            }
            break;
        case 5:
            // Middle
            if (mCurrentBtnDirection.contains("U")) {
                mRobikCube2D.rotateMiddleLayerTop();
                Log.d(TAG, "rotateMiddleLayerTop");
            } else {
                mRobikCube2D.rotateMiddleLayerBottom();
                Log.d(TAG, "rotateMiddleLayerBottom");
            }
            break;
        case 6:
            // Right
            if (mCurrentBtnDirection.contains("U")) {
                mRobikCube2D.rotateRightLayerTop();
                Log.d(TAG, "rotateRightLayerTop");
            } else {
                mRobikCube2D.rotateRightLayerBottom();
                Log.d(TAG, "rotateRightLayerBottom");
            }
            break;
        }
        mRobikCube2D.printFaces();
    }

    private void updateFrontButtonText() {
        mRobikCube2D.fillButtonWithFrontFace(mButtons);
    }

    private int findButtonDirection() {
        int index = 0;
        for (String[] directions : mButtonDirections) {
            index++;
            for (String direction : directions) {
                if (direction.equals(mCurrentBtnDirection)) {
                    return index;
                }
            }
        }
        return -1;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 1, "重置");
        menu.add(0, 2, 2, "介绍");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 1) {
            clearState();
        } else if (item.getItemId() == 2) {
            // saveCurrentState();
            luanchUserGuideActivity();
        }
        return true;
    }

    private void luanchUserGuideActivity() {
        Intent intent = new Intent(this, UserGuideActivity.class);
        intent.putExtra("extra_introduce", true);
        startActivity(intent);
    }

    private void saveCurrentState() {
        String[] tackerList = new String[mButtonDirectionTackerList.size()];
        mButtonDirectionTackerList.toArray(tackerList);
        Editor editor = prefs.edit();
        editor.putString("robik.cube2d.direction.tacker", Arrays.toString(tackerList));
        editor.commit();
        Log.d(TAG, "saveCurrentState mButtonDirectionTackerList : " + Arrays.toString(tackerList));
    }

    private boolean isValidateColorAndDirection() {
        // password length must larger than 3
        mBtnSave.setClickable(mButtonDirectionTackerList.size() > 2);
        String tacker = prefs.getString("robik.cube2d.direction.tacker", "");
        if (TextUtils.isEmpty(tacker)) {
            return false;
        }
        tacker = tacker.substring(1, tacker.length() - 1);
        String[] tackerPrevious = tacker.split(",");
        for (int i = 0; i < tackerPrevious.length; i++) {
            tackerPrevious[i] = tackerPrevious[i].trim();
        }
        String[] tackerCurrent = new String[mButtonDirectionTackerList.size()];
        mButtonDirectionTackerList.toArray(tackerCurrent);
        Log.d(TAG, "isValidateDirection tackerPrevious : " + Arrays.toString(tackerPrevious)
                + "\n tackerCurrent" + Arrays.toString(tackerCurrent));
        return Arrays.equals(tackerPrevious, tackerCurrent);
    }

    private void clearState() {
        Editor editor = prefs.edit();
        // editor.putString("robik.cube2d.direction.tacker", "");
        editor.clear();
        editor.commit();
        SharedPreferences sharedPrefs = getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);
        Editor sharedEditor = sharedPrefs.edit();
        sharedEditor.clear();
        sharedEditor.commit();
    }
}
