/**
 * 1�����ÿ�����Ҫ�򿪵ķ���İ�������������Ctrl+F����"#"�ַ��ɿ��ٶ�λ��
 * 
 * 
 * **/

package com.tomatozq.opengl.rubik.acts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

//����ʱ����
public class BootReceiver extends BroadcastReceiver{
	String myPkgName = "com.example.screenlocker";//#����
	String myActName = "com.example.acts.myService";//#����
	
	@Override
	public void onReceive(Context context, Intent intent) {
		//������������
		Intent myIntent=new Intent();
		myIntent.setAction(myPkgName+"."+myActName);
		context.startService(myIntent);	
	}
}
