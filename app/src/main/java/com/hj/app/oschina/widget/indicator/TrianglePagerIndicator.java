package com.hj.app.oschina.widget.indicator;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import java.util.List;

/**
 * Created by huanghaibin
 * on 16-6-15.
 */
public class TrianglePagerIndicator extends LinearLayout implements PagerIndicator{

    private static final int MAX_TRIANGLE_WIDTH = 130;
    private static final int MAX_TRIANGLE_HEIGHT = 50;

    private static final int TEXT_COLOR_NORMAL = 0xFFF6F6F6;
    private static final int TEXT_COLOR_SELECT = 0x00000000;
    private static final int INDICATOR_COLOR_NORMAL = 0xFFF6F6F6;
    private static final int INDICATOR_COLOR_SELECT = 0x00000000;

    private Paint mPaint = new Paint();
    private Path mPath = new Path();

    private int mTriangleWidth;
    private int mTriangleHeight;

    private List<String> tabTitles;

    public TrianglePagerIndicator(Context context) {
        super(context);
    }

    public TrianglePagerIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void bindViewPager(ViewPager viewPager) {

    }

    @Override
    public void bindViewPager(ViewPager viewPager, int initialPosition) {

    }

    @Override
    public void setCurrentItem(int currentItem) {

    }

    @Override
    public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {

    }

    @Override
    public void notifyDataSetChanged() {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
