package com.github.ggggxiaolong.xmpp.customView;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import com.github.ggggxiaolong.xmpp.R;
import com.github.ggggxiaolong.xmpp.utils.BitmapUtil;
import com.github.ggggxiaolong.xmpp.utils.MeasureUtil;

import timber.log.Timber;

/**
 * 参考https://github.com/binglingziyu/OwlView
 * 猫头鹰输入密码的时候捂眼动画
 * 1. 宽高比 5:3
 * 2. 翅膀高度占身体的 5:3
 * 3. 翅膀宽高比 3:5
 */

public final class OwlView extends View {

    //view的宽高
    private int mViewHeight, mViewWidth;
    //view的预期大小
    private final int expectWidth = MeasureUtil.dip2px(175);
    private final int expectHeight = MeasureUtil.dip2px(105);
    //身体图片
    private Bitmap bm_owl;
    //左翅膀图片
    private Bitmap bm_owl_arm_left;
    //右翅膀图片
    private Bitmap bm_owl_arm_right;
    //翅膀图片的高度
    private int bm_height;
    //动画小手在移动时的透明度
    private int alpha = 255;
    //动画：翅膀小手的高度
    private int move_height = 0;
    //动画椭圆小手移动的距离
    private int move_length = 0;
    //椭圆小手的宽高
    private int ovalWidth, ovalHeight;
    //翅膀小手距离底部的高度
    private int mWindMarginHeight;

    //身体和翅膀的矩形区域
    private Rect mBody_src, mBody_dst, mLeftWind_src, mLeftWind_dst, mRightWind_src, mRightWind_dst;
    //椭圆小手的大小
    private RectF mOvalHandRectF;

    //画椭圆小手的画笔
    private Paint handPaint;
    //画翅膀小手的画笔
    private Paint bitmapPaint;
    //动画的差值器
    private AccelerateInterpolator mAccelerateInterpolator;
    private DecelerateInterpolator mDecelerateInterpolator;

    public OwlView(Context context) {
        this(context, null);
    }

    public OwlView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OwlView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        bm_owl = BitmapFactory.decodeResource(getResources(), R.drawable.owl_login);
        bm_owl_arm_left = BitmapFactory.decodeResource(getResources(), R.drawable.owl_login_arm_left);
        bm_owl_arm_right = BitmapFactory.decodeResource(getResources(), R.drawable.owl_login_arm_right);

        handPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        handPaint.setColor(0xff472d20);

        bitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        mDecelerateInterpolator = new DecelerateInterpolator();
        mAccelerateInterpolator = new AccelerateInterpolator();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        //修正view的宽高比（如果宽高比不同则将测量值等比缩小）
        float temp = width / 5.0f * 3;
        if (temp < height) {
            height = (int) temp;
        } else {
            width = (int) (height / 3.0f * 5);
        }
//        Timber.i("height =  %d; width = %d", height, width);
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        switch (mode) {
            case MeasureSpec.AT_MOST: {
                if (expectHeight > height) {
                    setMeasuredDimension(expectWidth, expectHeight);
                } else {
                    setMeasuredDimension(width, height);
                }
                break;
            }
            case MeasureSpec.EXACTLY: {
                setMeasuredDimension(width, height);
                break;
            }
            case MeasureSpec.UNSPECIFIED: {
                setMeasuredDimension(expectWidth, expectHeight);
                break;
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //计算图片的宽高
        if (w == oldw && h == oldh) {
            return;
        }
        int bmWidth = h;
        mViewHeight = h;
        mViewWidth = w;
        int windHeight = (int) (h / 5.0f * 3);
        int windWidth = (int) (windHeight / 5.0f * 3);
        ovalWidth = (int) (windWidth / 4.0f * 3);
        ovalHeight = (int) (ovalWidth / 3.0f * 2);
        mWindMarginHeight = (int) (mViewHeight * 0.07f);
        //缩小图片
        bm_owl = BitmapUtil.compressBitmap(bm_owl, bmWidth, bmWidth, true);
        bm_owl_arm_left = BitmapUtil.compressBitmap(bm_owl_arm_left, windWidth, windHeight, true);
        bm_owl_arm_right = BitmapUtil.compressBitmap(bm_owl_arm_right, windWidth, windHeight, true);
        //设置初始的图片大小
        bm_height = bm_owl_arm_left.getHeight() / 3 * 2;

        mBody_src = new Rect(0, 0, bm_owl.getWidth(), bm_owl.getHeight());
        mBody_dst = new Rect((mViewWidth - bm_owl.getWidth()) / 2,
                mViewHeight - bm_owl.getHeight(),
                (mViewWidth + bm_owl.getWidth()) / 2,
                mViewHeight);
        //椭圆小手默认显示
        mOvalHandRectF = new RectF(0, mViewHeight - ovalHeight, ovalWidth, mViewHeight);

        //翅膀小手默认不显示
        mLeftWind_src = new Rect(0, 0, bm_owl_arm_left.getWidth(), bm_height);
        //高度0dp 宽度距离view左侧43dp
        mLeftWind_dst = new Rect();
        mRightWind_src = new Rect(0, 0, bm_owl_arm_right.getWidth(), bm_height);
        //高度0dp 宽度距离view右侧40dp
        mRightWind_dst = new Rect();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        //画身体
        canvas.drawBitmap(bm_owl, mBody_src, mBody_dst, bitmapPaint);
        //画椭圆小手
        handPaint.setAlpha(alpha);
        mOvalHandRectF.offsetTo(move_length, mViewHeight - ovalHeight);
        canvas.drawOval(mOvalHandRectF, handPaint);
        mOvalHandRectF.offsetTo(mViewWidth - move_length - ovalWidth, mViewHeight - ovalHeight);
        canvas.drawOval(mOvalHandRectF, handPaint);
        //画翅膀小手
        mLeftWind_dst.set(
                mViewWidth / 2 - bm_owl_arm_left.getWidth(),
                mViewHeight - move_height - mWindMarginHeight,
                mViewWidth / 2,
                mViewHeight - mWindMarginHeight);
        canvas.drawBitmap(bm_owl_arm_left, mLeftWind_src, mLeftWind_dst, bitmapPaint);
        mRightWind_dst.set(mViewWidth / 2,
                mViewHeight - mWindMarginHeight - move_height,
                mViewWidth / 2 + bm_owl_arm_left.getWidth(),
                mViewHeight - mWindMarginHeight);
        canvas.drawBitmap(bm_owl_arm_right, mRightWind_src, mRightWind_dst, bitmapPaint);
    }

    public void close() {
        /**
         * move_height:0-bm_height | delay 200 run 300
         */
        ValueAnimator windAnim = ValueAnimator.ofInt(bm_height, 0).setDuration(300);
        windAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                move_height = (int) animation.getAnimatedValue();
                invalidate();
            }
        });

        /**
         * move_length: 0 -- 45dp | 200ms
         * alpha: 255 -- 0 | 300ms
         */
        ValueAnimator alphaAnim = ValueAnimator.ofInt(0, 255).setDuration(300);
        alphaAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                alpha = (int) animation.getAnimatedValue();
                invalidate();
            }
        });

        alphaAnim.setInterpolator(mDecelerateInterpolator);
        alphaAnim.setStartDelay(200);
        ValueAnimator moveAnim = ValueAnimator.ofInt(mViewWidth / 2, 0).setDuration(200);
        moveAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                move_length = (int) animation.getAnimatedValue();
                invalidate();
            }
        });
        moveAnim.setStartDelay(200);

        //开启动画
        windAnim.start();
        alphaAnim.start();
        moveAnim.start();
    }

    public void open() {
        /**
         * move_length: 0 -- 45dp | 200ms
         * alpha: 255 -- 0 | 300ms
         */
        ValueAnimator alphaAnim = ValueAnimator.ofInt(255, 0).setDuration(300);
        alphaAnim.setInterpolator(mAccelerateInterpolator);
        alphaAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                alpha = (int) animation.getAnimatedValue();
                invalidate();
            }
        });
        ValueAnimator moveAnim = ValueAnimator.ofInt(0, mViewWidth / 2).setDuration(200);
        moveAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                move_length = (int) animation.getAnimatedValue();
                invalidate();
            }
        });

        /**
         *
         * move_height:0-bm_height | delay 200 run 300
         */
        ValueAnimator windAnim = ValueAnimator.ofInt(0, bm_height).setDuration(300);
        windAnim.setStartDelay(200);
        windAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                move_height = (int) animation.getAnimatedValue();
                invalidate();
            }
        });
        //开启动画
        windAnim.start();
        alphaAnim.start();
        moveAnim.start();
    }
}
