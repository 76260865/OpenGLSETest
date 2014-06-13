package org.join.ogles.lib;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Buffer������
 * �ṩ����Buffer�ķ����� 
 * ע��OPhone��Ҫ����gl*Pointer()������Buffer�������ҪΪdirectģʽ����ģ�
 * ��������ȷ��������������Native�Ķ��У������ܵ�Java�˵��������ջ��Ƶ�Ӱ�졣
 * ����FloatBuffer,ShortBuffer,IntBuffer�ȶ��ֽڵĻ������ 
 * ���ǵ��ֽ�˳�� ��������ΪnativeOrder��
 * 
 * @author Yong
 */
public class IBufferFactory {
	
	/**
	 * �����µ�FloatBuffer����
	 * 
	 * @param numElements
	 *            - floatԪ�صĸ���
	 * @return
	 */
	public static FloatBuffer newFloatBuffer(int numElements) {
		ByteBuffer bb = ByteBuffer.allocateDirect(numElements * 4);
		bb.order(ByteOrder.nativeOrder());
		FloatBuffer fb = bb.asFloatBuffer();
		fb.position(0);
		return fb;
	}

	public static ShortBuffer newShortBuffer(int numElements) {
		ByteBuffer bb = ByteBuffer.allocateDirect(numElements * 2);
		bb.order(ByteOrder.nativeOrder());
		ShortBuffer sb = bb.asShortBuffer();
		sb.position(0);
		return sb;
	}

	public static void read(FloatBuffer fb, Vector3f v) {
		v.x = fb.get();
		v.y = fb.get();
		v.z = fb.get();
	}

	public static void fillBuffer(FloatBuffer fb, Vector3f v) {
		fb.put(v.x);
		fb.put(v.y);
		fb.put(v.z);
	}

	public static void fillBuffer(FloatBuffer fb, Vector3f v, int limit) {
		fb.put(v.x);
		fb.put(1.0f - v.y);

		if (limit == 2) {

		} else {
			fb.put(v.z);
		}
	}

	public static void fillBuffer(FloatBuffer fb, Vector4f v) {
		fb.put(v.x);
		fb.put(v.y);
		fb.put(v.z);
		fb.put(v.w);
	}

	public static void fillBuffer(ShortBuffer sb, int[] data) {
		for (int i = 0; i < data.length; i++) {
			sb.put((short) data[i]);
		}
	}

	public static ShortBuffer newShortBuffer(short[] s) {
		// TODO Auto-generated method stub
		ByteBuffer bb = ByteBuffer.allocateDirect(s.length * 2);
		bb.order(ByteOrder.nativeOrder());
		ShortBuffer sb = bb.asShortBuffer();
		
		for (int i = 0; i < s.length; i++) {
			sb.put(s[i]);
		}
		
		sb.position(0);
		return sb;
	}

	public static FloatBuffer newFloatBuffer(float[] fs) {
		// TODO Auto-generated method stub
		ByteBuffer bb = ByteBuffer.allocateDirect(fs.length * 4);
		bb.order(ByteOrder.nativeOrder());
		FloatBuffer fb = bb.asFloatBuffer();
		
		for (int i = 0; i < fs.length; i++) {
			fb.put(fs[i]);
		}
		
		fb.position(0);
		return fb;
	}

}