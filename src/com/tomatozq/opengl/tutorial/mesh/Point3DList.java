package com.tomatozq.opengl.tutorial.mesh;

import java.util.ArrayList;

public class Point3DList extends ArrayList<Point3D> {
	public void add(float x,float y,float z){
		Point3D pt = new Point3D(x,y,z);
		add(pt);
	}
	
	public float[] get3D(int pos){
		Point3D pt = this.get(pos);
		
		float[] arr = new float[]{pt.getX(),pt.getY(),pt.getZ()};
	
		return arr;
	}
	
	public float[] toFloatArray(){
		float[] array = new float[this.size()*3];
		
		for (int i = 0; i < this.size(); i++) {
			array[i*3] = this.get(i).getX();
			array[i*3+1] = this.get(i).getY();
			array[i*3+2] = this.get(i).getZ();
		}
		
		return array;
	}
}
