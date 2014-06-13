package com.tomatozq.opengl.rubik.view;

import android.support.v4.view.ViewPager;

public interface PageIndicator extends ViewPager.OnPageChangeListener {
    public void setViewPager(ViewPager view);

    public void setViewPager(ViewPager view, int initialPosition);

    public void setCurrentItem(int item);

    public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener);
}
