package com.tomatozq.opengl.rubik;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.tomatozq.opengl.R;
import com.tomatozq.opengl.rubik.view.CirclePageIndicator;

public class UserGuideActivity extends FragmentActivity {
    GuideFragmentAdapter mAdapter;
    ViewPager mPager;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        // The look of this sample is set via a style in the manifest
        setContentView(R.layout.user_guide_activity);
        SharedPreferences prefs = getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);
        boolean isPwdSet = prefs.getBoolean("ro.set.password", false);
        mAdapter = new GuideFragmentAdapter(getSupportFragmentManager());

        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);

        CirclePageIndicator indicator = (CirclePageIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(mPager);

        Bundle extra = getIntent().getExtras();
        boolean isViewUserGuide = extra != null ? extra.getBoolean("extra_introduce", false)
                : false;
        if (isPwdSet && !isViewUserGuide) {
            Intent intent = new Intent(UserGuideActivity.this, RobikCube2DActivity.class);
            startActivity(intent);
            finish();
        }
    }

    class GuideFragmentAdapter extends FragmentPagerAdapter {
        protected final String[] CONTENT = new String[] { "第一页", "第二页", "第三页" };

        private int mCount = CONTENT.length;

        public GuideFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return GuideFragment.newInstance(CONTENT[position % CONTENT.length]);
        }

        @Override
        public int getCount() {
            return mCount;
        }

        public void setCount(int count) {
            if (count > 0 && count <= 10) {
                mCount = count;
                notifyDataSetChanged();
            }
        }
    }
}
