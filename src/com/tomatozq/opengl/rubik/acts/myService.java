/**
 * ʹ�÷�ʽ��
 * 1����manifest��ע��Ȩ�ޣ�<uses-permission android:name="android.permission.DISABLE_KEYGUARD" />
 * 2����manifest��ע��˷���
 * 3����toMainIntent���ó�Ҫ��ת���Ľ��棨��Ctrl+F����"#"�ַ��ɿ��ٶ�λ��
 * 4����������Main�������˷���startService(new Intent(Main.this, myService.class));
 * **/

package com.tomatozq.opengl.rubik.acts;

import android.app.KeyguardManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import com.tomatozq.opengl.rubik.RobikCube2DActivity;

@SuppressWarnings("deprecation")
public class myService extends Service {
    private String TAG = "ScreenReceiver Log";
    private KeyguardManager keyguardManager = null;
    private KeyguardManager.KeyguardLock keyguardLock = null;
    Intent toMainIntent;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // ����intent
        toMainIntent = new Intent(myService.this, RobikCube2DActivity.class);// #����Main.classΪҪ��ת���Ľ��棬�ȵ�����ʱҪ�򿪵Ľ���
        toMainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);// ������У���֪Ϊ��

        keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        keyguardLock = keyguardManager.newKeyguardLock("myService");
        keyguardLock.disableKeyguard();

        // ע��㲥
        IntentFilter intentFilter = new IntentFilter("android.intent.action.SCREEN_OFF");
        intentFilter.addAction("android.intent.action.SCREEN_ON");
        registerReceiver(screenReceiver, intentFilter);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;// ճ�Խ���
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(screenReceiver);
        // �����˷���
        startActivity(new Intent(myService.this, myService.class));
    }

    private BroadcastReceiver screenReceiver = new BroadcastReceiver() {

        @SuppressWarnings("static-access")
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.e(TAG, "intent.action = " + action);

            if (action.equals("android.intent.action.SCREEN_ON")
                    || action.equals("android.intent.action.SCREEN_OFF")) {

                // �ر�����
                keyguardManager = (KeyguardManager) context
                        .getSystemService(context.KEYGUARD_SERVICE);
                keyguardLock = keyguardManager.newKeyguardLock("");
                keyguardLock.disableKeyguard();
                Log.e("", "closed the keyGuard");

                // ��������
                startActivity(toMainIntent);
            }

        }
    };

}
