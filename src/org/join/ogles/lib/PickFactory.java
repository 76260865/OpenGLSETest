package org.join.ogles.lib;
import org.join.ogles.lib.Projector;
import org.join.ogles.lib.Ray;

public class PickFactory {

	private static Ray gPickRay = new Ray();

	public static Ray getPickRay() {
		return gPickRay;
	}

	private static Projector gProjector = new Projector();

	private static float[] gpObjPosArray = new float[4];

	/**
	 * ����ʰȡ����
	 * @param screenX - ��Ļ����X
	 * @param screenY - ��Ļ����Y
	 */
	public static void update(float screenX, float screenY) {
		AppConfig.gMatView.fillFloatArray(AppConfig.gpMatrixViewArray);

		// ����OpenGL��Ļ����ϵԭ��Ϊ���½ǣ�����������ϵԭ��Ϊ���Ͻ�
		// ��ˣ���OpenGl�е�YӦ����Ҫ�õ�ǰ�ӿڸ߶ȣ���ȥ��������Y
		float openglY = AppConfig.gpViewport[3] - screenY;
		
		// z = 0 , �õ�P0
		gProjector.gluUnProject(screenX, openglY, 0.0f,
				AppConfig.gpMatrixViewArray, 0, AppConfig.gpMatrixProjectArray,
				0, AppConfig.gpViewport, 0, gpObjPosArray, 0);
		
		// �������ԭ��P0
		gPickRay.mvOrigin.set(gpObjPosArray[0], gpObjPosArray[1],gpObjPosArray[2]);

		// z = 1 ���õ�P1
		gProjector.gluUnProject(screenX, openglY, 1.0f,
				AppConfig.gpMatrixViewArray, 0, AppConfig.gpMatrixProjectArray,
				0, AppConfig.gpViewport, 0, gpObjPosArray, 0);
		
		// �������ߵķ���P1 - P0
		gPickRay.mvDirection.set(gpObjPosArray[0], gpObjPosArray[1],gpObjPosArray[2]);
		gPickRay.mvDirection.sub(gPickRay.mvOrigin);
		
		// ������һ��
		gPickRay.mvDirection.normalize();
	}

}
