package com.baidu91.tao.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.ImageView;

/**
 * 裁剪控件
 */
public class ClipImageView extends ImageView implements ScaleGestureDetector.OnScaleGestureListener {

    private static final int LAYER_FLAGS = Canvas.MATRIX_SAVE_FLAG | Canvas.CLIP_SAVE_FLAG
                | Canvas.HAS_ALPHA_LAYER_SAVE_FLAG | Canvas.FULL_COLOR_LAYER_SAVE_FLAG
                | Canvas.CLIP_TO_LAYER_SAVE_FLAG | Canvas.ALL_SAVE_FLAG;

    //裁剪区域半径
    private float mRadius = 200;
    private float radiusWidthRatio  = 3f/9;//裁剪圆框的半径占view的宽度的比
    private int clipBorderWidth = 2;
    //最大缩放
    public static final float SCALE_MAX = 3.0f;
    private float initScale = 1.0f;
    private float minScale = 1.0f;

    //绘制原型区域
    private Paint mPaint = new Paint();
    private Paint mBorderPaint = new Paint();
    private Xfermode xfermode;

    private RectF shelterR, circleR;

    private Bitmap mBitmap;


    private float mLastX;
    private float mLastY;

    private int lastPointerCount;
    /**
     * 缩放的手势检测
     */
    private ScaleGestureDetector mScaleGestureDetector = null;

    private GestureDetector mGestureDetector;

    private boolean isAutoScale;

    /**
     * 用于存放矩阵的9个值
     */
    private final float[] matrixValues = new float[9];
    private final Matrix mScaleMatrix = new Matrix();
    private boolean once = true;

    public ClipImageView(Context context) {
        this(context, null);
    }

    public ClipImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClipImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        sharedConstructing();
        mScaleGestureDetector = new ScaleGestureDetector(context, this);
        mGestureDetector = new GestureDetector(context, new GestureListener());
    }

    private void sharedConstructing() {
        super.setClickable(true);
        super.setScaleType(ScaleType.MATRIX);

        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.parseColor("#a8000000"));

        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setColor(Color.WHITE);
        mBorderPaint.setStrokeWidth(clipBorderWidth);
        mBorderPaint.setAntiAlias(true); //去锯齿
        xfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void compatPostOnAnimation(Runnable runnable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            postOnAnimation(runnable);
        } else {
            postDelayed(runnable, 1000 / 60);
        }
    }

    private float getScale() {
        mScaleMatrix.getValues(matrixValues);
        return matrixValues[Matrix.MSCALE_X];
    }

    private float getTranslateX() {
        mScaleMatrix.getValues(matrixValues);
        return matrixValues[Matrix.MTRANS_X];
    }

    private float getTranslateY() {
        mScaleMatrix.getValues(matrixValues);
        return matrixValues[Matrix.MTRANS_Y];
    }

    /**
     * 用来保证阴影的大小足以遮住图片
     * @return
     */
    private RectF getShelterRectF() {
        float x = (int) getTranslateX();
        float y = (int) getTranslateY();
        float width = getDrawable().getIntrinsicWidth() * getScale();
        float height = getDrawable().getIntrinsicHeight() * getScale();
        if (shelterR == null) {
            shelterR = new RectF(x, y, width + x, height + y);
        } else {
            shelterR.set(x, y, width + x, height + y);
        }
        return shelterR;
    }

    public void setImageResource(int resourceId) {
        super.setImageResource(resourceId);
        mBitmap = BitmapFactory.decodeResource(getResources(),resourceId);
        fitImageToView();
    }

    public void setImageBitmap(Bitmap bitmap) {
        super.setImageBitmap(bitmap);
        mBitmap = bitmap;
        fitImageToView();
    }

    /**
     * 剪裁头像
     * @return
     */
    public Bitmap clipBitmap() {
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        int dw = getDrawable().getIntrinsicWidth();
        int dh = getDrawable().getIntrinsicHeight();
        float x = getTranslateX() - (getWidth() - dw) / 2;
        float y = getTranslateY() - (getHeight() - dh) / 2;
        mBitmap = zoomBitmap(mBitmap);
        Bitmap target = Bitmap.createBitmap((int) mRadius * 2, (int) mRadius * 2, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(target);
        canvas.drawCircle(mRadius, mRadius, mRadius, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        canvas.drawBitmap(mBitmap,- (dw / 2 - mRadius) + x ,- (dh / 2 - mRadius) + y, paint);
        return target;
    }

    private Bitmap zoomBitmap(Bitmap bitmap) {
        int imageWidth = bitmap.getWidth();
        int imageHeight = bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(getScale(),getScale());
        Bitmap imageBitmap = Bitmap.createBitmap(bitmap, 0, 0, imageWidth, imageHeight, matrix, true);
        return imageBitmap;
    }

    private class AutoScaleRunnable implements Runnable {
        static final float BIGGER = 1.07f;
        static final float SMALLER = 0.93f;
        private float mTargetScale;
        private float tmpScale;

        /**
         * 缩放的中心
         */
        private float x;
        private float y;

        /**
         * 传入目标缩放值，根据目标值与当前值，判断应该放大还是缩小
         *
         * @param targetScale
         */
        public AutoScaleRunnable(float targetScale, float x, float y) {
            this.mTargetScale = targetScale;
            this.x = x;
            this.y = y;
            if (getScale() < mTargetScale) {
                tmpScale = BIGGER;
            } else {
                tmpScale = SMALLER;
            }
        }

        @Override
        public void run() {
            // 进行缩放
            mScaleMatrix.postScale(tmpScale, tmpScale, x, y);
            checkBorderAndCenterWhenScale();
            setImageMatrix(mScaleMatrix);

            final float currentScale = getScale();
            // 如果值在合法范围内，继续缩放
            if (((tmpScale > 1f) && (currentScale < mTargetScale))
                    || ((tmpScale < 1f) && (mTargetScale < currentScale))) {
                ClipImageView.this.postDelayed(this, 16);
            } else {
                // 设置为目标的缩放比例
                final float deltaScale = mTargetScale / currentScale;
                mScaleMatrix.postScale(deltaScale, deltaScale, x, y);
                checkBorderAndCenterWhenScale();
                setImageMatrix(mScaleMatrix);
                isAutoScale = false;
            }
        }
    }

    /**
     * 在缩放时，进行图片显示范围的控制
     */
    private void checkBorderAndCenterWhenScale() {

        RectF rect = getMatrixRectF();
        float deltaX = 0;
        float deltaY = 0;

        int width = getWidth();
        int height = getHeight();

        // 如果宽或高大于屏幕，则控制范围
        if (rect.width() >= width) {
            if (rect.left > 0) {
                deltaX = -rect.left;
            }
            if (rect.right < width) {
                deltaX = width - rect.right;
            }
        }
        if (rect.height() >= height) {
            if (rect.top > 0) {
                deltaY = -rect.top;
            }
            if (rect.bottom < height) {
                deltaY = height - rect.bottom;
            }
        }
        // 如果宽或高小于屏幕，则让其居中
        if (rect.width() < width) {
            deltaX = width * 0.5f - rect.right + 0.5f * rect.width();
        }
        if (rect.height() < height) {
            deltaY = height * 0.5f - rect.bottom + 0.5f * rect.height();
        }
        mScaleMatrix.postTranslate(deltaX, deltaY);
    }

    /**
     * 根据当前图片的Matrix获得图片的范围
     *
     * @return
     */
    private RectF getMatrixRectF() {
        Matrix matrix = mScaleMatrix;
        RectF rect = new RectF();
        Drawable d = getDrawable();
        if (null != d) {
            rect.set(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            matrix.mapRect(rect);
        }
        return rect;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (getDrawable() == null){
            return;
        }
        getShelterRectF();
        // 画入前景圆形蒙板层
        int sc = canvas.saveLayer(shelterR, null, LAYER_FLAGS);
        canvas.drawRect(shelterR, mPaint);
        // DST_OUT取差集，显示图层上最下面如图片背景不相交的部分，抠掉中间裁剪区域
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        mRadius = getWidth() * radiusWidthRatio;
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, mRadius, mPaint);
        if (circleR == null){
            circleR = new RectF(getWidth() / 2 - mRadius, getHeight() / 2 - mRadius, getWidth() / 2 + mRadius,
                    getHeight() / 2 + mRadius);
        }
        //白色的圆边框
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, mRadius, mBorderPaint);
        canvas.restoreToCount(sc);
        mPaint.setXfermode(null);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mGestureDetector.onTouchEvent(event))
            return true;
        mScaleGestureDetector.onTouchEvent(event);
        float x = 0, y = 0;
        // 拿到触摸点的个数
        final int pointerCount = event.getPointerCount();
        // 得到多个触摸点的x与y均值
        for (int i = 0; i < pointerCount; i++) {
            x += event.getX(i);
            y += event.getY(i);
        }
        x = x / pointerCount;
        y = y / pointerCount;
        /**
         * 每当触摸点发生变化时，重置mLasX , mLastY
         */
        if (pointerCount != lastPointerCount) {
            mLastX = x;
            mLastY = y;
        }
        lastPointerCount = pointerCount;
        switch (event.getAction()){
            case MotionEvent.ACTION_MOVE:
                float dx = x - mLastX;
                float dy = y - mLastY;

                if (shelterR.left + dx >= circleR.left - clipBorderWidth / 2 || shelterR.right + dx <= circleR.right - clipBorderWidth / 2) {
                    dx = 0;
                }
                if (shelterR.top + dy >=  circleR.top - clipBorderWidth / 2 || shelterR.bottom + dy <= circleR.bottom - clipBorderWidth / 2) {
                    dy = 0;
                }
                mScaleMatrix.postTranslate(dx, dy);
                setImageMatrix(mScaleMatrix);
                mLastX = x;
                mLastY = y;
                postInvalidate();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                lastPointerCount = 0;
                break;
        }
        return true;
    }

    private void fitImageToView() {
        Drawable drawable = getDrawable();
        if (drawable == null || drawable.getIntrinsicWidth() == 0 || drawable.getIntrinsicHeight() == 0) {
            return;
        }

        int width = getWidth();
        int height = getHeight();

        if (width == 0 || height == 0) {
            return;
        }

        mRadius = getWidth() * radiusWidthRatio;
        float clipWidth = mRadius * 2;
        int dw = drawable.getIntrinsicWidth();
        int dh = drawable.getIntrinsicHeight();
        float scale = 1.0f;
        if (dw < clipWidth && dh > clipWidth) {
            scale = clipWidth * 1.0f / dw;
        }
        if (dh < clipWidth && dw > clipWidth) {
            scale = clipWidth * 1.0f / dh;
        }
        if (dh < clipWidth && dw <clipWidth){
            scale = Math.max(clipWidth * 1.0f / dw, clipWidth * 1.0f / dh);
        }
        if (dw > dh) {
            minScale = clipWidth * 1.0f / dh;
        } else {
            minScale = clipWidth * 1.0f / dw;
        }
        initScale = scale;
        // 图片移动至屏幕中心
        mScaleMatrix.postTranslate((width - dw) / 2, (height - dh) / 2);
        mScaleMatrix.postScale(scale, scale, getWidth() / 2, getHeight() / 2);
        setImageMatrix(mScaleMatrix);
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        float scale = getScale();
        float scaleFactor = detector.getScaleFactor();

        if (getDrawable() == null)
            return true;

        /**
         * 缩放的范围控制
         */
        if ((scale < SCALE_MAX && scaleFactor > 1.0f)
                || (scale > minScale && scaleFactor < 1.0f)) {
            /**
             * 最大值最小值判断
             */
            if (scaleFactor * scale < minScale) {
                scaleFactor = minScale / scale;
            }
            if (scaleFactor * scale > SCALE_MAX) {
                scaleFactor = SCALE_MAX / scale;
            }
            /**
             * 设置缩放比例
             */
            mScaleMatrix.postScale(scaleFactor, scaleFactor, detector.getFocusX(), detector.getFocusX());
            checkBorderAndCenterWhenScale();
            setImageMatrix(mScaleMatrix);
        }
        return true;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {

    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return performClick();
        }

        @Override
        public void onLongPress(MotionEvent e) {
            performLongClick();
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if (!isAutoScale) {
                float x = e.getX();
                float y = e.getY();

                if (getScale() < SCALE_MAX) {
                    compatPostOnAnimation(new AutoScaleRunnable(SCALE_MAX, x, y));
                    isAutoScale = true;
                } else {
                    compatPostOnAnimation(new AutoScaleRunnable(initScale, x, y));
                    isAutoScale = true;
                }
                return true;
            }
            return false;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            return false;
        }
    }
}
