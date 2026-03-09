package com.iapp.concentration.views;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;

public class TimerCircleView extends View {

    private Paint trackPaint;
    private Paint progressPaint;
    private Paint dotPaint;

    private float progress = 0f;

    private final float strokeWidth = 24f;
    private final float dotRadius = 40f;

    private RectF arcRect;

    public TimerCircleView(Context context, AttributeSet attrs) {
        super(context, attrs);

        trackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        trackPaint.setColor(Color.WHITE);
        trackPaint.setStyle(Paint.Style.STROKE);
        trackPaint.setStrokeWidth(strokeWidth);

        progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(strokeWidth);
        progressPaint.setStrokeCap(Paint.Cap.ROUND);

        dotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        dotPaint.setColor(Color.WHITE);

        // glow шарика
        dotPaint.setShadowLayer(20,0,0,Color.WHITE);
        setLayerType(LAYER_TYPE_SOFTWARE,null);
    }

    public void setProgress(float progress) {
        this.progress = progress;
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        float padding = strokeWidth + dotRadius;

        arcRect = new RectF(
                padding,
                padding,
                w - padding,
                h - padding
        );

        // градиент
        SweepGradient gradient =
                new SweepGradient(
                        w/2f,
                        h/2f,
                        new int[]{
                                Color.parseColor("#A8E063"),
                                Color.parseColor("#56AB2F")
                        },
                        null
                );

        progressPaint.setShader(gradient);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        float cx = getWidth()/2f;
        float cy = getHeight()/2f;

        // белая дорожка
        canvas.drawArc(arcRect, -90, 360, false, trackPaint);

        // зелёный прогресс
        float sweep = 360 * progress;
        canvas.drawArc(arcRect, -90, sweep, false, progressPaint);

        // позиция шарика
        float radius = arcRect.width()/2f;

        float angle = (float)Math.toRadians(sweep - 90);

        float x = (float)(cx + radius * Math.cos(angle));
        float y = (float)(cy + radius * Math.sin(angle));

        canvas.drawCircle(x, y, dotRadius, dotPaint);
    }
}