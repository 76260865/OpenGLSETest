/**
 * 使用方式：
 * 1，在manifest中注册权限：<uses-permission android:name="android.permission.DISABLE_KEYGUARD" />
 * 2，在manifest中注册此服务
 * 3，将toMainIntent设置成要跳转到的界面（用Ctrl+F搜索"#"字符可快速定位）
 * 4，在主界面Main中启动此服务：startService(new Intent(Main.this, myService.class));
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

        // 设置intent
        toMainIntent = new Intent(myService.this, RobikCube2DActivity.class);// #设置Main.class为要跳转到的界面，既当解锁时要打开的界面
        toMainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);// 必须得有，不知为何

        keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        keyguardLock = keyguardManager.newKeyguardLock("myService");
        keyguardLock.disableKeyguard();

        // 注册广播
        IntentFilter intentFilter = new IntentFilter("android.intent.action.SCREEN_OFF");
        intentFilter.addAction("android.intent.action.SCREEN_ON");
        registerReceiver(screenReceiver, intentFilter);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;// 粘性进程
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(screenReceiver);
        // 重启此服务
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

                // 关闭锁屏
                keyguardManager = (KeyguardManager) context
                        .getSystemService(context.KEYGUARD_SERVICE);
                keyguardLock = keyguardManager.newKeyguardLock("");
                keyguardLock.disableKeyguard();
                Log.e("", "closed the keyGuard");

                // 打开主界面
                startActivity(toMainIntent);
            }

        }
    };

}
