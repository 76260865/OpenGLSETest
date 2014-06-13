package com.tomatozq.opengl.rubik;

import org.join.ogles.lib.AppConfig;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

public class KubeSurfaceView extends GLSurfaceView {
    private float mPreviousX;
    private float mPreviousY;
    private float mDownX;
    private float mDownY;
    
    private KubeRenderer mRenderer;
    
	public KubeSurfaceView(Context context,KubeRenderer renderer) {
		super(context);
		// TODO Auto-generated constructor stub
		this.mRenderer = renderer;
		this.setRenderer(this.mRenderer);
		
	    //������ȾģʽΪ������Ⱦ   
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);   
	}

	 @Override public boolean onTouchEvent(MotionEvent e) {
	        float x = e.getX();
	        float y = e.getY();
	        AppConfig.setTouchPosition(x, y);
	        
	        if(!AppConfig.Turning){
	        	AppConfig.gbNeedPick = true;
	        }
	        
	        switch (e.getAction()) {
	        case MotionEvent.ACTION_DOWN:
	        	mRenderer.clearPickedCubes();
	        	mDownX = x;
	        	mDownY = y;
	        	break;
	        case MotionEvent.ACTION_MOVE:
	            // ���ƾ���
	            //float d = (float) (Math.sqrt(dx * dx + dy * dy));
		        //��X����ת
	            float dx = y - mPreviousY;
	            //��y����ת
	            float dy = x - mPreviousX;

	            mRenderer.offsetX = dx;
//	            mRenderer.offsetY = dy;
	            requestRender();
	            break;
	        case MotionEvent.ACTION_UP:
	            boolean direction = false;
	            
	        	if(Math.abs(x-mDownX) > Math.abs(y-mDownY)){
	            	if (x-mDownX>0) {
						direction = false;
					}
	            	else{
	            		direction = true;
	            	}
	            }
	            else{
	            	if (y-mDownY>0) {
						direction = true;
					}
	            	else{
	            		direction = false;
	            	}	            	
	            }
	            
	            mRenderer.offsetX = 0;
	            mRenderer.offsetY = 0;
	            
	            final boolean direct = direction;

	            //��ѡ�еķ��飬�ж�ħ��ת��
	            //���ݵ�ǰ��Ļ�Ӵ������������жϽӴ���
	            mRenderer.decideTurning(direct);

	            mDownX = 0;
	            mDownY = 0;
		        AppConfig.setTouchPosition(0, 0);
	        }
	        mPreviousX = x;
	        mPreviousY = y;
	        return true;
	    }
}
