package com.ljfth.ecgviewlib;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class EcgWaveView extends View {
    private int mode;
    private EcgViewInterface listener;
    private Bitmap machineBitmap;
    private Bitmap cacheBitmap;
    private Bitmap backBitmap;
    private Canvas machineCanvas = null;
    private Paint paint;
    private Paint bmpPaint = new Paint();
    private int width;
    private int height;
    private boolean waveAdapter = true;
    private int SampleV;
    private int SampleR;
    private double n_frequency;
    private int bx;
    private int by;
    //基线电压
    private double baseline_voltage = 1D; //baseline_voltage
    private int change_50n;
    private double change_nV = 1D;
    private int n_step;
    private int n_ecgx;
    private int ecgy;
    private int n_Btime;
    private double n_Ptime;
    private double[] t = new double[3];
    private float oldDistance;
    private float newDistance;
    private static int DISTANCE = 100;
    private int y_max;
    private int y_min;
    private int change_type;
    private int waveColor = Color.RED;
    private int gridColor = Color.argb(128, 220, 190, 50);
    private boolean b_autoResize = true;
    private boolean gridFullFill = false;

    public EcgViewInterface getListener() {
        return this.listener;
    }

    public void setListener(EcgViewInterface var1) {
        this.listener = var1;
    }

    public boolean isWaveAdapter() {
        return this.waveAdapter;
    }

    public void setWaveAdapter(boolean var1) {
        this.waveAdapter = var1;
    }

    public void setWaveColor(int var1) {
        this.waveColor = var1;
    }

    public void setGridColor(int var1) {
        this.gridColor = var1;
    }

    public void setGridFullFill(boolean var1) {
        this.gridFullFill = var1;
    }

    public int getSampleRe() {
        return this.SampleR;
    }

    public void setSampleRe(int var1) {
        this.SampleR = var1;
    }

    public int getSampleV() {
        return this.SampleV;
    }

    public void setSampleV(int var1) {
        this.SampleV = var1;
        //初始化基线电压
    }

    //初始化每格电压
    public void initGridVoltage(double var1) {
        this.change_nV = var1;
    }

    //初始化下基线电压
    public void initBaseLineVoltage(double var1) {
        this.baseline_voltage = var1;
    }

    public double getN_frequency() {
        return this.n_frequency;
    }

    public void setN_frequency(double var1) {
        this.n_frequency = var1;
    }

    public EcgWaveView(Context var1, int var2, int var3) {
        super(var1);
        this.width = var2;
        this.height = var3;
    }

    public void init(EcgViewInterface var1) {
        listener = var1;
        this.bx = this.width;
        this.by = this.height / 50 * 50 + 5;
        this.drawBackGrid();
        this.machineBitmap = Bitmap.createBitmap(this.backBitmap, 0, 0, this.width, this.height);
        this.cacheBitmap = Bitmap.createBitmap(this.backBitmap, 0, 0, this.width, this.height);
        this.machineCanvas = new Canvas();
        this.machineCanvas.setBitmap(this.machineBitmap);
        this.paint = new Paint(4);
        this.paint.setColor(waveColor);
        this.paint.setStyle(Style.STROKE);
        this.paint.setStrokeWidth(4.0F);
        this.paint.setAntiAlias(true);
        this.paint.setDither(true);
        this.paint.setStrokeJoin(Join.ROUND);
        this.ecgy = 21;
        this.n_ecgx = 1;
        checkRange();
        this.change_type = 1;
        this.change_50n = 50;
        this.n_step = 2;
        this.UnitDraw();
    }

    public void UnitDraw() {
        this.n_Ptime = 1000.0D / this.n_frequency;
        this.n_Btime = (int) (this.n_Ptime * (double) this.change_50n / (double) this.n_step);
        if (listener != null) {
            this.listener.onShowMessage(this, this.n_Btime + "", 0);
        }
    }

    private void drawBackGrid() {
        //绘制网格
        int m, n;
        backBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvasBackground = new Canvas();
        canvasBackground.setBitmap(backBitmap);

        Paint paint1 = new Paint();

        //画背景
        paint1.setColor(gridFullFill ? gridColor : Color.WHITE);
        paint1.setStyle(Paint.Style.FILL);
        paint1.setStrokeWidth(2);
        paint1.setAntiAlias(true);
        paint1.setDither(true);
        paint1.setStrokeJoin(Paint.Join.ROUND);
        canvasBackground.drawRect(0, 0, bx, by, paint1);
        if (!gridFullFill) {
            setBackgroundColor(gridColor);
            paint1.setStyle(Paint.Style.STROKE);
            paint1.setColor(gridColor);
            //画点
            for (m = 0; m < bx; m = m + 10)
                for (n = 0; n < by; n = n + 10)
                    canvasBackground.drawPoint(m, n, paint1);
            //画网格
            for (m = 0; m < bx; m = m + 50)
                canvasBackground.drawLine(m, 0, m, by - 5, paint1);
            for (n = 0; n < by; n = n + 50)
                canvasBackground.drawLine(0, n, bx, n, paint1);
        }

    }


    public void onDraw(Canvas canvas) {
        canvas.drawBitmap(machineBitmap, 0, 0, bmpPaint);
    }

    public void drawWave(int y) {
        int ecgy_new = changeOut(y);
        machineCanvas.drawLine(n_ecgx, ecgy, n_ecgx + n_step, ecgy_new, this.paint);
        n_ecgx += n_step;
        ScreenResh();
        invalidate();
        ecgy = ecgy_new;
    }


    private int changeOut(int temp) {
        float a;
        int b;
        //temp-欲转换的数值
        //a表示放大之前电压的真实范围。SampleV 是输入的电压范围：比如[0,10]mv,则SampleV 就是10，而[0,10]所对应的Y值是[0,4096],则SampleR就是4096
        a = (float) SampleV * temp / SampleR;
        //这个公式的意思是（真实电压-基线电压）* 每格所拥有的50个像素/每格代表的电压
        b = (short) (by - (a - baseline_voltage) * change_50n / change_nV);
        // y_max 和y_min 用于记录像素最大值和最小值，当前的像素点操过了最大值，表明曲线将要超出屏幕，需要进行波形的自适应调整。
        if (b < y_min)
            y_min = b;
        if (b > y_max)
            y_max = b;

        if (b > by) {
//            b = (short) by - 1;
            b = (short) by;
            b_autoResize = true;
            change_type = 1;
        }

        if (b <= 0) {
            b = 0;
            b_autoResize = true;
            change_type = 1;
        }
        return b;
    }


    private void ScreenResh() {
        if (n_ecgx > bx - 5)    //如果曲线到头
        {
            //判断（ymax-ymin）的值是不是小于高度一半，如果小于，就要自动调整
            if ((y_max - y_min) * 2 < by) {
                b_autoResize = true;
                change_type = 1;
            }
            n_ecgx = 0;
            Rect rect = new Rect(0, 0, 20, height);    //表示更新的区域,从0到20的X轴
            machineCanvas.drawBitmap(cacheBitmap, rect, rect, bmpPaint);
            if (b_autoResize && waveAdapter) {
                AutoResize();
            }
            checkRange();
        }

        Rect rect = new Rect(n_ecgx + n_step + 10, 0, n_ecgx + n_step + 20, height);
//        Rect rect = new Rect(n_ecgx + 2, 0, n_ecgx + n_step + 2, height);
        machineCanvas.drawBitmap(cacheBitmap, rect, rect, bmpPaint);
    }


    //自适应调整
    public void AutoResize() {
        if (change_type == 1) {
            //波形的幅度小于画布高度，并且波形幅度的2倍大于画布高度，说明波形幅度合适，此时只要调整基线
            if ((y_max - y_min) * 2 >= by && (y_max - y_min) <= by) {
                change_type = 2;
            } else {
                //表示波形范围超过画布高度
                if (y_max - y_min >= by) {
                    change_nV = change_nV * 2;
                    if (listener != null) {
                        listener.onShowMessage(this, change_nV + "", 1);
                    }
                    //如果波形幅度的两倍都小于画布高度，说明波形幅度过小，需要波形像素调整放大
                } else if ((y_max - y_min) * 2 <= by) {
                    change_nV = change_nV / 2;
                    if (listener != null) {
                        listener.onShowMessage(this, change_nV + "", 1);
                    }
                }

                checkRange();
                return;
            }
        }

        if (change_type == 2) {
            int n_top = (by - (y_max + y_min)) / 2;
            this.baseline_voltage += n_top * change_nV / change_50n;
            if (listener != null) {
                listener.onShowMessage(this, baseline_voltage + "", 2);
            }
        }
        b_autoResize = false;
    }




    public void checkRange() {
        this.y_max = -3 * this.by;
        this.y_min = 3 * this.by;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                Log.i("tag", "手势放下");
                mode = 1;
                break;
            case MotionEvent.ACTION_UP:
                mode = 0;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                Log.i("tag", "手势拿起" + spacing(event));
                newDistance = spacing(event);
                mode -= 1;
                if (newDistance > oldDistance + DISTANCE) {
                    n_step = n_step * 2;
                    n_Btime = (int) (n_Ptime * change_50n / n_step);
                    if (listener != null) {
                        listener.onShowMessage(this, n_Btime + "", 0);
                    }
                    Log.i("tag", "n_step-->" + n_step + ";n_Btime-->" + n_Btime);
                } else if (newDistance < oldDistance - DISTANCE) {
                    if (n_step >= 2) {
                        n_step = n_step / 2;
                        n_Btime = (int) (n_Ptime * change_50n / n_step);
                        if (listener != null) {
                            listener.onShowMessage(this, n_Btime + "", 0);
                        }
                        Log.i("tag", "n_step-->" + n_step + ";n_Btime-->" + n_Btime);
                    }
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                Log.i("tag", "手势放下" + spacing(event));
                oldDistance = spacing(event);
                mode += 1;
                break;

            case MotionEvent.ACTION_MOVE:
                break;
        }
        return true;
    }


    private float spacing(MotionEvent var1) {
        float var2 = var1.getX(0) - var1.getX(1);
        float var3 = var1.getY(0) - var1.getY(1);
        return (float) Math.sqrt((double) (var2 * var2 + var3 * var3));
    }
}
