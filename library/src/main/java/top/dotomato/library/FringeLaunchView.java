package top.dotomato.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.NinePatch;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by chen on 2017/11/7.
 */

public class FringeLaunchView extends View {

    private NinePatch shadowPatch;
    private FringeGroup fringeGroup1;
    private FringeGroup fringeGroup2;
    private Paint backgroundPaint;
    private Paint fringePaint;
    private Rect shadowRect = new Rect();
    private Rect fringeRect = new Rect();

    private boolean exited;
    private int fringeState;
    private int exitState;

    private long startTime;
    private Rect canvasRect;

    final private int shadowWidth1 = 35;

    private float mainH1;
    private float mainH2;
    private int fringeWidth;
    private int fringeHeight;
    private int degree;
    private int overlapWidth;
    private int fringeCount;
    private int intervalHeight;
    private int slideLength;
    private float enterDuration;
    private float exitDuration;
    private long ExitdelayMillis;
    private float previewTime;

    public FringeLaunchView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        getAttrs(context, attrs);
        init(context);
    }

    public FringeLaunchView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getAttrs(context, attrs);
        init(context);
    }

    private void getAttrs(Context context, AttributeSet attrs){
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.FringeLaunchView, 0, 0);
        try{
            mainH1 = a.getFloat(R.styleable.FringeLaunchView_mainH1, 0.9f);
            mainH2 = a.getFloat(R.styleable.FringeLaunchView_mainH2, 0.98f);
            degree = a.getInteger(R.styleable.FringeLaunchView_degree, 20);
            fringeCount = a.getInteger(R.styleable.FringeLaunchView_fringeCount, 15);
            intervalHeight = a.getInteger(R.styleable.FringeLaunchView_intervalHeight, 150);
            fringeHeight = a.getInteger(R.styleable.FringeLaunchView_fringeHeight, 300);
            fringeWidth = a.getInteger(R.styleable.FringeLaunchView_fringeWidth, 2500);
            slideLength = a.getInteger(R.styleable.FringeLaunchView_slideLength, 1500);
            overlapWidth = a.getInteger(R.styleable.FringeLaunchView_overlapWidth, 700);
            enterDuration = a.getFloat(R.styleable.FringeLaunchView_enterDuration, 1.5f);
            exitDuration = a.getFloat(R.styleable.FringeLaunchView_exitDuration, 0.7f);
            previewTime = a.getFloat(R.styleable.FringeLaunchView_previewTime, 2);
        }finally {
            a.recycle();
        }
    }

    private void init(Context context){
        fringePaint = new Paint();
        backgroundPaint = new Paint();
        canvasRect = new Rect();

        fringeGroup1 = new FringeGroup(context,0, 0, -degree, 1);
        fringeGroup2 = new FringeGroup(context, -overlapWidth, -fringeCount*intervalHeight, -degree-180, 2);

        ExitdelayMillis = (long) ((exitDuration+1) * 1000);

        exited = false;
        fringeState = 1;
        exitState = 0;
    }

    public void start(){
        post(new Runnable() {
            @Override
            public void run() {
                fringeGroup1.start();
            }
        });
        postDelayed(new Runnable() {
            @Override
            public void run() {
                fringeGroup2.start();
            }
        }, 400);

        postDelayed(new Runnable() {
            @Override
            public void run() {

                if (fringeState ==1){
                    fringeState = 3;
                }

                if (fringeState ==2){
                    fringeState = 4;
                    exitState = 1;
                    startTime = System.currentTimeMillis();
                    fringeGroup1.back();
                    fringeGroup2.back();
                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            exited = true;
                            fringeState = 5;
                        }
                    }, (long) (exitDuration*1000));

                    if (mOnExitCallback!=null){
                        mOnExitCallback.onExit();
                    }
                }
            }
        }, (long) (enterDuration*1000));
    }


    @Override
    protected void onDraw(Canvas canvas)
    {
        canvasRect.right = canvas.getWidth();
        canvasRect.bottom = canvas.getHeight();

        if (exitState ==0) {
            backgroundPaint.setARGB(255, 255, 255, 255);
            canvas.drawRect(canvasRect, backgroundPaint);
        } else {
            float t =(System.currentTimeMillis() - startTime)/1000.0f;
            int a = (int) (Math.max(0, 1-t*2)*255);
            backgroundPaint.setARGB(a, 255, 255, 255);
            canvas.drawRect(canvasRect, backgroundPaint);
        }

        fringeGroup2.onDraw(canvas);
        fringeGroup1.onDraw(canvas);

        if (fringeState !=5) {
            invalidate();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event){
        if (!exited) {
            onTouchEvent(event);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN)
            performClick();
        return true;
    }

    @Override
    public boolean performClick() {
        super.performClick();
        if (fringeState ==1) {
            fringeState = 2;
        }
        if (fringeState ==3) {
            fringeState = 4;
            exitState = 1;
            startTime = System.currentTimeMillis();
            fringeGroup1.back();
            fringeGroup2.back();
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    exited = true;
                    fringeState = 5;
                }
            }, ExitdelayMillis);

            if (mOnExitCallback!=null){
                mOnExitCallback.onExit();
            }
        }
        return true;
    }

    class FringeGroup {

        private int degree;
        private long startTime;
        private ArrayList<Fringe> fringes = new ArrayList<>();

        FringeGroup(Context context, int left, int top, int degree, int index){

            startTime =0;
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.shadow);
            shadowPatch = new NinePatch(bitmap, bitmap.getNinePatchChunk(), null);
            this.degree = degree;

            for (int i = 0; i< fringeCount; i++){
                Fringe fringe = new Fringe(left+ slideLength, left, top+ intervalHeight *i, i, index);
                fringes.add(fringe);
            }
            Collections.shuffle(fringes);
        }

        void start(){
            startTime = System.currentTimeMillis();
        }

        void back(){
            startTime = System.currentTimeMillis();
        }

        void onDraw(Canvas canvas){
            if (isInEditMode()){
                canvas.rotate(degree);
                float t = previewTime;
                for (Fringe fringe : fringes) {
                    fringe.drawFront(canvas, t);
                }
                canvas.rotate(-degree);
                return;
            }

            if ((fringeState ==1 || fringeState ==2 || fringeState ==3) && startTime!=0) {
                canvas.rotate(degree);
                float t = (System.currentTimeMillis() - startTime)/1000.0f;
                for (Fringe fringe : fringes) {
                    fringe.drawFront(canvas, t);
                }
                canvas.rotate(-degree);
            }

            if (fringeState ==4){
                canvas.rotate(degree);
                float t = (System.currentTimeMillis() - startTime)/1000.0f;
                for (Fringe fringe : fringes) {
                    fringe.drawBack(canvas, t);
                }
                canvas.rotate(-degree);
            }


        }


        class Fringe {
            private int color;
            private int x1;
            private int x2;
            private int y;
            private double t1;
            private double ts;
            private int h;

            private float randF(){
                return (float) Math.random();
            }

            Fringe(int x1, int x2, int y, int fringeIndex, int groupIndex){
                this.x1 = x1;
                this.x2 = (int) (x2+ randF()*150);
                this.y = y;
                h = (int) (fringeHeight*(Math.random()*0.5+0.5));
                if (groupIndex==1){
                    color = HSBtoRGB(mainH1+(randF()-0.5f)*0.1f, 0.3f+ randF()*0.2f, 0.8f+ randF()*0.2f);
                }
                else if (groupIndex==2) {
                    color = HSBtoRGB(mainH2+(randF()-0.5f)*0.1f, 0.7f + randF() * 0.2f, 0.8f + randF() * 0.2f);
                }
                t1 = -Math.random()*0.3;
                ts = 0.8f+Math.random()*0.2f;
            }

            private int HSBtoRGB(float hue, float saturation, float brightness) {
                int r = 0, g = 0, b = 0;
                if (saturation == 0) {
                    r = g = b = (int) (brightness * 255.0f + 0.5f);
                } else {
                    float h = (hue - (float)Math.floor(hue)) * 6.0f;
                    float f = h - (float)java.lang.Math.floor(h);
                    float p = brightness * (1.0f - saturation);
                    float q = brightness * (1.0f - saturation * f);
                    float t = brightness * (1.0f - (saturation * (1.0f - f)));
                    switch ((int) h) {
                        case 0:
                            r = (int) (brightness * 255.0f + 0.5f);
                            g = (int) (t * 255.0f + 0.5f);
                            b = (int) (p * 255.0f + 0.5f);
                            break;
                        case 1:
                            r = (int) (q * 255.0f + 0.5f);
                            g = (int) (brightness * 255.0f + 0.5f);
                            b = (int) (p * 255.0f + 0.5f);
                            break;
                        case 2:
                            r = (int) (p * 255.0f + 0.5f);
                            g = (int) (brightness * 255.0f + 0.5f);
                            b = (int) (t * 255.0f + 0.5f);
                            break;
                        case 3:
                            r = (int) (p * 255.0f + 0.5f);
                            g = (int) (q * 255.0f + 0.5f);
                            b = (int) (brightness * 255.0f + 0.5f);
                            break;
                        case 4:
                            r = (int) (t * 255.0f + 0.5f);
                            g = (int) (p * 255.0f + 0.5f);
                            b = (int) (brightness * 255.0f + 0.5f);
                            break;
                        case 5:
                            r = (int) (brightness * 255.0f + 0.5f);
                            g = (int) (p * 255.0f + 0.5f);
                            b = (int) (q * 255.0f + 0.5f);
                            break;
                    }
                }
                return 0xff000000 | (r << 16) | (g << 8) | (b << 0);
            }

            private void drawFront(Canvas canvas, float t){
                double k = t*ts/enterDuration + t1;
                k = Math.max(0, k);
                k = Math.min(1, k);
                double k1 = Math.cos(Math.pow(k,1.3)*3*Math.PI);
                double k2 = (Math.cos(Math.pow(k,0.2)*Math.PI)+1)/2;
                double k3 = 1-k1*k2;

                double m = (1-k3)*50;

                int left = (int) (x1 + k3*(x2- x1));
                int right = left + fringeWidth;
                int top = (int) (y + m);
                int bottom = top + h;

                shadowRect.left = left-shadowWidth1;
                shadowRect.top = top-shadowWidth1;
                shadowRect.right = right+shadowWidth1;
                shadowRect.bottom = bottom+shadowWidth1;
                shadowPatch.draw(canvas, shadowRect);

                fringeRect.left = left;
                fringeRect.top = top;
                fringeRect.right = right;
                fringeRect.bottom = bottom;

                fringePaint.setColor(color);
                canvas.drawRect(fringeRect, fringePaint);
            }

            private void drawBack(Canvas canvas, float t){
                double k = t/exitDuration + t1*2;
                k = Math.max(0, k);
                k = Math.min(1, k);
                double k1 = Math.pow(1-k, 1);

                int left = (int) (x1 + k1*(x2- x1));
                int right = left + fringeWidth;
                int top = y;
                int bottom = top + h;

                shadowRect.left = left-shadowWidth1;
                shadowRect.top = top-shadowWidth1;
                shadowRect.right = right+shadowWidth1;
                shadowRect.bottom = bottom+shadowWidth1;
                shadowPatch.draw(canvas, shadowRect);

                fringeRect.left = left;
                fringeRect.top = top;
                fringeRect.right = right;
                fringeRect.bottom = bottom;
                fringePaint.setColor(color);
                canvas.drawRect(fringeRect, fringePaint);
            }
        }
    }


    public interface OnExitCallback{
        void onExit();
    }
    private OnExitCallback mOnExitCallback = null;
    public void setOnExitCallback(OnExitCallback callback){
        mOnExitCallback = callback;
    }
}
