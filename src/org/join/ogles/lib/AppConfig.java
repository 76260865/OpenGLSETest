package org.join.ogles.lib;

import org.join.ogles.lib.Matrix4f;

public class AppConfig {
	public static boolean Turning = false;
	/**
	 * ͶӰ����
	 */
	public static Matrix4f gMatProject = new Matrix4f();
	/**
	 * ��ͼ����
	 */
	public static Matrix4f gMatView = new Matrix4f();
	/**
	 * ģ�;���
	 */
	public static Matrix4f gMatModel = new Matrix4f();
	/**
	 * �ӿڲ���
	 */
	public static int[] gpViewport = new int[4];
	/**
	 * ��ǰϵͳ��ͶӰ�����������
	 */
	public static float[] gpMatrixProjectArray = new float[16];
	/**
	 * ��ǰϵͳ����ͼ�����������
	 */
	public static float[] gpMatrixViewArray = new float[16];
	/**
	 * �Ƿ���Ҫ����ʰȡ��⣨�������¼�����ʱ��
	 */
	public static boolean gbNeedPick = false;
	/**
	 * �Ƿ��������α�ѡ��
	 */
	public static boolean gbTrianglePicked = false;

	public static float gScreenX, gScreenY;

	public static void setTouchPosition(float x, float y) {
		gScreenX = x;
		gScreenY = y;
	}

}