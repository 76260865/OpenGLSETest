package com.tomatozq.opengl.tutorial;

import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import javax.microedition.khronos.opengles.GL11Ext;

import org.join.ogles.lib.AppConfig;
import org.join.ogles.lib.IBufferFactory;
import org.join.ogles.lib.Matrix4f;
import org.join.ogles.lib.PickFactory;
import org.join.ogles.lib.Ray;
import org.join.ogles.lib.Vector3f;

import com.tomatozq.opengl.tutorial.mesh.Cube;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.opengl.GLUtils;
import android.opengl.GLSurfaceView.Renderer;
import android.util.Log;

public class OpenGLRenderer implements Renderer {
	private Cube cube;
	private OnSurfacePickedListener onSurfacePickedListener;
	public float mAngleX = 0;
	public float mAngleY = 0;
	public float gesDistance = 0;
	
	private FloatBuffer mBufPickedTriangle = IBufferFactory.newFloatBuffer(3 * 3);
	
	public OpenGLRenderer(Bitmap bitmap,OnSurfacePickedListener onSurfacePickedListener) {
		// Initialize our cube.
//		Group group = new Group();
		cube = new Cube(1, 1, 1);
		cube.loadBitmap(bitmap);
		
		this.onSurfacePickedListener = onSurfacePickedListener;
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * android.opengl.GLSurfaceView.Renderer#onSurfaceCreated(javax.microedition
	 * .khronos.opengles.GL10, javax.microedition.khronos.egl.EGLConfig)
	 */
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		// Set the background color to black ( rgba ).
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);
		// Enable Smooth Shading, default not really needed.
		gl.glShadeModel(GL10.GL_SMOOTH);
		// Depth buffer setup.
		gl.glClearDepthf(1.0f);
		// Enables depth testing.
		gl.glEnable(GL10.GL_DEPTH_TEST);
		// The type of depth testing to do.
		gl.glDepthFunc(GL10.GL_LEQUAL);
		// Really nice perspective calculations.
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
		
		// ���ñ������
		gl.glEnable(GL10.GL_CULL_FACE);
		gl.glCullFace(GL10.GL_BACK);
		
		//ģ�;�����Ϊ��λ����
		AppConfig.gMatModel.setIdentity();
	}

	private Vector3f mvEye = new Vector3f(0, 0, 4);
	private Vector3f mvCenter = new Vector3f(0, 0, 0);
	private Vector3f mvUp = new Vector3f(0, 1, 0);

	/**
	 * ���·�ʽ��ȫ�������Ʒ�����ת
	 * ����ֻ����ĳһ������ת
	 * @param gl
	 */
	private void rotate1(GL10 gl){
		Matrix4f matRot = new Matrix4f();
		Matrix4f matInvertModel = new Matrix4f();

		matRot.setIdentity();

		// ��������ϵ��������
		Vector3f point = new Vector3f(mAngleX, mAngleY, 0);

		try {
			// ת����ģ���ڲ��ĵ㣬��Ҫ����
			matInvertModel.set(AppConfig.gMatModel);
			matInvertModel.invert();
			matInvertModel.transform(point, point);

			float d = Vector3f.distance(new Vector3f(), point);

			// �ټ������
			if (Math.abs(d - gesDistance) <= 1E-5) {
				// �������λ������ת�����������ܻ�������Ŷ�ʹ��ģ����ʧ������
				matRot.glRotatef((float) (gesDistance * Math.PI / 180), point.x / d, point.y / d, point.z / d);

				// ��ת����ԭ��������ת
				if (0 != gesDistance) {
					AppConfig.gMatModel.mul(matRot);
				}
			}
		} catch (Exception e) {
			// �������������������ʧ��
		}
		
		gesDistance = 0;

		gl.glMultMatrixf(AppConfig.gMatModel.asFloatBuffer());
	}
	
	private void rotate2(GL10 gl){
			//TOUCH_SCALE_FACTOR
			Matrix4f matRotX = new Matrix4f();
			Matrix4f matRotY = new Matrix4f();
			
			matRotX.setIdentity();
			matRotX.rotX((float) (mAngleX * Math.PI / 180));
			
			matRotY.setIdentity();
			matRotY.rotY((float) (mAngleY * Math.PI / 180));
			
			AppConfig.gMatModel.set(matRotX);
			AppConfig.gMatModel.mul(matRotY);
			
			gl.glMultMatrixf(AppConfig.gMatModel.asFloatBuffer());
	}
	
	private Ray transformedRay = new Ray();
	
	/**
	 * ����ʰȡ�¼�
	 */
	private void pickDetect() {
		if (!AppConfig.gbNeedPick) {
			return;
		}
		
		AppConfig.gbNeedPick = false;
		// �������µ�ʰȡ����
		PickFactory.update(AppConfig.gScreenX, AppConfig.gScreenY);
		// ������µ�ʰȡ����
		Ray ray = PickFactory.getPickRay();

		Vector3f transformedSphereCenter = new Vector3f();
		// ���Ȱ�ģ�͵İ���ͨ��ģ�;�����ģ�;ֲ��ռ�任������ռ�
		AppConfig.gMatModel.transform(cube.getSphereCenter(),transformedSphereCenter);

		// ��������������ı��Ϊ��
		cube.surface = -1;

		// ���ȼ��ʰȡ�����Ƿ���ģ�Ͱ������ཻ
		// ������ܿ죬���Կ����ų�����Ҫ�ľ�ȷ�ཻ���
		if (ray.intersectSphere(transformedSphereCenter, cube.getSphereRadius())) {
			// ���������������ཻ����ô����Ҫ���о�ȷ�������漶����ཻ���
			// �������ǵ�ģ����Ⱦ���ݣ�������ģ�;ֲ�����ϵ��
			// ��ʰȡ����������������ϵ��
			// �����Ҫ������ת����ģ������ϵ��
			// �������ȼ���ģ�;���������
			Matrix4f matInvertModel = new Matrix4f();
			matInvertModel.set(AppConfig.gMatModel);
			matInvertModel.invert();
			
			// �����߱任��ģ������ϵ�У��ѽ���洢��transformedRay��
			ray.transform(matInvertModel, transformedRay);
			
			Vector3f[] mpTriangle = { new Vector3f(), new Vector3f(),new Vector3f() };
			
			// ��������ģ������ȷ�ཻ���
			if (cube.intersect(transformedRay, mpTriangle)) {
				// ����ҵ����ཻ�������������
				AppConfig.gbTrianglePicked = true;
				// ��������һ����
				Log.i("��������������", "=���=" + cube.surface);
				// �ص�
				if (null != onSurfacePickedListener) {
					onSurfacePickedListener.onSurfacePicked(cube.surface);
				}
				// ������ݵ���ѡȡ�����ε���Ⱦ������
				mBufPickedTriangle.clear();
				for (int i = 0; i < 3; i++) {
					IBufferFactory.fillBuffer(mBufPickedTriangle, mpTriangle[i]);
				}
				mBufPickedTriangle.position(0);
			}
			} else {
				AppConfig.gbTrianglePicked = false;
			}
	}
	
	/**
	 * ��Ⱦѡ�е�������
	 */
	private void drawPickedTriangle(GL10 gl) {
		if (!AppConfig.gbTrianglePicked) {
			return;
		}
		// ���ڷ��ص�ʰȡ�����������ǳ���ģ������ϵ��
		// �����Ҫ����ģ�ͱ任�������Ǳ任����������ϵ�н�����Ⱦ
		// ����ģ�ͱ任����
		gl.glMultMatrixf(AppConfig.gMatModel.asFloatBuffer());
		// ������������ɫ��alphaΪ0.7
		gl.glColor4f(1.0f, 0.0f, 0.0f, 0.7f);
		// ����Blend���ģʽ
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		// �����޹����ԣ�����ʹ�ô�ɫ���
		gl.glDisable(GL10.GL_DEPTH_TEST);
		gl.glDisable(GL10.GL_TEXTURE_2D);
		// ��ʼ����Ⱦ��������
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mBufPickedTriangle);
		// �ύ��Ⱦ
		gl.glDrawArrays(GL10.GL_TRIANGLES, 0, 3);
		// �����������
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glDisable(GL10.GL_BLEND);
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * android.opengl.GLSurfaceView.Renderer#onDrawFrame(javax.microedition.
	 * khronos.opengles.GL10)
	 */
	public void onDrawFrame(GL10 gl) {
		// Clears the screen and depth buffer.
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		// Replace the current matrix with the identity matrix
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();

//		GLU.gluLookAt(gl, 0, 0, 5, 0, 0, 0, 0, 1, 0);
		Matrix4f.gluLookAt(mvEye,mvCenter,mvUp, AppConfig.gMatView);
		gl.glLoadMatrixf(AppConfig.gMatView.asFloatBuffer());

		gl.glPushMatrix();
		rotate2(gl);
		cube.draw(gl);
		gl.glPopMatrix();
		
		pickDetect();
		
		gl.glPushMatrix();
		drawPickedTriangle(gl);
		gl.glPopMatrix();
		
		//�����·���Ļ��ֱ����ͼ 
		drawTextInScreen(gl);
	}
	
	private int mTextureID = -1;
	private int imageWidth;
	private int imageHeight;
	
	private void drawTextInScreen(GL10 gl){
		if(mTextureID==-1){
	    	int fontSize = 32;
	    	//�������塢�����С��������ɫ
	    	Paint p = new Paint();
	    	String familyName = "Times New Roman";
	    	Typeface font = Typeface.create(familyName, Typeface.NORMAL);
	    	p.setColor(Color.RED);
	    	p.setTypeface(font);
	    	p.setTextSize(fontSize);
	    	
	    	//��Bitmap�ϻ�������
	    	String text = "��ת����";
	    	int textWidth = (int) Math.ceil(p.measureText(text));
            int textHeight = (int) Math.ceil(-p.ascent()) + (int) Math.ceil(p.descent());
            
            imageWidth = textWidth;
            imageHeight = textHeight + 10;
            
			Bitmap bitmap = Bitmap.createBitmap(imageWidth, imageHeight, Bitmap.Config.ARGB_8888); 
	    	
	    	Canvas canvas = new Canvas(bitmap);
	    	canvas.drawText(text,0,textHeight, p); 
	    	
			int[] textures = new int[1];
	        gl.glGenTextures(1, textures, 0);
	        mTextureID = textures[0];
	        gl.glBindTexture(GL10.GL_TEXTURE_2D, mTextureID);
	
	        // Use Nearest for performance.
	        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,GL10.GL_NEAREST);
	        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,GL10.GL_NEAREST);
	        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S,GL10.GL_CLAMP_TO_EDGE);
	        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T,GL10.GL_CLAMP_TO_EDGE);
	
	        //GL10.GL_MODULATE
	        gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE,GL10.GL_REPLACE);
	        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0); 
		}

        gl.glEnable(GL10.GL_TEXTURE_2D);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, mTextureID);
        int[] crop = {0, imageHeight, imageWidth, -imageHeight};

        ((GL11)gl).glTexParameteriv(GL10.GL_TEXTURE_2D,GL11Ext.GL_TEXTURE_CROP_RECT_OES,crop, 0);
        
        //������ֱ�ӻ�����Ļ��ĳλ��
        ((GL11Ext)gl).glDrawTexiOES((AppConfig.gpViewport[2] - imageWidth)/2, 10 , 0,imageWidth, imageHeight);
        
        gl.glDisable(GL10.GL_TEXTURE_2D);
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * android.opengl.GLSurfaceView.Renderer#onSurfaceChanged(javax.microedition
	 * .khronos.opengles.GL10, int, int)
	 */
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		// Sets the current view port to the new size.
		gl.glViewport(0, 0, width, height);
		
		AppConfig.gpViewport[0] = 0;
		AppConfig.gpViewport[1] = 0;
		AppConfig.gpViewport[2] = width;
		AppConfig.gpViewport[3] = height;
		
		// Select the projection matrix
		gl.glMatrixMode(GL10.GL_PROJECTION);
		// Reset the projection matrix
		gl.glLoadIdentity();
		// Calculate the aspect ratio of the window
		float ratio = (float) width / (float) height;
		
//		GLU.gluPerspective(gl, 45f, ratio, 0.1f,100.0f);
		
		//��Ϊ�йܾ�������
		Matrix4f.gluPersective(45.0f, ratio, 0.1f, 100, AppConfig.gMatProject);
		gl.glLoadMatrixf(AppConfig.gMatProject.asFloatBuffer());
		AppConfig.gMatProject.fillFloatArray(AppConfig.gpMatrixProjectArray);
		
		//�����ӽ�
//		gl.glFrustumf(-ratio, ratio, -1, 1, 0.5f, 5);//�����ӽ�
		
		//����ͶӰ
//		gl.glOrthof(-2,2,-2,2,-2,2); 

		// Select the modelview matrix
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		// Reset the modelview matrix
		gl.glLoadIdentity();		
	}
}
