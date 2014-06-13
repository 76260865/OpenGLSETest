package com.tomatozq.opengl.tutorial;

import com.tomatozq.opengl.R;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class TutorialActivity extends Activity implements OnSurfacePickedListener {
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			Toast.makeText(TutorialActivity.this, "选中了" + msg.what + "面",Toast.LENGTH_SHORT).show();
		}
	};
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

 		Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pre5);
 		
        TouchSurfaceView view = new TouchSurfaceView(this,new OpenGLRenderer(bitmap,this));
   		setContentView(view);
    }

	@Override
	public void onSurfacePicked(int which) {
		// TODO Auto-generated method stub
		handler.sendEmptyMessage(which);
	}
}
