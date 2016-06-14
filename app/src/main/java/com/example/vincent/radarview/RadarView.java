package com.example.vincent.radarview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by vincent on 16/6/13.
 */
public class RadarView extends View {

    private int mHeight;
    private int mWidth;

    private boolean isStart = false;
    private Shader mShader;
    private int start = 0;
    private Matrix matrix;

    private Paint paintRadarLocation;
    private Paint paintRadarPointer;
    private Paint paintRadarCircle;
    private Paint paintRadarLine;
    private Paint paintRadarDegree;
    private Paint paintRadarBg;
    private int circleRadius;

    private Bitmap locationBmp;

    private PathEffect effects;

    private final int paintWidth = 2;
    private int time = 25;

    private ScanRunnable scanRunnable = new ScanRunnable();

    public RadarView(Context context) {
        super(context);
    }

    public RadarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RadarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        initLocationBmp();
        initPaint();
        matrix = new Matrix();
        setBackgroundColor(Color.TRANSPARENT);
    }

    private void initLocationBmp() {
        Bitmap bitmap = null;
        bitmap = ((BitmapDrawable) getResources().getDrawable(R.drawable.location_icon)).getBitmap();

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Matrix matrixLocation = new Matrix();
        float scaleWidth = ((float) 60 / width);
        float scaleHeight = ((float) 80 / height);
        matrixLocation.postScale(scaleWidth, scaleHeight);
        locationBmp = Bitmap.createBitmap(bitmap, 0, 0, width, height,
                matrixLocation, true);
    }

    private void initPaint() {
        effects = new DashPathEffect(new float[]{10, 10}, 1);
        //定位图标
        paintRadarLocation = new Paint();
        paintRadarLocation.setAntiAlias(true);
        paintRadarLocation.setColor(Color.BLACK);
        paintRadarLocation.setStrokeWidth((float) 3.0);
        paintRadarLocation.setStyle(Paint.Style.STROKE);
        //雷达扫码圆心画笔
        paintRadarPointer = new Paint();
        paintRadarPointer.setStyle(Paint.Style.FILL);
        paintRadarPointer.setColor(getResources().getColor(R.color.orange_red));
        //雷达扫码圆圈画笔
        paintRadarCircle = new Paint();
        paintRadarCircle.setStrokeWidth(paintWidth);
        paintRadarCircle.setAntiAlias(true);
        paintRadarCircle.setStyle(Paint.Style.STROKE);
        paintRadarCircle.setColor(0x80888888);
        paintRadarCircle.setDither(true);
        //雷达扫码扫描分区画笔
        paintRadarDegree = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintRadarDegree.setStrokeWidth(paintWidth);
        paintRadarDegree.setAntiAlias(true);
        paintRadarDegree.setStyle(Paint.Style.STROKE);
        paintRadarDegree.setColor(0x80888888);
        paintRadarDegree.setDither(true);
        paintRadarDegree.setPathEffect(effects);
        //雷达扫描背景画笔
        paintRadarBg = new Paint();
        paintRadarBg.setAntiAlias(true);
        //雷达扫描针画笔
        paintRadarLine = new Paint();
        paintRadarLine.setStrokeWidth(3);
        paintRadarLine.setColor(getResources().getColor(R.color.orange_red));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mHeight = getMeasuredHeight();
        mWidth = getMeasuredWidth();
        circleRadius = mWidth / 8;
        mShader = new SweepGradient(mWidth / 2, mHeight / 2, Color.TRANSPARENT, 0x80888888);
        paintRadarBg.setShader(mShader);

        // 画圆心
        canvas.drawCircle(mWidth / 2, mHeight / 2, 10, paintRadarPointer);
        // 画分区
        drawRadarDegree(canvas);
        // 画雷达圆圈
        drawRadarCircle(canvas);

        canvas.save();
        canvas.concat(matrix);
        canvas.drawLine(mWidth / 2, mHeight / 2, mWidth / 2 + 3 * circleRadius + 15, mHeight / 2, paintRadarLine);
        canvas.drawCircle(mWidth / 2, mHeight / 2, 3 * circleRadius, paintRadarBg);
        canvas.restore();
        //设置定位图标
        canvas.drawBitmap(locationBmp, mWidth / 2 - locationBmp.getWidth() / 2, mHeight / 2 - circleRadius-locationBmp.getHeight(), paintRadarLocation);
    }

    private void drawRadarDegree(Canvas canvas) {

        for (int i = 0; i < 24; i++) {
            if (i == 0 || i == 3 || i == 6 || i == 9) {
                Path path = new Path();
                path.moveTo(mWidth / 2, mHeight / 2 - 3 * circleRadius);
                path.lineTo(mWidth / 2, mHeight / 2 + 3 * circleRadius);
                canvas.drawPath(path, paintRadarDegree);
            }
            // 通过旋转画布简化坐标运算
            canvas.rotate(45, mWidth / 2, mHeight / 2);
        }
    }

    private void drawRadarCircle(Canvas canvas) {
        int count = getMeasuredHeight() / circleRadius / 2;
        int alpha = 128 / count;
        for (int i = 0; i < count; i++) {
            paintRadarCircle.setAlpha(128 - alpha * i);
            if (i == 0) {
                paintRadarCircle.setPathEffect(effects);
            } else {
                paintRadarCircle.setPathEffect(null);
            }
            canvas.drawCircle(mWidth / 2, mHeight / 2, circleRadius + i * circleRadius, paintRadarCircle);
        }
    }

    public void setTime(int time) {
        this.time = time;
    }

    public void start() {
        new Thread(scanRunnable).start();
        isStart = true;
    }

    public void stop() {
        if (isStart) {
            Thread.interrupted();
            isStart = false;
        }
    }

    protected class ScanRunnable implements Runnable {

        @Override
        public void run() {
            while (isStart) {
                start = start + 2;
                matrix.reset();
                matrix.postRotate(start, mWidth / 2, mHeight / 2);
                postInvalidate();
                try {
                    Thread.sleep(time * 1000 / 180);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
