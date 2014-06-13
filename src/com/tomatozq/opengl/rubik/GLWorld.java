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

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.microedition.khronos.opengles.GL10;

import org.join.ogles.lib.AppConfig;
import org.join.ogles.lib.IBufferFactory;
import org.join.ogles.lib.Matrix4f;
import org.join.ogles.lib.PickFactory;
import org.join.ogles.lib.Ray;
import org.join.ogles.lib.Vector3f;
import org.join.ogles.lib.Vector4f;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.Log;

public class GLWorld {
	private FloatBuffer mBufPickedTriangle = IBufferFactory.newFloatBuffer(3 * 3);
	private Vector3f[] mpTriangle = { new Vector3f(), new Vector3f(),new Vector3f()};
	private CopyOnWriteArrayList<Cube> pickedList = new CopyOnWriteArrayList<Cube>();
	
	public void createCubeImage(){
		for(Cube cube : mShapeList){
	    	int imgSize = 64;
	    	int fontSize = 20;
	    	
	    	Bitmap bitmap = Bitmap.createBitmap(imgSize, imgSize, Bitmap.Config.ARGB_8888); 
	    	
	    	Canvas canvas = new Canvas(bitmap);

	    	canvas.drawColor(Color.WHITE);
	    	Paint p = new Paint();
	
	    	//�������塢�����С��������ɫ
	    	String familyName = "Times New Roman";
	    	Typeface font = Typeface.create(familyName, Typeface.NORMAL);
	    	p.setColor(Color.BLACK);
	    	p.setTypeface(font);
	    	p.setTextSize(fontSize);
	    	
	    	//��Bitmap�ϻ�������
	    	String text = cube.id;
	    	float textWidth = p.measureText(text);
	    	canvas.drawText(cube.id,(imgSize - textWidth)/2,imgSize - fontSize, p); 
	    	
	    	cube.loadBitmap(bitmap);
		}
	}
	
	private void addPickedList(Cube cube){
		boolean has = false;
		
		for (Cube _cube : pickedList) {
			if (_cube.id.equals(cube.id)) {
				has = true;
				break;
			}
		}
		
		if (!has) {
			pickedList.add(cube);
		}
	}
	
	public void clearPickedCubes(){
		Log.d("GLWorld", "���ѡȡ�б�");
		pickedList.clear();
		
		for (int i = 0; i < mpTriangle.length; i++) {
			mpTriangle[i] = new Vector3f();
		}
	}
	
	public void addShape(Cube shape) {
		mShapeList.add(shape);
	}
	
	public void generate() {		
		Iterator<Cube> iter3 = mShapeList.iterator();
		while (iter3.hasNext()) {
			GLShape shape = iter3.next();
			shape.generate();
		}
	}
		
	//int count = 0;
    public void draw(GL10 gl)
    {      
    	gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

	    /*
	     * GL_SMOOTH�� OpenGL�����ݶ������ɫ����ֵ�������������ɫ���γ���ɫ�䡢���ɵ�Ч����
 		   GL_FLAT�� û����ɫ����͹��ɵ�Ч�������磺�������ε�����ɫ��ȡ���һ���������ɫ��������Ρ�
	     * */
//	    gl.glShadeModel(GL10.GL_FLAT);
//	    gl.glShadeModel(GL10.GL_SMOOTH);
	    gl.glEnable(GL10.GL_CULL_FACE);
		gl.glFrontFace(GL10.GL_CCW);
	    gl.glCullFace(GL10.GL_BACK);
		gl.glEnable(GL10.GL_DEPTH_TEST); //���ử����ס��ͼ�β���
	    
        for(int i=0;i<mShapeList.size();i++) {
        	GLShape shape = mShapeList.get(i);
			shape.draw(gl);
		}
        
        gl.glDisable(GL10.GL_DEPTH_TEST);
        gl.glDisableClientState(GL10.GL_CULL_FACE);
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
    }
    
    /**
	 * ��Ⱦѡ�е�������
	 */
	public void drawPickedTriangle(GL10 gl) {
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
		
	    gl.glEnable(GL10.GL_CULL_FACE);
	    //ע���������򽻼��ĵ�˳�����йأ����򻭵������޷�����!
		gl.glFrontFace(GL10.GL_CW);
	    gl.glCullFace(GL10.GL_BACK);
		
		// ��ʼ����Ⱦ��������
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
	
		mBufPickedTriangle.position();
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mBufPickedTriangle);
		// �ύ��Ⱦ
		gl.glDrawArrays(GL10.GL_TRIANGLES, 0, 3);
		// �����������
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glDisable(GL10.GL_BLEND);
		gl.glDisable(GL10.GL_CULL_FACE);
	}
	
    static public float toFloat(int x) {
    	return x/65536.0f;
    }

	private ArrayList<Cube>	mShapeList = new ArrayList<Cube>();	
	    
	private Vector4f location = new Vector4f();
	public Vector3f worldCenter = new Vector3f(0, 0, 0); 
	public final float worldRadius = 1.7f;
	
	/**
	 * ������ģ�͵ľ�ȷ��ײ���
	 * @param ray - ת����ģ�Ϳռ��е�����
	 * @param trianglePosOut - ���ص�ʰȡ��������ζ���λ��
	 * @return ����ཻ������true
	 */
	public boolean intersectDetect() {
		if (!AppConfig.gbNeedPick || AppConfig.Turning) {
			return false;
		}
		
		AppConfig.gbNeedPick = false;
		// �������µ�ʰȡ����
		PickFactory.update(AppConfig.gScreenX, AppConfig.gScreenY);
		// ������µ�ʰȡ����
		Ray ray = PickFactory.getPickRay();
		
		boolean bFound = false;
		// �洢������ԭ�����������ཻ��ľ���
		// �������������������������һ��
		float closeDis = 0.0f;

		Vector3f v0, v1, v2;
		
		Ray transformedRay = new Ray();
		Cube mpCube = null;

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
		
		// ���Ȱ�ģ�͵İ���ͨ��ģ�;�����ģ�;ֲ��ռ�任������ռ�
//		Vector3f transformedSphereCenter = new Vector3f();
//	    AppConfig.gMatModel.transform(new Vector3f(0, 0, 0),transformedSphereCenter);
	    
	    long begin = System.currentTimeMillis();
	    
		if (transformedRay.intersectSphere(worldCenter,worldRadius)) {
			//26�����飬���м䲻��ʾ
			int cubeCount = mShapeList.size();
//			Vector3f _transformedSphereCenter = new Vector3f();
			
			for (int c = 0; c < cubeCount; c++) {
				Cube cube = (Cube)mShapeList.get(c);
//			    AppConfig.gMatModel.transform(cube.getSphereCenter(),_transformedSphereCenter);
			    if (transformedRay.intersectSphere(cube.getSphereCenter(),cube.getSphereRadius())) {
					// ������6����
					for (int i = 0; i < cube.mFaceList.size(); i++) {
						GLFace face = cube.mFaceList.get(i);
						
						//��ɫ��Ϊ���ɼ�������Ҫ�ж�
						if (face.getColor().equals(GLColor.BLACK)) {
							continue;
						}
						
						// ÿ��������������
						for (int j = 0; j < 2; j++) {
							if(j==0){
								//1 2
						        //0 3
								//˳ʱ�뷽��Ϊ 0 1 3->��ʱ�뷽��Ϊ0,3,1
								v0 = getVector3f(face.getVertex(0));
								v1 = getVector3f(face.getVertex(1));
								v2 = getVector3f(face.getVertex(3));
							}
							else{
								v0 = getVector3f(face.getVertex(1));
								v1 = getVector3f(face.getVertex(2));
								v2 = getVector3f(face.getVertex(3));			
							}
							
							// ����ת��������ߺ������е���ײ���
							if (transformedRay.intersectTriangle(v0, v1, v2, location)) {
							    
								// ����������ཻ
								if (!bFound) {
									// ����ǳ��μ�⵽����Ҫ�洢����ԭ���������ν���ľ���ֵ
									bFound = true;
									closeDis = location.w;
									mpTriangle[0]=v0;
									mpTriangle[1]=v1;
									mpTriangle[2]=v2;

									mpCube = cube;
								} else {
									// ���֮ǰ�Ѿ���⵽�ཻ�¼�������Ҫ�����ཻ����֮ǰ���ཻ������Ƚ�
									// ���ձ���������ԭ�������
									if (closeDis > location.w) {
										closeDis = location.w;
										mpTriangle[0]=v0;
										mpTriangle[1]=v1;
										mpTriangle[2]=v2;
										
										mpCube = cube;
									}
								}
							}
						}
					}
			    }
			}
			
		}

		if(bFound){
			// ����ҵ����ཻ�������������
			AppConfig.gbTrianglePicked = true;
			
			// ������ݵ���ѡȡ�����ε���Ⱦ������
			mBufPickedTriangle.clear();
			for (int i = 0; i < 3; i++) {
				IBufferFactory.fillBuffer(mBufPickedTriangle, mpTriangle[i]);
			}
			
			mBufPickedTriangle.position(0);
			
			//ѡ��info������Բ���debug����Ϣ,����߿��Կ�����͵���Ϣ
			Log.d("GLWorld", "��ǰ��ѡ�񷽿�����" + pickedList.size() + ",��ǰѡ�񷽿飺" + mpCube.id);
//			Log.d("GLWorld", "��ǰ��������ĵ�����(ת��ǰ)��" + mpCube.getSphereCenter().toString());
//			Log.d("GLWorld", "��ǰ��������ĵ�����(ת����)��" + _transformedSphereCenter.toString());
			
			if(!AppConfig.Turning){
				addPickedList(mpCube);
			}
		}
		else{
			AppConfig.gbTrianglePicked = false;
		}
		
//		Log.i("GLWorld","��ʱ��" + (System.currentTimeMillis() - begin) + "millis");

		return bFound;
	}

	private Vector3f getVector3f(GLVertex vertex) {
		
		return new Vector3f(vertex.tempX,vertex.tempY,vertex.tempZ);
	}

	public void decideTurning(KubeActivity kubeAct) {
		// TODO Auto-generated method stub
		if(pickedList.size()<2){
			return;
		}
		
		Log.i("GLWorld", "ѡ�񷽿飺" + pickedList.size());
		
		Layer[] mLayers = kubeAct.mLayers;
		int layerID = -1;
		int index = -1;
		
		
		ArrayList<Integer> indexList = new ArrayList<Integer>();
		ArrayList<Layer> layerList = new ArrayList<Layer>();
		
		for (int i=0;i<mLayers.length;i++) {
			Layer layer = mLayers[i];
			indexList.clear();
			
//			Log.d("GLWorld","��ǰ��" + i + "���з��飺" +  layer.toString());
					
			for (int j=0;j < pickedList.size();j++) {
				Cube cube = pickedList.get(j);
				HashMap<String,Object> map = new HashMap<String, Object>();
				
				if(layer.hashCube(cube,map)){
					index = (Integer)map.get("index");					
					indexList.add(index);
				}
			}
		
			//��ǰ������鶼�ڸò�
			if (indexList.size()==pickedList.size()) {				
				layerID = i;
				Log.d("GLWorld", "����" + layerID + "��");
				
				StringBuilder sb = new StringBuilder();
				for (int j = 0; j < layer.mShapes.length; j++) {
					if (layer.mShapes[j]!=null) {
						sb.append(layer.mShapes[j].id + ";");
					}
				}
				
				layerList.add(layer);
				Log.d("GLWorld", "�ò������" + sb.toString());
			}
		}
		
		Log.d("GLWorld", "������" + layerList.size());
		
		AppConfig.gbNeedPick = false;

		// ������µ�ʰȡ����
		Ray ray = PickFactory.getPickRay();
		
		boolean bFound = false;
		
		// �洢������ԭ�����������ཻ��ľ���
		// �������������������������һ��
		float closeDis = 0.0f;

		Vector3f v0, v1, v2;
		Vector3f[] nearest = {new Vector3f(),new Vector3f(),new Vector3f()};
		Layer nearstLayer = null;
		
		
		Ray transformedRay = new Ray();

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
		
		//�жϵ�ǰ�������ĵ㵽�Ǹ��������������������������Ӧ���������㶼����������������㣩
		for (Layer layer : layerList) {
			float[] ixArr = layer.getMinMax();
			
			float minX = ixArr[0];
			float maxX = ixArr[1];
			float minY = ixArr[2];
			float maxY = ixArr[3];
			float minZ = ixArr[4];
			float maxZ = ixArr[5];
			
			//һ����������12�����������
			float[][] faces ={
					{minX,maxY,minZ , maxX,maxY,maxZ , minX,maxY,maxZ}, //top0
					{minX,maxY,minZ , maxX,maxY,minZ , maxX,maxY,maxZ}, //top1
					{minX,minY,minZ , maxX,minY,maxZ , minX,minY,maxZ}, //bottom0
					{minX,minY,minZ , maxX,minY,minZ , maxX,minY,maxZ}, //bottom1
					{minX,maxY,minZ , minX,minY,maxZ , minX,minY,minZ}, //left0
					{minX,maxY,minZ , minX,maxY,maxZ , minX,minY,maxZ}, //left1
					{maxX,maxY,maxZ , maxX,minY,minZ , maxX,minY,maxZ}, //right0
					{maxX,maxY,maxZ , maxX,maxY,minZ , maxX,minY,minZ}, //right1
					{minX,maxY,maxZ , maxX,minY,maxZ , minX,minY,maxZ}, //front0
					{minX,maxY,maxZ , maxX,maxY,maxZ , maxX,minY,maxZ}, //front1
					{minX,maxY,minZ , maxX,minY,minZ , minX,minY,minZ}, //back0
					{minX,maxY,minZ , maxX,maxY,minZ , maxX,minY,minZ}, //back1
			};
			
			//8������ɵ�6����(12��������)�Ƿ��뵱ǰ��������ཻ
			for (int j = 0; j < faces.length; j++) {
				v0 = new Vector3f(faces[j][0], faces[j][1], faces[j][2]);
				v1 = new Vector3f(faces[j][3], faces[j][4], faces[j][5]);
				v2 = new Vector3f(faces[j][6], faces[j][7], faces[j][8]);

				// ����������ཻ
				if (transformedRay.intersectTriangle(v0, v1, v2, location)) {
					Log.d("GLWorld", "��" + layer.index + "�������ཻ,������Ļ:" + location.w);
					
					if (!bFound) {
						// ����ǳ��μ�⵽����Ҫ�洢����ԭ���������ν���ľ���ֵ
						bFound = true;
						closeDis = location.w;
						nearstLayer = layer;
						
						nearest[0] = v0;
						nearest[1] = v1;
						nearest[2] = v2;
					} else {
						// ���֮ǰ�Ѿ���⵽�ཻ�¼�������Ҫ�����ཻ����֮ǰ���ཻ������Ƚ�
						// ���ձ���������ԭ�������(�������һ����Χ�ھ��ж��ཻƽ������)
						if(Math.abs(closeDis-location.w)<0.0001){
							//��ƽ��������������ж����������
							//�ȵ�ǰ�Ĵ���˵���濿ǰ
							double area1 = calculateArea(nearest[0],nearest[1],nearest[2]);
							double area2 = calculateArea(v0,v1,v2);
							
							Log.i("GLWorld","area1=" + String.valueOf(area1));
							Log.i("GLWorld","area2=" + String.valueOf(area2));
							
							if (area2>area1) {
								nearstLayer = layer;
								nearest[0] = v0;
								nearest[1] = v1;
								nearest[2] = v2;
							}
						}
						else if (closeDis > location.w) {
							closeDis = location.w;
							nearstLayer = layer;
							
							nearest[0] = v0;
							nearest[1] = v1;
							nearest[2] = v2;
						}
					}
				}
			}
			
			if (bFound) {
				Log.d("GLWorld","������Ļ�����������:" + nearest[0].toString() + "," + nearest[1].toString() + "," + nearest[2].toString());
			}
		}
		
		Log.d("GLWorld", "�뵱ǰ�Ӵ���������������Ϊ:" + nearest[0].toString() + "," + nearest[1].toString() + "," + nearest[2].toString());

		if (nearstLayer==null) {
			Log.d("GLWorld", "δ�ཻ");
		}
		else{
			Log.d("GLWorld", "�뵱ǰ�Ӵ�����������Ϊ:" + nearstLayer.index);
		}
		
		//����Ĳ���ת�������ӦΪ�������Ļ���棩
		for (Layer layer : layerList) {
			if (nearstLayer !=null && nearstLayer.index!= layer.index) {
				layerID = layer.index;
				break;
			}
		}
		
		if(layerID!=-1){
			AppConfig.Turning = true;
		}
		kubeAct.layerID = layerID;
	}

	/**
	 * ����ռ���ֱ�������ε����
	 * @param vector3f
	 * @param vector3f2
	 * @param vector3f3
	 * @return
	 */
	private double calculateArea(Vector3f v0, Vector3f v1,Vector3f v2) {
		// TODO Auto-generated method stub
		double[] arr = new double[3];
		
		arr[0] = Math.pow(v0.x - v1.x,2) + Math.pow(v0.y - v1.y,2) + Math.pow(v0.z - v1.z,2);
		arr[1] = Math.pow(v0.x - v2.x,2) + Math.pow(v0.y - v2.y,2) + Math.pow(v0.z - v2.z,2);
		arr[2] = Math.pow(v2.x - v1.x,2) + Math.pow(v2.y - v1.y,2) + Math.pow(v2.z - v1.z,2);
		
		//����
		Arrays.sort(arr);
		
		return Math.sqrt(arr[0])*Math.sqrt(arr[1])/2;
	}
}
