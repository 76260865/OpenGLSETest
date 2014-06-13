package com.tomatozq.opengl.tutorial;

import org.join.ogles.lib.AppConfig;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;
import android.view.GestureDetector.OnGestureListener;

public class TouchSurfaceView extends GLSurfaceView implements OnGestureListener{
//    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
//    private final float TRACKBALL_SCALE_FACTOR = 36.0f;
    private OpenGLRenderer mRenderer;
    private float mPreviousX;
    private float mPreviousY;
    
	public TouchSurfaceView(Context context,OpenGLRenderer renderer) {
		super(context);
		// TODO Auto-generated constructor stub
		this.mRenderer = renderer;
		setRenderer(renderer);
		// setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
	}

    @Override 
    public boolean onTouchEvent(MotionEvent e) {
        float x = e.getX();
        float y = e.getY();
        AppConfig.setTouchPosition(x, y);
        AppConfig.gbNeedPick = true;
        
        switch (e.getAction()) {
        case MotionEvent.ACTION_MOVE:
        	//»∆X÷·–˝◊™
            float dx = y - mPreviousY;
            //»∆y÷·–˝◊™
            float dy = x - mPreviousX;
            
            //  ÷ ∆æ‡¿Î
            float d = (float) (Math.sqrt(dx * dx + dy * dy));
            
            mRenderer.mAngleX += dx;
            mRenderer.mAngleY += dy;
            mRenderer.gesDistance = d;
            
            requestRender();
        }
        mPreviousX = x;
        mPreviousY = y;
        return true;
    }

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		return false;
	}
}
