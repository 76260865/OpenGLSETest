/**
 * 1����manifest������themeΪ��
 * android:theme="@android:style/Theme.NoDisplay" 
 * 
 * 2������intent-filterΪ��
 * <action android:name="android.intent.action.MAIN" />
 * <category android:name="android.intent.category.HOME" />
 * <category android:name="android.intent.category.DEFAULT" />
 * 
 * 3�����ú�̨�����intent����Ctrl+F����"#"�ַ��ɿ��ٶ�λ��
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
        startService(new Intent(Home.this, myService.class));// #���ú�̨�����intent
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

        public void chooseBackHome() {// ���ؿ��Խ��յ�home��ͼ�ĳ������

            // ��ȡhome�б�
            List<String> pkgNamesT = new ArrayList<String>();
            List<String> actNamesT = new ArrayList<String>();
            List<ResolveInfo> resolveInfos = context.getPackageManager().queryIntentActivities(
                    intent, PackageManager.MATCH_DEFAULT_ONLY);
            for (int i = 0; i < resolveInfos.size(); i++) {
                String string = resolveInfos.get(i).activityInfo.packageName;
                if (!string.equals(context.getPackageName())) {// �ų��Լ��İ���
                    pkgNamesT.add(string);
                    string = resolveInfos.get(i).activityInfo.name;
                    actNamesT.add(string);
                }
            }

            // Ԥ��ѡ��home
            String[] names = new String[pkgNamesT.size()];
            for (int i = 0; i < names.length; i++) {
                names[i] = pkgNamesT.get(i);
            }

            // ���õ���ʽ�Ի���ѡ��
            pkgNames = pkgNamesT;
            actNames = actNamesT;
            new AlertDialog.Builder(context).setTitle("��ѡ����������Ļ").setCancelable(false)
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

            Log.e("", "Ĭ������ĻΪ��" + pkgName + "/" + actName);

            if (pkgName.equals("android")) {
                // δ��������
            } else if (pkgName.equals("coder.zhuoke")) {
                // ������Ϊ������
            } else {
                // ��ǰ����Ϊ����
            }
        }

    }

}
