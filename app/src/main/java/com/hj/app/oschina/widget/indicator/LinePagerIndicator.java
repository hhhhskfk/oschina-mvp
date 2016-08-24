
package com.hj.app.oschina.widget.indicator;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

import com.hj.app.oschina.R;


/**
 * Created by huanghaibin
 * on 16-5-19.
 */
@SuppressWarnings("unused")
public class LinePagerIndicator extends View implements PagerIndicator {

    //绘制Line的画笔
    private final Paint mPaintFull = new Paint(Paint.ANTI_ALIAS_FLAG);

    //绘制Line指示器当前位置的画笔
    private final Paint mPaintIndicator = new Paint(Paint.ANTI_ALIAS_FLAG);

    private ViewPager mViewPager;
    private ViewPager.OnPageChangeListener mListener;
    private float mPageOffset;
    private int mCurrentPage;
    private int mFollowPage;
    private boolean mCenterHorizontal;
    private float mLineWidth;
    private float mLineHeight;
    private float mLineIndicatorHeight;
    private float mSpace;
    private boolean mIsFollow;


    public LinePagerIndicator(Context context) {
        this(context, null);
    }

    @SuppressWarnings("SuspiciousNameCombination")
    public LinePagerIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LinePagerIndicator);
        mCenterHorizontal = a.getBoolean(R.styleable.LinePagerIndicator_line_indicator_centerHorizontal, true);
        mLineWidth = a.getDimension(R.styleable.LinePagerIndicator_line_indicator_width, 10);
        mSpace = a.getDimension(R.styleable.LinePagerIndicator_line_indicator_space, 5);
        mLineHeight = a.getDimension(R.styleable.LinePagerIndicator_line_indicator_height, 0);
        mLineIndicatorHeight = a.getDimension(R.styleable.LinePagerIndicator_line_indicator_indicator_height, mLineHeight);
        mPaintFull.setColor(a.getColor(R.styleable.LinePagerIndicator_line_indicator_fill_color, 0x000000));
        mPaintIndicator.setColor(a.getColor(R.styleable.LinePagerIndicator_line_indicator_indicator_color, 0x00ff00));
        mIsFollow = a.getBoolean(R.styleable.LinePagerIndicator_line_indicator_follow, true);
        a.recycle();

        mPaintIndicator.setStrokeWidth(mLineIndicatorHeight > mLineHeight ? mLineIndicatorHeight : mLineHeight);
        mPaintFull.setStrokeWidth(mLineHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mViewPager == null) {
            return;
        }

        final int count = mViewPager.getAdapter().getCount();
        if (count == 0) {
            return;
        }

        if (mCurrentPage >= count) {
            setCurrentItem(count - 1);
            return;
        }

        final float lineWidthAndSpace = mLineWidth + mSpace;//线宽和间距
        final float indicatorWidth = (count * lineWidthAndSpace) - mSpace;//指示器+间隔

        final float paddingTop = getPaddingTop();
        final float paddingLeft = getPaddingLeft();
        final float paddingRight = getPaddingRight();

        float verticalOffset = paddingTop + ((getHeight() - paddingTop - getPaddingBottom()) / 2.0f);//绘制线的中心竖直方向偏移量
        float horizontalOffset = paddingLeft;////绘制线的中心水平方向偏移量

        //如果采用水平居中对齐的水平偏移量
        if (mCenterHorizontal) {
            horizontalOffset += ((getWidth() - paddingLeft - paddingRight) - indicatorWidth) / 2.0f;
        }

        float startX;
        float stopX;

        for (int i = 0; i < count; i++) {
            startX = horizontalOffset + (i * lineWidthAndSpace);////计算下个圆绘制起点偏移量
            stopX = startX + mLineWidth;
            canvas.drawLine(startX, verticalOffset, stopX, verticalOffset, mPaintFull);
        }


        float currentSpace = (!mIsFollow ? mFollowPage : mCurrentPage) * lineWidthAndSpace;


        //指示器选择缓慢移动
        if (mIsFollow) {
            currentSpace += mPageOffset * lineWidthAndSpace;
        }

        startX = horizontalOffset + currentSpace;
        stopX = startX + mLineWidth;
        canvas.drawLine(startX, verticalOffset, stopX, verticalOffset, mPaintIndicator);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
    }


    private int measureWidth(int measureSpec) {
        float width;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if ((specMode == MeasureSpec.EXACTLY) || (mViewPager == null)) {
            width = specSize;
        } else {
            final int count = mViewPager.getAdapter().getCount();
            width = getPaddingLeft() + getPaddingRight() + (count * mLineWidth) + ((count - 1) * mSpace);
            if (specMode == MeasureSpec.AT_MOST) {
                width = Math.min(width, specSize);
            }
        }
        return (int) Math.ceil(width);
    }

    private int measureHeight(int measureSpec) {
        float height;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            height = specSize;
        } else {
            height = mPaintIndicator.getStrokeWidth() + getPaddingTop() + getPaddingBottom();
            if (specMode == MeasureSpec.AT_MOST) {
                height = Math.min(height, specSize);
            }
        }
        return (int) Math.ceil(height);
    }

    @Override
    public void bindViewPager(ViewPager viewPager) {
        if (mViewPager == viewPager) {
            return;
        }
        if (viewPager.getAdapter() == null) {
            throw new IllegalStateException("ViewPager does not set adapter");
        }
        mViewPager = viewPager;
        mViewPager.addOnPageChangeListener(this);
        invalidate();
    }

    @Override
    public void bindViewPager(ViewPager view, int initialPosition) {
        bindViewPager(view);
        setCurrentItem(initialPosition);
    }

    @Override
    public void setCurrentItem(int item) {
        if (mViewPager == null) {
            throw new IllegalStateException("indicator has not bind ViewPager");
        }
        mViewPager.setCurrentItem(item);
        mCurrentPage = item;
        invalidate();
    }

    @Override
    public void notifyDataSetChanged() {
        invalidate();
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (mListener != null) {
            mListener.onPageScrollStateChanged(state);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        mCurrentPage = position;
        mPageOffset = positionOffset;
        //如果指示器跟随ViewPager缓慢滑动，那么滚动是时候都绘制界面
        if (mIsFollow) {
            invalidate();
        }
        if (mListener != null) {
            mListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }
    }

    @Override
    public void onPageSelected(int position) {
        mCurrentPage = position;
        mFollowPage = position;
        invalidate();
        if (mListener != null) {
            mListener.onPageSelected(position);
        }
    }

    @Override
    public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        mListener = listener;
    }
}