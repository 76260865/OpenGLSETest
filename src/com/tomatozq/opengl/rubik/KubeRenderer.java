/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tomatozq.opengl.rubik;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import org.join.ogles.lib.AppConfig;
import org.join.ogles.lib.Matrix4f;
import org.join.ogles.lib.PickFactory;
import org.join.ogles.lib.Ray;
import org.join.ogles.lib.Vector3f;

import android.graphics.Paint;
import android.opengl.GLSurfaceView;
import android.os.SystemClock;

import com.tomatozq.opengl.spritetext.LabelMaker;
import com.tomatozq.opengl.spritetext.NumericSprite;
import com.tomatozq.opengl.spritetext.Projector;
import com.tomatozq.opengl.spritetext.Triangle;

/**
 * Example of how to use OpenGL|ES in a custom view
 */
class KubeRenderer implements GLSurfaceView.Renderer {
    private LabelMaker mLabels;
    private Paint mLabelPaint;
    private int mLabelMsPF;
    private NumericSprite mNumericSprite;
    private long mStartTime;
    private int mWidth,mHeight;    
    private int mFrames;
    private int mMsPerFrame;
    private final static int SAMPLE_PERIOD_FRAMES = 12;
    private final static float SAMPLE_FACTOR = 1.0f / SAMPLE_PERIOD_FRAMES;
    
    public interface AnimationCallback {
        void animate();
    }

    public KubeRenderer(GLWorld world, AnimationCallback callback) {
        mWorld = world;
        mCallback = callback;
        
        mLabelPaint = new Paint();
        mLabelPaint.setTextSize(32);
        mLabelPaint.setAntiAlias(true);
        mLabelPaint.setARGB(0xff, 0x00, 0x00, 0x00);
    }
    
	private Vector3f mvEye = new Vector3f(0, 0, 7);
	private Vector3f mvCenter = new Vector3f(0, 0, 0);
	private Vector3f mvUp = new Vector3f(0, 1, 0);
	
	private void rotate(GL10 gl){
		//����
//      gl.glScalef(0.5f, 0.5f, 0.5f);
//		Matrix4f matScale = new Matrix4f();
//		matScale.scale(0.5f,0.5f, 0.5f);
//		AppConfig.gMatModel.mul(matScale);

//      gl.glRotatef(mAngle,        0, 1, 0);
//      gl.glRotatef(mAngle*0.25f,  1, 0, 0);
        //������ת
	   	Matrix4f matRotX = new Matrix4f();
		matRotX.setIdentity();
		matRotX.rotX((float) (mAngleX * Math.PI / 180));
		AppConfig.gMatModel.mul(matRotX);
		
		Matrix4f matRotY = new Matrix4f();
		matRotY.setIdentity();
		matRotY.rotY((float) (mAngleY * Math.PI / 180));
		AppConfig.gMatModel.mul(matRotY);
		
		gl.glMultMatrixf(AppConfig.gMatModel.asFloatBuffer());
	}
	
	private boolean touchInCubeSphere(){
		//�Ƿ����ڷ���������
		PickFactory.update(AppConfig.gScreenX, AppConfig.gScreenY);
		// ������µ�ʰȡ����
		Ray ray = PickFactory.getPickRay();
		
		Ray transformedRay = new Ray();
		
		// ���������������ཻ������ת
		Matrix4f matInvertModel = new Matrix4f();
		matInvertModel.set(AppConfig.gMatModel);
		matInvertModel.invert();
		// �����߱任��ģ������ϵ�У��ѽ���洢��transformedRay��
		ray.transform(matInvertModel, transformedRay);
		
		return transformedRay.intersectSphere(mWorld.worldCenter,mWorld.worldRadius);
	}
	
    public void onDrawFrame(GL10 gl) {
    	if (mCallback!=null) {
    		mCallback.animate();
		}
    	
        gl.glClearColor(0.5f,0.5f,0.5f,1);
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        /*
         * Now we're ready to draw some 3D object
         */
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
        
        //ϵͳapi����
//        gl.glTranslatef(0, 0, -3.0f);
		Matrix4f.gluLookAt(mvEye,mvCenter,mvUp, AppConfig.gMatView);
		gl.glLoadMatrixf(AppConfig.gMatView.asFloatBuffer());
		
		AppConfig.gMatModel.setIdentity();
				
		if (AppConfig.gbNeedPick && !touchInCubeSphere()) {
			mAngleX += offsetX;
			mAngleY += offsetY;
//			Log.i("GLWorld", mAngleX + "," + mAngleY);
		}
		
		gl.glPushMatrix();
		rotate(gl);
        mWorld.draw(gl);
		gl.glPopMatrix();
		
        mWorld.intersectDetect();
        
		gl.glPushMatrix();
        mWorld.drawPickedTriangle(gl);
		gl.glPopMatrix();
		
		//д����
        mLabels.beginDrawing(gl, mWidth, mHeight);
        float msPFX = mWidth - mLabels.getWidth(mLabelMsPF) - 1;
        mLabels.draw(gl, msPFX, 0, mLabelMsPF);
        mLabels.endDrawing(gl);

        drawMsPF(gl, msPFX);
    }
    
    private void drawMsPF(GL10 gl, float rightMargin) {
        long time = SystemClock.uptimeMillis();
        if (mStartTime == 0) {
            mStartTime = time;
        }
        if (mFrames++ == SAMPLE_PERIOD_FRAMES) {
            mFrames = 0;
            long delta = time - mStartTime;
            mStartTime = time;
            mMsPerFrame = (int) (delta * SAMPLE_FACTOR);
        }
        if (mMsPerFrame > 0) {
            mNumericSprite.setValue(mMsPerFrame);
            float numWidth = mNumericSprite.width();
            float x = rightMargin - numWidth;
            mNumericSprite.draw(gl, x, 0, mWidth, mHeight);
        }
    }
	
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        gl.glViewport(0, 0, width, height);

		AppConfig.gpViewport[0] = 0;
		AppConfig.gpViewport[1] = 0;
		AppConfig.gpViewport[2] = width;
		AppConfig.gpViewport[3] = height;
		
		mWidth = width;
		mHeight = height;
		
        /*
         * Set our projection matrix. This doesn't have to be done
         * each time we draw, but usually a new projection needs to be set
         * when the viewport is resized.
         */
        float ratio = (float)width / height;
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        
        //GLU.gluPerspective(gl, 45f, ratio, 2,12);		
        
		//��Ϊ�йܾ�������
		Matrix4f.gluPersective(45.0f, ratio, 0.1f, 100, AppConfig.gMatProject);
		gl.glLoadMatrixf(AppConfig.gMatProject.asFloatBuffer());
		AppConfig.gMatProject.fillFloatArray(AppConfig.gpMatrixProjectArray);

		// Select the modelview matrix
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		// Reset the modelview matrix
		gl.glLoadIdentity();

        gl.glDisable(GL10.GL_DITHER);
//      gl.glActiveTexture(GL10.GL_TEXTURE0);
        
        mWorld.createCubeImage();
    }
    
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
    	// Enable Smooth Shading, default not really needed.
		gl.glShadeModel(GL10.GL_SMOOTH);
		// Depth buffer setup.
//		gl.glClearDepthf(1.0f);
		// Enables depth testing.
//		gl.glEnable(GL10.GL_DEPTH_TEST);
		// The type of depth testing to do.
//		gl.glDepthFunc(GL10.GL_LEQUAL);
		// Really nice perspective calculations.
//		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
    			
		AppConfig.gMatModel.setIdentity();
		
		//�������ִ���
        if (mLabels != null) {
            mLabels.shutdown(gl);
        } else {
            mLabels = new LabelMaker(true, 256, 64);
        }
        
        mLabels.initialize(gl);
        mLabels.beginAdding(gl);
        
        mLabelMsPF = mLabels.add(gl, "ms/frame", mLabelPaint);
        mLabels.endAdding(gl);

        if (mNumericSprite != null) {
            mNumericSprite.shutdown(gl);
        } else {
            mNumericSprite = new NumericSprite();
        }
        mNumericSprite.initialize(gl, mLabelPaint);
    }

    private GLWorld mWorld;
    private AnimationCallback mCallback;
	private float mAngleX;
	private float mAngleY;
	
	public float offsetX;
	public float offsetY;

	public void decideTurning(boolean direction) {
		// TODO Auto-generated method stub
		KubeActivity kubeAct = (KubeActivity)mCallback;
		kubeAct.turningDirection = direction;
		mWorld.decideTurning(kubeAct);
	}
	
	public void clearPickedCubes(){
		mWorld.clearPickedCubes();
	}
}


