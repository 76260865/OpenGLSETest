package com.tomatozq.opengl.rubik.acts;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.tomatozq.opengl.R;

public class Main extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        startService(new Intent(Main.this, myService.class));

        Log.e("", "hello");
    }

    @Override
    public void onBackPressed() {
        Log.d("xxxxx", "onBackPressed");
        Toast.makeText(this, "最最込込", Toast.LENGTH_LONG).show();
    }
}
