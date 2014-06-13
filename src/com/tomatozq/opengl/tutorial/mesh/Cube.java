package com.tomatozq.opengl.tutorial.mesh;

import org.join.ogles.lib.Ray;
import org.join.ogles.lib.Vector3f;
import org.join.ogles.lib.Vector4f;


public class Cube extends Mesh {
	private float[] vertices;
	private short[] indices;
	
	private float radius;
	// 触碰的立方体某一面的标记（0-5）
	public int surface = -1;
	
	// 返回立方体外切圆的半径
	public float getSphereRadius() {
		return radius;
	}
	
	public Vector3f getSphereCenter(){
		return new Vector3f(this.x, this.y, this.z);
	}
	
	public Cube(float width, float height, float depth) {
		width /= 2;
		height /= 2;
		depth /= 2;

		float max = width>height ? width :height;
		
		max = max>depth ? max : depth;
		
		radius = max * 1.732051f;
		
		Point3DList ptList = new Point3DList();

		ptList.add(-width, -height, -depth); // 0
		ptList.add(width, -height, -depth); // 1
		ptList.add(width, height, -depth); // 2
		ptList.add(-width, height, -depth); // 3
		ptList.add(-width, -height, depth); // 4
		ptList.add(width, -height, depth); // 5
		ptList.add(width, height, depth); 	// 6
		ptList.add(-width, height, depth); // 7
		
		Point3DList vList = new Point3DList();
		
		//7 4 5 6 
		vList.add(ptList.get(7));
		vList.add(ptList.get(4));
		vList.add(ptList.get(5));
		vList.add(ptList.get(6));
		
		//6 5 1 2
		vList.add(ptList.get(6));
		vList.add(ptList.get(5));
		vList.add(ptList.get(1));
		vList.add(ptList.get(2));		
		
		//3-0-1-2 
		vList.add(ptList.get(3));
		vList.add(ptList.get(0));
		vList.add(ptList.get(1));
		vList.add(ptList.get(2));
		
		//7-4-0-3
		vList.add(ptList.get(7)); //12
		vList.add(ptList.get(4)); //13
		vList.add(ptList.get(0)); //14
		vList.add(ptList.get(3)); //15
		
		//3-7-6-2 
		vList.add(ptList.get(3)); //16
		vList.add(ptList.get(7)); //17
		vList.add(ptList.get(6)); //18
		vList.add(ptList.get(2)); //19		
		
		//0-4-5-1 
		vList.add(ptList.get(0)); //20
		vList.add(ptList.get(4)); //21
		vList.add(ptList.get(5)); //22
		vList.add(ptList.get(1)); //23
		
		vertices = vList.toFloatArray();
		
		indices = new short[]{0,1,2,0,2,3,
						      4,5,6,4,6,7,
						      8,10,9,8,11,10,
						      12,14,13,12,15,14,
						      16,17,18,16,18,19,
						      20,22,21,20,23,22};

		float textureCoordinates[] = {0,0 , 0,1 , 1,1 , 1,0,
									  0,0 , 0,1 , 1,1 , 1,0,
									  0,0 , 0,1 , 1,1 , 1,0,
									  0,0 , 0,1 , 1,1 , 1,0,
									  0,0 , 0,1 , 1,1 , 1,0,
									  0,0 , 0,1 , 1,1 , 1,0};

		setIndices(indices);
		setVertices(vertices);
		// setColors(colors);
		setTextureCoordinates(textureCoordinates);
	}
	
	private static Vector4f location = new Vector4f();

	/**
	 * 射线与模型的精确碰撞检测
	 * 
	 * @param ray
	 *            - 转换到模型空间中的射线
	 * @param trianglePosOut
	 *            - 返回的拾取后的三角形顶点位置
	 * @return 如果相交，返回true
	 */
	public boolean intersect(Ray ray, Vector3f[] trianglePosOut) {
		boolean bFound = false;
		// 存储着射线原点与三角形相交点的距离
		// 我们最后仅仅保留距离最近的那一个
		float closeDis = 0.0f;

		Vector3f v0, v1, v2;

		// 立方体6个面
		for (int i = 0; i < 6; i++) {
			// 每个面两个三角形
			for (int j = 0; j < 2; j++) {
				v0 = getVector3f(indices[i * 6 + j*3]);
				v1 = getVector3f(indices[i * 6 + j*3 + 1]);
				v2 = getVector3f(indices[i * 6 + j*3 + 2]);

				// 进行射线和三角行的碰撞检测
				if (ray.intersectTriangle(v0, v1, v2, location)) {
					// 如果发生了相交
					if (!bFound) {
						// 如果是初次检测到，需要存储射线原点与三角形交点的距离值
						bFound = true;
						closeDis = location.w;
						trianglePosOut[0].set(v0);
						trianglePosOut[1].set(v1);
						trianglePosOut[2].set(v2);
						surface = i;
					} else {
						// 如果之前已经检测到相交事件，则需要把新相交点与之前的相交数据相比较
						// 最终保留离射线原点更近的
						if (closeDis > location.w) {
							closeDis = location.w;
							trianglePosOut[0].set(v0);
							trianglePosOut[1].set(v1);
							trianglePosOut[2].set(v2);
							surface = i;
						}
					}
				}
			}
		}
		return bFound;
	}

	private Vector3f getVector3f(int start) {
		return new Vector3f(vertices[3 * start], vertices[3 * start + 1],vertices[3 * start + 2]);
	}
}
