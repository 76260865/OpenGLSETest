package com.tomatozq.opengl.rubik;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import com.tomatozq.opengl.R;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Button;

public class RobikCube2D {
    private static final String TAG = "RobikCube2D";

    private static RobikCube2D mInstance = new RobikCube2D();

    private static RobikCube2D getInstance() {
        return mInstance;
    }

    // 面的数组一共6个面,每个面9个小方块
    private int[] topFace = { 10, 11, 12, 13, 14, 15, 16, 17, 18 };
    private int[] bottomFace = { 28, 29, 30, 31, 32, 33, 34, 35, 36 };
    private int[] leftFace = { 37, 38, 39, 40, 41, 42, 43, 44, 45 };
    private int[] rightFace = { 19, 20, 21, 22, 23, 24, 25, 26, 27 };
    private int[] frontFace = { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
    private int[] backFace = { 46, 47, 48, 49, 50, 51, 52, 53, 54 };
    private int[][] faces = { topFace, bottomFace, leftFace, rightFace, frontFace, backFace };

    // 前面一共占用6个层,每个层有2个方向可以选择
    private String[] layerUp = { "1L", "1R", "2L", "2R", "3L", "3R" };
    private String[] layerDown = { "7L", "7R", "8L", "8R", "9L", "9R" };
    private String[] layerLeft = { "1U", "1D", "4U", "4D", "7U", "7D" };
    private String[] layerRight = { "3U", "3D", "6U", "6D", "9U", "9D" };
    private String[] layerEquator = { "4L", "4R", "5L", "5R", "6L", "6R" };
    private String[] layerMiddle = { "2U", "2D", "5U", "5D", "8U", "8D" };
    private String mLayers[] = new String[6];

    public void rotateTopLayerRight() {
        int[] tempInitial = Arrays.copyOfRange(frontFace, 0, 3);
        int[] tempMiddle = new int[3];
        // right layer:
        for (int i = 0; i < 3; i++) {
            tempMiddle[i] = rightFace[i];
            rightFace[i] = tempInitial[i];
        }
        // back layer:
        for (int i = 0; i < 3; i++) {
            tempInitial[i] = backFace[i];
            backFace[i] = tempMiddle[i];
        }
        // left layer:
        for (int i = 0; i < 3; i++) {
            tempMiddle[i] = leftFace[i];
            leftFace[i] = tempInitial[i];
        }
        // front layer:
        for (int i = 0; i < 3; i++) {
            tempInitial[i] = frontFace[i];
            frontFace[i] = tempMiddle[i];
        }
        // top layer:
        rotateTopFaceRight();
    }

    private void rotateTopFaceRight() {
        int[] temp = Arrays.copyOf(topFace, topFace.length);
        for (int i = 0; i < 3; i++) {
            topFace[i] = temp[i * 3 + 2];
        }
        topFace[3] = temp[1];
        topFace[4] = temp[4];
        topFace[5] = temp[7];
        topFace[6] = temp[0];
        topFace[7] = temp[3];
        topFace[8] = temp[6];
    }

    public void rotateEquatorLayerRight() {
        int[] tempInitial = Arrays.copyOfRange(frontFace, 3, 6);
        int[] tempMiddle = new int[3];
        // right layer:
        for (int i = 3; i < 6; i++) {
            tempMiddle[i - 3] = rightFace[i];
            rightFace[i] = tempInitial[i - 3];
        }
        // back layer:
        for (int i = 3; i < 6; i++) {
            tempInitial[i - 3] = backFace[i];
            backFace[i] = tempMiddle[i - 3];
        }
        // left layer:
        for (int i = 3; i < 6; i++) {
            tempMiddle[i - 3] = leftFace[i];
            leftFace[i] = tempInitial[i - 3];
        }
        // front layer:
        for (int i = 3; i < 6; i++) {
            tempInitial[i - 3] = frontFace[i];
            frontFace[i] = tempMiddle[i - 3];
        }
    }

    public void rotateBottomLayerRight() {
        int[] tempInitial = Arrays.copyOfRange(frontFace, 6, 9);
        int[] tempMiddle = new int[3];
        // right layer:
        for (int i = 6; i < 9; i++) {
            tempMiddle[i - 6] = rightFace[i];
            rightFace[i] = tempInitial[i - 6];
        }
        // back layer:
        for (int i = 6; i < 9; i++) {
            tempInitial[i - 6] = backFace[i];
            backFace[i] = tempMiddle[i - 6];
        }
        // left layer:
        for (int i = 6; i < 9; i++) {
            tempMiddle[i - 6] = leftFace[i];
            leftFace[i] = tempInitial[i - 6];
        }
        // front layer:
        for (int i = 6; i < 9; i++) {
            tempInitial[i - 6] = frontFace[i];
            frontFace[i] = tempMiddle[i - 6];
        }
        rotateBottomFaceRight();
    }

    private void rotateBottomFaceRight() {
        int[] temp = Arrays.copyOf(bottomFace, bottomFace.length);
        bottomFace[0] = temp[6];
        bottomFace[1] = temp[3];
        bottomFace[2] = temp[0];
        bottomFace[3] = temp[7];
        bottomFace[4] = temp[4];
        bottomFace[5] = temp[1];
        bottomFace[6] = temp[8];
        bottomFace[7] = temp[5];
        bottomFace[8] = temp[2];
    }

    public void rotateTopLayerLeft() {
        int[] tempInitial = Arrays.copyOfRange(frontFace, 0, 3);
        int[] tempMiddle = new int[3];
        // left layer:
        for (int i = 0; i < 3; i++) {
            tempMiddle[i] = leftFace[i];
            leftFace[i] = tempInitial[i];
        }
        // back layer:
        for (int i = 0; i < 3; i++) {
            tempInitial[i] = backFace[i];
            backFace[i] = tempMiddle[i];
        }
        // right layer:
        for (int i = 0; i < 3; i++) {
            tempMiddle[i] = rightFace[i];
            rightFace[i] = tempInitial[i];
        }
        // front layer:
        for (int i = 0; i < 3; i++) {
            tempInitial[i] = frontFace[i];
            frontFace[i] = tempMiddle[i];
        }
        // top layer:
        rotateTopFaceleft();
    }

    private void rotateTopFaceleft() {
        int[] temp = Arrays.copyOf(topFace, topFace.length);
        topFace[0] = temp[6];
        topFace[1] = temp[3];
        topFace[2] = temp[0];
        topFace[3] = temp[7];
        topFace[4] = temp[4];
        topFace[5] = temp[1];
        topFace[6] = temp[8];
        topFace[7] = temp[5];
        topFace[8] = temp[2];
    }

    public void rotateEquatorLayerLeft() {
        int[] tempInitial = Arrays.copyOfRange(frontFace, 3, 6);
        int[] tempMiddle = new int[3];
        // left layer:
        for (int i = 3; i < 6; i++) {
            tempMiddle[i - 3] = leftFace[i];
            leftFace[i] = tempInitial[i - 3];
        }
        // back layer:
        for (int i = 3; i < 6; i++) {
            tempInitial[i - 3] = backFace[i];
            backFace[i] = tempMiddle[i - 3];
        }
        // right layer:
        for (int i = 3; i < 6; i++) {
            tempMiddle[i - 3] = rightFace[i];
            rightFace[i] = tempInitial[i - 3];
        }
        // front layer:
        for (int i = 3; i < 6; i++) {
            tempInitial[i - 3] = frontFace[i];
            frontFace[i] = tempMiddle[i - 3];
        }
    }

    public void rotateBottomLayerLeft() {
        int[] tempInitial = Arrays.copyOfRange(frontFace, 6, 9);
        int[] tempMiddle = new int[3];
        // left layer:
        for (int i = 6; i < 9; i++) {
            tempMiddle[i - 6] = leftFace[i];
            leftFace[i] = tempInitial[i - 6];
        }
        // back layer:
        for (int i = 6; i < 9; i++) {
            tempInitial[i - 6] = backFace[i];
            backFace[i] = tempMiddle[i - 6];
        }
        // right layer:
        for (int i = 6; i < 9; i++) {
            tempMiddle[i - 6] = rightFace[i];
            rightFace[i] = tempInitial[i - 6];
        }
        // front layer:
        for (int i = 6; i < 9; i++) {
            tempInitial[i - 6] = frontFace[i];
            frontFace[i] = tempMiddle[i - 6];
        }
        rotateBottomFaceLeft();
    }

    private void rotateBottomFaceLeft() {
        int[] temp = Arrays.copyOf(bottomFace, bottomFace.length);
        for (int i = 0; i < 3; i++) {
            bottomFace[i] = temp[i * 3 + 2];
        }
        bottomFace[3] = temp[1];
        bottomFace[4] = temp[4];
        bottomFace[5] = temp[7];
        bottomFace[6] = temp[0];
        bottomFace[7] = temp[3];
        bottomFace[8] = temp[6];
    }

    public void rotateLeftLayerTop() {
        int[] tempInitial = { frontFace[0], frontFace[3], frontFace[6] };
        int[] tempMiddle = new int[3];
        // top layer:
        for (int i = 0; i < 3; i++) {
            tempMiddle[i] = topFace[i * 3];
            topFace[i * 3] = tempInitial[i];
        }
        // back layer:
        for (int i = 0; i < 3; i++) {
            tempInitial[i] = backFace[i * 3 + 2];
            backFace[i * 3 + 2] = tempMiddle[2 - i];
        }
        // bottom layer:
        for (int i = 0; i < 3; i++) {
            tempMiddle[i] = bottomFace[i * 3];
            bottomFace[i * 3] = tempInitial[2 - i];
        }
        // front layer:
        for (int i = 0; i < 3; i++) {
            tempInitial[i] = frontFace[i * 3];
            frontFace[i * 3] = tempMiddle[i];
        }

        // left layer:
        rotateLeftFaceTop();
    }

    private void rotateLeftFaceTop() {
        int[] temp = Arrays.copyOf(leftFace, leftFace.length);
        leftFace[0] = temp[2];
        leftFace[1] = temp[5];
        leftFace[2] = temp[8];
        leftFace[3] = temp[1];
        leftFace[4] = temp[4];
        leftFace[5] = temp[7];
        leftFace[6] = temp[0];
        leftFace[7] = temp[3];
        leftFace[8] = temp[6];
    }

    public void rotateLeftLayerBottom() {
        int[] tempInitial = { frontFace[0], frontFace[3], frontFace[6] };
        int[] tempMiddle = new int[3];
        // bottom layer:
        for (int i = 0; i < 3; i++) {
            tempMiddle[i] = bottomFace[i * 3];
            bottomFace[i * 3] = tempInitial[i];
        }
        // back layer:
        for (int i = 0; i < 3; i++) {
            tempInitial[i] = backFace[i * 3 + 2];
            backFace[i * 3 + 2] = tempMiddle[2 - i];
        }
        // top layer:
        for (int i = 0; i < 3; i++) {
            tempMiddle[i] = topFace[i * 3];
            topFace[i * 3] = tempInitial[2 - i];
        }
        // front layer:
        for (int i = 0; i < 3; i++) {
            tempInitial[i] = frontFace[i * 3];
            frontFace[i * 3] = tempMiddle[i];
        }

        // left layer:
        rotateLeftFaceBottom();
    }

    private void rotateLeftFaceBottom() {
        int[] temp = Arrays.copyOf(leftFace, leftFace.length);
        leftFace[0] = temp[6];
        leftFace[1] = temp[3];
        leftFace[2] = temp[0];
        leftFace[3] = temp[7];
        leftFace[4] = temp[4];
        leftFace[5] = temp[1];
        leftFace[6] = temp[8];
        leftFace[7] = temp[5];
        leftFace[8] = temp[2];
    }

    public void rotateMiddleLayerTop() {
        int[] tempInitial = { frontFace[1], frontFace[4], frontFace[7] };
        int[] tempMiddle = new int[3];
        // top layer:
        for (int i = 0; i < 3; i++) {
            tempMiddle[i] = topFace[i * 3 + 1];
            topFace[i * 3 + 1] = tempInitial[i];
        }
        // back layer:
        for (int i = 0; i < 3; i++) {
            tempInitial[i] = backFace[i * 3 + 1];
            backFace[i * 3 + 1] = tempMiddle[2 - i];
        }
        // bottom layer:
        for (int i = 0; i < 3; i++) {
            tempMiddle[i] = bottomFace[i * 3 + 1];
            bottomFace[i * 3 + 1] = tempInitial[2 - i];
        }
        // front layer:
        for (int i = 0; i < 3; i++) {
            tempInitial[i] = frontFace[i * 3 + 1];
            frontFace[i * 3 + 1] = tempMiddle[i];
        }
    }

    public void rotateMiddleLayerBottom() {
        int[] tempInitial = { frontFace[1], frontFace[4], frontFace[7] };
        int[] tempMiddle = new int[3];
        // bottom layer:
        for (int i = 0; i < 3; i++) {
            tempMiddle[i] = bottomFace[i * 3 + 1];
            bottomFace[i * 3 + 1] = tempInitial[i];
        }
        // back layer:
        for (int i = 0; i < 3; i++) {
            tempInitial[i] = backFace[i * 3 + 1];
            backFace[i * 3 + 1] = tempMiddle[2 - i];
        }
        // top layer:
        for (int i = 0; i < 3; i++) {
            tempMiddle[i] = topFace[i * 3 + 1];
            topFace[i * 3 + 1] = tempInitial[2 - i];
        }
        // front layer:
        for (int i = 0; i < 3; i++) {
            tempInitial[i] = frontFace[i * 3 + 1];
            frontFace[i * 3 + 1] = tempMiddle[i];
        }
    }

    public void rotateRightLayerTop() {
        int[] tempInitial = { frontFace[2], frontFace[5], frontFace[8] };
        int[] tempMiddle = new int[3];
        // top layer:
        for (int i = 0; i < 3; i++) {
            tempMiddle[i] = topFace[i * 3 + 2];
            topFace[i * 3 + 2] = tempInitial[i];
        }
        // back layer:
        for (int i = 0; i < 3; i++) {
            tempInitial[i] = backFace[i * 3];
            backFace[i * 3] = tempMiddle[2 - i];
        }
        // bottom layer:
        for (int i = 0; i < 3; i++) {
            tempMiddle[i] = bottomFace[i * 3 + 2];
            bottomFace[i * 3 + 2] = tempInitial[2 - i];
        }
        // front layer:
        for (int i = 0; i < 3; i++) {
            tempInitial[i] = frontFace[i * 3 + 2];
            frontFace[i * 3 + 2] = tempMiddle[i];
        }

        // left layer:
        rotateRightFaceTop();
    }

    private void rotateRightFaceTop() {
        int[] temp = Arrays.copyOf(rightFace, rightFace.length);
        rightFace[0] = temp[6];
        rightFace[1] = temp[3];
        rightFace[2] = temp[0];
        rightFace[3] = temp[7];
        rightFace[4] = temp[4];
        rightFace[5] = temp[1];
        rightFace[6] = temp[8];
        rightFace[7] = temp[5];
        rightFace[8] = temp[2];
    }

    public void rotateRightLayerBottom() {
        int[] tempInitial = { frontFace[2], frontFace[5], frontFace[8] };
        int[] tempMiddle = new int[3];
        // bottom layer:
        for (int i = 0; i < 3; i++) {
            tempMiddle[i] = bottomFace[i * 3 + 2];
            bottomFace[i * 3 + 2] = tempInitial[i];
        }
        // back layer:
        for (int i = 0; i < 3; i++) {
            tempInitial[i] = backFace[i * 3];
            backFace[i * 3] = tempMiddle[2 - i];
        }
        // top layer:
        for (int i = 0; i < 3; i++) {
            tempMiddle[i] = topFace[i * 3 + 2];
            topFace[i * 3 + 2] = tempInitial[2 - i];
        }
        // front layer:
        for (int i = 0; i < 3; i++) {
            tempInitial[i] = frontFace[i * 3 + 2];
            frontFace[i * 3 + 2] = tempMiddle[i];
        }

        // right layer:
        rotateRightFaceBottom();
    }

    private void rotateRightFaceBottom() {
        int[] temp = Arrays.copyOf(rightFace, rightFace.length);
        rightFace[0] = temp[2];
        rightFace[1] = temp[5];
        rightFace[2] = temp[8];
        rightFace[3] = temp[1];
        rightFace[4] = temp[4];
        rightFace[5] = temp[7];
        rightFace[6] = temp[0];
        rightFace[7] = temp[3];
        rightFace[8] = temp[6];
    }

    public void printFaces() {
        System.out.println("rightFace   : " + Arrays.toString(rightFace));
        System.out.println("backFace    : " + Arrays.toString(backFace));
        System.out.println("leftFace    : " + Arrays.toString(leftFace));
        System.out.println("frontFace   : " + Arrays.toString(frontFace));
        System.out.println("topFace     : " + Arrays.toString(topFace));
        System.out.println("bottomFace  : " + Arrays.toString(bottomFace));
    }

    public void fillButtonWithFrontFace(Button[] btns) {
        for (int i = 0; i < 9; i++) {
            if (frontFace[i] < 10) {
                btns[i].setText("0" + frontFace[i]);
            } else {
                btns[i].setText(String.valueOf(frontFace[i]));
            }
            btns[i].setTag(frontFace[i]);
            // btns[i].setBackgroundColor(Color.parseColor(arrayButtonColors.get(frontFace[i])));
            btns[i].setText("");
            btns[i].setBackgroundResource(getResoureIdByColor(arrayButtonColors.get(frontFace[i])));
        }
    }

    private int getResoureIdByColor(String color) {
        if ("CYAN".equals(color)) {
            return R.drawable.cyan;
        } else if ("BLUE".equals(color)) {
            return R.drawable.blue;
        } else if ("GREEN".equals(color)) {
            return R.drawable.green;
        } else if ("RED".equals(color)) {
            return R.drawable.red;
        } else if ("WHITE".equals(color)) {
            return R.drawable.white;
        } else if ("YELLOW".equals(color)) {
            return R.drawable.yellow;
        }
        return R.drawable.red;
    }

    private SparseArray<String> arrayButtonColors = new SparseArray<String>();
    private Random mRandom = new Random();

    public void initButtonColors(SharedPreferences prefs) {
        // if exist colors restore them, otherwise init them random
        for (int i = 0; i < faces.length; i++) {
            ArrayList<String> arrayColors = new ArrayList<String>();
            arrayColors.add("CYAN");
            arrayColors.add("BLUE");
            arrayColors.add("GREEN");
            arrayColors.add("RED");
            arrayColors.add("WHITE");
            arrayColors.add("YELLOW");
            arrayColors.add("RED");
            arrayColors.add("GREEN");
            arrayColors.add("BLUE");
            for (int j = 0; j < faces[i].length; j++) {
                int random = mRandom.nextInt(9);
                arrayButtonColors.put(faces[i][j], arrayColors.get(random % arrayColors.size()));
                arrayColors.remove(random % arrayColors.size());
            }
        }
        for (int i = 1; i <= arrayButtonColors.size(); i++) {
            Log.d(TAG,
                    "initButtonColors : arrayButtonColors[" + i + "] = " + arrayButtonColors.get(i));
            Editor editor = prefs.edit();
            editor.putString(String.valueOf(i), arrayButtonColors.get(i));
            editor.commit();
        }
    }

    public void restoreButtonColors(SharedPreferences prefs) {
        arrayButtonColors.clear();
        for (int i = 0; i < faces.length; i++) {
            for (int j = 0; j < faces[i].length; j++) {
                String color = prefs.getString(String.valueOf(faces[i][j]), "BLACK");
                arrayButtonColors.put(faces[i][j], color);
            }
        }

        for (int i = 1; i <= arrayButtonColors.size(); i++) {
            Log.d(TAG,
                    "restoreButtonColors : arrayButtonColors[" + i + "] = "
                            + arrayButtonColors.get(i));
        }

    }

    public String findColorByFaceId(int faceId) {
        return arrayButtonColors.get(faceId);
    }
}
