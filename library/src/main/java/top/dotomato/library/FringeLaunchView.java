package top.dotomato.library;

import android.content.Context;
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



    private NinePatch patch;
    private RectGroup rectGroup1;
    private RectGroup rectGroup2;
    private boolean finished;


    private int mode;
    private int mode2;
    private long startTime;
    private Paint paint;
    private Rect rect;

    public FringeLaunchView(Context context) {
        super(context);
        init(context);
    }

    public FringeLaunchView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FringeLaunchView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private void init(Context context){

        finished = false;

        paint = new Paint();
        rect = new Rect();

        rectGroup1 = new RectGroup(context,0, 0, -20, 1);
        rectGroup2 = new RectGroup(context, -600, -2000, -200, 2);
        mode = 1;
        mode2 = 0;

        postDelayed(new Runnable() {
            @Override
            public void run() {
                rectGroup1.start();
            }
        }, 100);
        postDelayed(new Runnable() {
            @Override
            public void run() {
                rectGroup2.start();
            }
        }, 400);


        postDelayed(new Runnable() {
            @Override
            public void run() {

                if (mode==1){
                    mode = 3;
                }

                if (mode==2){
                    mode = 4;
                    mode2 = 1;
                    startTime = System.currentTimeMillis();
                    rectGroup1.back();
                    rectGroup2.back();
                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finished = true;
                            mode = 5;
                        }
                    }, 1000);

                    if (mOnExitCallback!=null){
                        mOnExitCallback.onExit();
                    }
                }
            }
        }, 3000);
    }


    @Override
    protected void onDraw(Canvas canvas)
    {
        rect.right = canvas.getWidth();
        rect.bottom = canvas.getHeight();
        if (mode2==0) {
            paint.setARGB(255, 255, 255, 255);
            canvas.drawRect(rect, paint);
        } else {
            float t =(System.currentTimeMillis() - startTime)/1000.0f;
            int a = (int) (Math.max(0, 1-t*2)*255);
            paint.setARGB(a, 255, 255, 255);
            canvas.drawRect(rect, paint);
        }

        rectGroup2.onDraw(canvas);
        rectGroup1.onDraw(canvas);

        if (mode!=5) {
            invalidate();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event){
//        super.dispatchTouchEvent(event);
        if (!finished) {
            onTouchEvent(event);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN){

            if (mode==1) {
                mode = 2;
            }
            if (mode==3) {
                mode = 4;
                mode2 = 1;
                startTime = System.currentTimeMillis();
                rectGroup1.back();
                rectGroup2.back();
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finished = true;
                        mode = 5;
                    }
                }, 1000);

                if (mOnExitCallback!=null){
                    mOnExitCallback.onExit();
                }
            }

        }
        performClick();
        return true;
    }

    class RectGroup{

        final private int shadowWidth1 = 35;

        private Paint paint1;
        private Rect mRect = new Rect();
        private Rect mRect2 = new Rect();
        private int mDegree;


        private long startTime;

        private ArrayList<RectMyStyle> rectMyStyles = new ArrayList<>();

        public RectGroup(Context context, int left, int top, int degree, int index){
            paint1 = new Paint();

            startTime =0;

            Bitmap mShadowBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.shadow);
            patch = new NinePatch(mShadowBitmap, mShadowBitmap.getNinePatchChunk(), null);
            mDegree = degree;

            for (int i=0; i<18; i++){
                RectMyStyle rectMyStyle = new RectMyStyle(left+2000, left, top+125*i, i, index);
                rectMyStyles.add(rectMyStyle);
            }
            Collections.shuffle(rectMyStyles);
        }

        public void start(){
            startTime = System.currentTimeMillis();
        }

        public void back(){
            startTime = System.currentTimeMillis();
        }

        public void onDraw(Canvas canvas){
            if ((mode==1 || mode==2 || mode==3) && startTime!=0) {
                canvas.rotate(mDegree);
                float t = (System.currentTimeMillis() - startTime)/1000.0f;
                for (RectMyStyle rectMyStyle:rectMyStyles) {
                    rectMyStyle.drawRectMyStyle(canvas, t);
                }
                canvas.rotate(-mDegree);
            }

            if (mode==4){
                canvas.rotate(mDegree);
                float t = (System.currentTimeMillis() - startTime)/1000.0f;
                for (RectMyStyle rectMyStyle:rectMyStyles) {
                    rectMyStyle.drawRectMyStyleBack(canvas, t);
                }
                canvas.rotate(-mDegree);
            }
        }


        class RectMyStyle{
            private int color;
            private int x1;
            private int x2;
            private int y;
            private double t1;
            private double ts;
            final private int width = 4000;
            private int height = 200;

            private float rand(){
                return (float) Math.random();
            }

            public RectMyStyle(int _x1, int _x2, int _y, int index, int index2){
                x1 = _x1;
                x2 = (int) (_x2+rand()*150);
                y = _y;
                height = (int) (250*(Math.random()*0.5+0.5));
                if (index2==1)
                    color = Color.HSVToColor(new float[]{320+index*2, 0.3f+rand()*0.2f, 0.8f+rand()*0.2f});
                else if (index2==2)
                    color = Color.HSVToColor(new float[]{330+index*2, 0.7f+rand()*0.2f, 0.8f+rand()*0.2f});
                t1 = -Math.random()*0.3;
                ts = 0.8f+Math.random()*0.2f;
                ts = ts/2;
            }

            private void drawRectMyStyle(Canvas c, float t){
                double k = t*ts + t1;
                k = Math.max(0, k);
                k = Math.min(1, k);
                double k1 = Math.cos(Math.pow(k,1.3)*5*Math.PI);
                double k2 = Math.pow(1-k, 1.7);
                double k3 = 1-k1*k2;

                double m = (1-k3)*50;

                int s = shadowWidth1;
                int left = (int) (x1 + k3*(x2-x1));
                int right = left + width;
                int top = (int) (y + m);
                int bottom = top + height;

                mRect.left = left-s;
                mRect.top = top-s;
                mRect.right = right+s;
                mRect.bottom = bottom+s;
                patch.draw(c, mRect);

                mRect2.left = left;
                mRect2.top = top;
                mRect2.right = right;
                mRect2.bottom = bottom;
                paint1.setColor(color);
                c.drawRect(mRect2, paint1);
            }

            private void drawRectMyStyleBack(Canvas c, float t){
                double k = t*2;
                k = Math.max(0, k);
                k = Math.min(1, k);
                double k1 = Math.pow(1-k, 0.5);

                int s = shadowWidth1;
                int left = (int) (x1 + k1*(x2-x1));
                int right = left + width;
                int top = y;
                int bottom = top + height;

                mRect.left = left-s;
                mRect.top = top-s;
                mRect.right = right+s;
                mRect.bottom = bottom+s;
                patch.draw(c, mRect);

                mRect2.left = left;
                mRect2.top = top;
                mRect2.right = right;
                mRect2.bottom = bottom;
                paint1.setColor(color);
                c.drawRect(mRect2, paint1);
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
