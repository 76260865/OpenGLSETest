/**
 * 1，在manifest中设置theme为：
 * android:theme="@android:style/Theme.NoDisplay" 
 * 
 * 2，设置intent-filter为：
 * <action android:name="android.intent.action.MAIN" />
 * <category android:name="android.intent.category.HOME" />
 * <category android:name="android.intent.category.DEFAULT" />
 * 
 * 3，设置后台服务的intent（用Ctrl+F搜索"#"字符可快速定位）
 * **/

package com.tomatozq.opengl.rubik.acts;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

public class Home extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HomeChoice homeChoice = new HomeChoice(this);
        startService(new Intent(Home.this, myService.class));// #设置后台服务的intent
        try {
            homeChoice.originalHome();
        } catch (Exception e) {
            homeChoice.chooseBackHome();
        }
    }

    public void onAttachedToWindow() {
        this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);

        super.onAttachedToWindow();
    }

    public class HomeChoice {
        Context context;
        Intent intent;

        SharedPreferences sharedPreferences;
        Editor editor;

        String packageName = "packageName";
        String activityName = "activityName";

        List<String> pkgNames, actNames;

        public HomeChoice(Context context) {
            this.context = context;
            intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            sharedPreferences = context.getSharedPreferences("homeChoice", MODE_PRIVATE);
            editor = sharedPreferences.edit();
        }

        public void chooseBackHome() {// 返回可以接收到home意图的程序包名

            // 获取home列表
            List<String> pkgNamesT = new ArrayList<String>();
            List<String> actNamesT = new ArrayList<String>();
            List<ResolveInfo> resolveInfos = context.getPackageManager().queryIntentActivities(
                    intent, PackageManager.MATCH_DEFAULT_ONLY);
            for (int i = 0; i < resolveInfos.size(); i++) {
                String string = resolveInfos.get(i).activityInfo.packageName;
                if (!string.equals(context.getPackageName())) {// 排除自己的包名
                    pkgNamesT.add(string);
                    string = resolveInfos.get(i).activityInfo.name;
                    actNamesT.add(string);
                }
            }

            // 预备选择home
            String[] names = new String[pkgNamesT.size()];
            for (int i = 0; i < names.length; i++) {
                names[i] = pkgNamesT.get(i);
            }

            // 利用弹出式对话框选择
            pkgNames = pkgNamesT;
            actNames = actNamesT;
            new AlertDialog.Builder(context).setTitle("请选择解锁后的屏幕").setCancelable(false)
                    .setSingleChoiceItems(names, 0, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            editor.putString(packageName, pkgNames.get(which));
                            editor.putString(activityName, actNames.get(which));
                            editor.commit();
                            originalHome();
                            dialog.dismiss();
                        }
                    }).show();
        }

        public void originalHome() {
            String pkgName = sharedPreferences.getString(packageName, null);
            String actName = sharedPreferences.getString(activityName, null);
            ComponentName componentName = new ComponentName(pkgName, actName);
            Intent intent = new Intent();
            intent.setComponent(componentName);
            context.startActivity(intent);
            ((Activity) context).finish();
        }

        public void chooseHome() {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
        }

        public void getPkgName() {
            ActivityInfo activityInfo = context.getPackageManager().resolveActivity(intent, 0).activityInfo;
            String pkgName = activityInfo.packageName;
            String actName = activityInfo.name;

            Log.e("", "默认主屏幕为：" + pkgName + "/" + actName);

            if (pkgName.equals("android")) {
                // 未设置锁屏
            } else if (pkgName.equals("coder.zhuoke")) {
                // 锁屏已为本程序
            } else {
                // 当前锁屏为其他
            }
        }

    }

}
