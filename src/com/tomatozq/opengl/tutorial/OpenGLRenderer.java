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
		
		// 启用背面剪裁
		gl.glEnable(GL10.GL_CULL_FACE);
		gl.glCullFace(GL10.GL_BACK);
		
		//模型矩阵设为单位矩阵
		AppConfig.gMatModel.setIdentity();
	}

	private Vector3f mvEye = new Vector3f(0, 0, 4);
	private Vector3f mvCenter = new Vector3f(0, 0, 0);
	private Vector3f mvUp = new Vector3f(0, 1, 0);

	/**
	 * 以下方式完全按照手势方向旋转
	 * 避免只按照某一方向旋转
	 * @param gl
	 */
	private void rotate1(GL10 gl){
		Matrix4f matRot = new Matrix4f();
		Matrix4f matInvertModel = new Matrix4f();

		matRot.setIdentity();

		// 世界坐标系的向量点
		Vector3f point = new Vector3f(mAngleX, mAngleY, 0);

		try {
			// 转换到模型内部的点，先要求逆
			matInvertModel.set(AppConfig.gMatModel);
			matInvertModel.invert();
			matInvertModel.transform(point, point);

			float d = Vector3f.distance(new Vector3f(), point);

			// 再减少误差
			if (Math.abs(d - gesDistance) <= 1E-5) {
				// 绕这个单位向量旋转（由于误差可能会产生缩放而使得模型消失不见）
				matRot.glRotatef((float) (gesDistance * Math.PI / 180), point.x / d, point.y / d, point.z / d);

				// 旋转后在原基础上再转
				if (0 != gesDistance) {
					AppConfig.gMatModel.mul(matRot);
				}
			}
		} catch (Exception e) {
			// 由于四舍五入求逆矩阵失败
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
	 * 更新拾取事件
	 */
	private void pickDetect() {
		if (!AppConfig.gbNeedPick) {
			return;
		}
		
		AppConfig.gbNeedPick = false;
		// 更新最新的拾取射线
		PickFactory.update(AppConfig.gScreenX, AppConfig.gScreenY);
		// 获得最新的拾取射线
		Ray ray = PickFactory.getPickRay();

		Vector3f transformedSphereCenter = new Vector3f();
		// 首先把模型的绑定球通过模型矩阵，由模型局部空间变换到世界空间
		AppConfig.gMatModel.transform(cube.getSphereCenter(),transformedSphereCenter);

		// 触碰的立方体面的标记为无
		cube.surface = -1;

		// 首先检测拾取射线是否与模型绑定球发生相交
		// 这个检测很快，可以快速排除不必要的精确相交检测
		if (ray.intersectSphere(transformedSphereCenter, cube.getSphereRadius())) {
			// 如果射线与绑定球发生相交，那么就需要进行精确的三角面级别的相交检测
			// 由于我们的模型渲染数据，均是在模型局部坐标系中
			// 而拾取射线是在世界坐标系中
			// 因此需要把射线转换到模型坐标系中
			// 这里首先计算模型矩阵的逆矩阵
			Matrix4f matInvertModel = new Matrix4f();
			matInvertModel.set(AppConfig.gMatModel);
			matInvertModel.invert();
			
			// 把射线变换到模型坐标系中，把结果存储到transformedRay中
			ray.transform(matInvertModel, transformedRay);
			
			Vector3f[] mpTriangle = { new Vector3f(), new Vector3f(),new Vector3f() };
			
			// 将射线与模型做精确相交检测
			if (cube.intersect(transformedRay, mpTriangle)) {
				// 如果找到了相交的最近的三角形
				AppConfig.gbTrianglePicked = true;
				// 触碰了哪一个面
				Log.i("触碰的立方体面", "=标记=" + cube.surface);
				// 回调
				if (null != onSurfacePickedListener) {
					onSurfacePickedListener.onSurfacePicked(cube.surface);
				}
				// 填充数据到被选取三角形的渲染缓存中
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
	 * 渲染选中的三角形
	 */
	private void drawPickedTriangle(GL10 gl) {
		if (!AppConfig.gbTrianglePicked) {
			return;
		}
		// 由于返回的拾取三角形数据是出于模型坐标系中
		// 因此需要经过模型变换，将它们变换到世界坐标系中进行渲染
		// 设置模型变换矩阵
		gl.glMultMatrixf(AppConfig.gMatModel.asFloatBuffer());
		// 设置三角形颜色，alpha为0.7
		gl.glColor4f(1.0f, 0.0f, 0.0f, 0.7f);
		// 开启Blend混合模式
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		// 禁用无关属性，仅仅使用纯色填充
		gl.glDisable(GL10.GL_DEPTH_TEST);
		gl.glDisable(GL10.GL_TEXTURE_2D);
		// 开始绑定渲染顶点数据
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mBufPickedTriangle);
		// 提交渲染
		gl.glDrawArrays(GL10.GL_TRIANGLES, 0, 3);
		// 重置相关属性
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
		
		//在最下方屏幕上直接贴图 
		drawTextInScreen(gl);
	}
	
	private int mTextureID = -1;
	private int imageWidth;
	private int imageHeight;
	
	private void drawTextInScreen(GL10 gl){
		if(mTextureID==-1){
	    	int fontSize = 32;
	    	//设置字体、字体大小和字体颜色
	    	Paint p = new Paint();
	    	String familyName = "Times New Roman";
	    	Typeface font = Typeface.create(familyName, Typeface.NORMAL);
	    	p.setColor(Color.RED);
	    	p.setTypeface(font);
	    	p.setTextSize(fontSize);
	    	
	    	//在Bitmap上绘制文字
	    	String text = "旋转方块";
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
        
        //将纹理直接画到屏幕中某位置
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
		
		//改为托管矩阵运行
		Matrix4f.gluPersective(45.0f, ratio, 0.1f, 100, AppConfig.gMatProject);
		gl.glLoadMatrixf(AppConfig.gMatProject.asFloatBuffer());
		AppConfig.gMatProject.fillFloatArray(AppConfig.gpMatrixProjectArray);
		
		//设置视角
//		gl.glFrustumf(-ratio, ratio, -1, 1, 0.5f, 5);//设置视角
		
		//正则投影
//		gl.glOrthof(-2,2,-2,2,-2,2); 

		// Select the modelview matrix
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		// Reset the modelview matrix
		gl.glLoadIdentity();		
	}
}
