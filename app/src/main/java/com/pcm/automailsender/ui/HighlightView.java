package com.pcm.automailsender.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;

import com.pcm.automailsender.R;
import com.pcm.automailsender.common.ui.UiUtil;


/**
 * Created by pengchenming at 2024.05.10
 * 可以忽略父view背景色，绘制背景对应为透明高亮样式
 * 支持动态设置：背景色、高亮色、高亮圆角
 */
public class HighlightView extends View {
    public static final String TAG = "HighlightView";

    private int mBackgroundColor; // 背景色
    private int mHighlightColor; // 高亮色
    private int mCorner; // 高亮圆角

    private Paint bgPaint; // 整体背景画笔
    private Paint highlightPaint; // 高亮色画笔(支持透明色(包括下层的透明色))
    private Path highlightPath; // 高亮色绘制形状

    public HighlightView(Context context) {
        super(context);
        init(null);
    }

    public HighlightView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public HighlightView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d(TAG, "onDraw");
        // 开启离屏缓冲 TODO PCM 确认是否需要开启
        int saved = canvas.saveLayer(null, null, Canvas.ALL_SAVE_FLAG);

        // 绘制背景色
        canvas.drawColor(mBackgroundColor);

        // 绘制带有圆角透明背景
        canvas.drawRoundRect(new RectF(50, 50, 200, 200), UiUtil.dp2px(mCorner), UiUtil.dp2px(mCorner), highlightPaint);

        // 关闭离屏缓冲
        canvas.restoreToCount(saved);
    }

    private void init(@Nullable AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.HighlightView);
            mBackgroundColor = a.getColor(R.styleable.HighlightView_backgroundColor, Color.TRANSPARENT);
            mHighlightColor = a.getColor(R.styleable.HighlightView_backgroundColor, Color.TRANSPARENT);
            mCorner = a.getDimensionPixelSize(R.styleable.HighlightView_corner, UiUtil.dp2px(6));
            a.recycle();
        } else {
            mBackgroundColor = Color.TRANSPARENT;
            mHighlightColor = Color.TRANSPARENT;
            mCorner = UiUtil.dp2px(6);
        }
        // 初始化画笔相关
        setBgColor(mBackgroundColor);
        setHighlightColor(mHighlightColor);
        setHighlightCorner(mCorner);
        // 不使用硬件加速
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        setWillNotDraw(false);
    }

    public void setHighlightColor(@ColorInt int color) {
        this.mHighlightColor = color;
//        this.mHighlightColor = Color.GREEN;
        // 初始化透明画笔
        highlightPaint = new Paint();
        highlightPaint.setColor(mHighlightColor);
        highlightPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        highlightPaint.setAntiAlias(true);
    }

    public void setBgColor(@ColorInt int color) {
        this.mBackgroundColor = color;
        this.mBackgroundColor = Color.parseColor("#B3000000");
        // 初始化背景画笔
        bgPaint = new Paint();
        bgPaint.setColor(mBackgroundColor); // 设置背景色
        bgPaint.setStyle(Paint.Style.FILL);
    }

    public void setHighlightCorner(int radius) {
        this.mCorner = radius;

        // 初始化透明区域路径：一个带有圆角的矩形路径
        highlightPath = new Path();
        float[] radii = {mCorner, mCorner, mCorner, mCorner, mCorner, mCorner, mCorner, mCorner}; // 圆角的半径
        highlightPath.addRoundRect(new RectF(0, 0, getWidth(), getHeight()), radii, Path.Direction.CW);
    }
}

