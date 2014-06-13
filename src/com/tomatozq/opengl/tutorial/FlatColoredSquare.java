package com.tomatozq.opengl.tutorial;

import javax.microedition.khronos.opengles.GL10;

public class FlatColoredSquare extends Square {
	public void draw(GL10 gl){
		 gl.glColor4f(0.5f, 0.5f, 1.0f, 1.0f);
		 super.draw(gl);
	}
}