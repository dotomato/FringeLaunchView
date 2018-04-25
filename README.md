# FringeLaunchView

一个漂亮的条纹动画。可以用来显示应用的启动界面上。
刚接触开源没多久，第一次把自己的代码做成了库发布出来，有不足的地方请大家多多指教喵的说~~ ^_^

![image](https://github.com/dotomato/FringeLaunchView/raw/master/demo.gif)

## 引用：

点击下方按钮查看引用方法。

[![](https://jitpack.io/v/dotomato/FringeLaunchView.svg)](https://jitpack.io/#dotomato/FringeLaunchView)

在你的根项目的build.gradle里增加：

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
    
在app的build.gradle里增加

	dependencies {
			compile 'com.github.dotomato:FringeLaunchView:1.0.1'
		}

You get it!

## 使用方法：

在布局文件里：

    <FrameLayout
        xmlns:android="http://schemas.android.com/apk/res/android"
        xmlns:app="http://schemas.android.com/apk/res-auto"
        xmlns:tools="http://schemas.android.com/tools"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        tools:context="top.dotomato.example.MainActivity">

        <top.dotomato.library.FringeLaunchView
            android:id="@+id/fringeLaunchView"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            app:mainH1="0.9"
            app:mainH2="0.98"
            app:degree="20"
            app:fringeCount="15"
            app:intervalHeight="150"
            app:fringeHeight="300"
            app:fringeWidth="2500"
            app:slideLength="2000"
            app:overlapWidth="700"
            app:enterDuration="1.5"
            app:exitDuration="0.7"
            app:previewTime="2"
            />

    </FrameLayout>
    
在代码里：

    public class MainActivity extends AppCompatActivity implements FringeLaunchView.OnExitCallback {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            FringeLaunchView flv = findViewById(R.id.fringeLaunchView);
            flv.setOnExitCallback(this);

            flv.start();
        }

        @Override
        public void onExit() {
            Toast.makeText(this, "FringeLaunchView Exit", Toast.LENGTH_SHORT).show();
        }
    }

# 把CSDN上面的内容也粘过来吧，万一哪天CSDN倒闭了呢

### **背景**
用过很多APP了吧，很多一开始启动的时候总是显示一页广告，停着好几秒@(￣-￣)@……然后想关的话，还得手动地去向右上角那小小的、一点点的关闭按钮，只要稍微点错一点点就进入了广告，简直ZZ(╯‵□′)╯︵┻━┻。

这样的情景，真的是烦@(￣-￣)@哎……真不知道产品经理是怎么样的，比如像我这样的人，一想到打开一个APP就要看好几秒广告，就不想打开这个APP了@(￣-￣)@。所以这样的设计真的是反效果。

做不能一个漂亮的启动界面吗？这样用户看了之后觉得赏心悦目，打开APP的主动性也会高很多吧。
刚好最近在和导师撕逼ᐠ( ᐢ ᵕ ᐢ )ᐟ （用力怼，出奇迹~），闲得无聊地瞎搞APP，便搞出了一个控件，总之先放个GIF图看看效果~

![GIT预览](https://github.com/dotomato/FringeLaunchView/raw/master/demo.gif)

这个效果调了好久，同事看了之后都觉得不错喵的说~
而且可以通过jitpack可以很方便地在Android Studio里添加依赖喵的说~
（迷之音：“不来一发star吗少年”，并且向你扔出了一个地址：https://github.com/dotomato/FringeLaunchView


### **代码/原理**：
本质上是一个完全自定义的View，所有效果在onDraw()里画出来。
首先定义一个Fringe类，用来帮助我们画单个的条纹。


```
  class Fringe {
            
            ...

            private float randF(){
                return (float) Math.random();
            }

			//初始化的时候要输入这个条纹的开始的横坐标、结束的横坐标，纵坐标，第几个条纹（虽然现在并没什么卵用），第几组条纹
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
```
由于整个效果是分成上下两层条纹的，所以在初始化的时候需要传递一些信息进去。
处于下层的条纹要暗一些，就比较符合视觉效果。然后每个条纹在需要对自条的高度、进入的延时、速度巴拉巴拉的做一下随机化，每个条纹的个性就出来了~这些参数在之后计算坐标的时候会用到。

然后是比较关键的绘制部分代码：

```
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
```

两个函数，分别是进入时的代码和退出时的代码。
先是做了一些曲线的计算工作，嘛，这里自己慢慢调就好，不要问我为什么这里是pow这里是cos。感觉，凭感觉~ᐠ( ᐢ ᵕ ᐢ )ᐟ （其实我是先python画图看一下大概曲线符合弹跳动画的形状，然后再在真机上慢慢调整@(￣-￣)@

然后再根据曲线计算当前时刻的坐标。
然后是绘制，绘制部份分为两步，先是用shadowPatch来绘制阴影层，shadowPatch是一个NinePatch。然后再用普通的canvas.drawRect来绘制彩色的条纹。

有了Fringe这个类之后，就可以再用一个FringeGroup类来管理每一层的绘制了，代码如下：

```
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
```

每一个层有不同的旋转角度，以及原点坐标，这样子就能通过旋转移动两个层，把整个屏幕盖住（迷之音：简直是天才的设计wwwwww
以及fringeState这个是一个状态值 ，由于这个控制件涉及到进入过程、退出过程、用户点击，所以状态还是比较复杂的@(￣-￣)@

所需要实现的交互逻辑是：自动开始“进入”过程。如果在“进入”过程中用户点击了屏幕，则“进入”过程不被打断，在结束后自动到“退出”过程。如果在“进入”过程中用户没有点击屏幕，则“进入”过程完毕后，停留在完毕状态，等待用户点击，才到“退出”过程。

所以这就需要一个状态机，fringeState就是状态值，含义如下：

 1：正在进入，用户没有点击
 2：正在进入，用户已经点击
 3：进入完毕，用户没有点击
 4：正在退出
 5：退出完毕

完成了FringeGroup之后，再在View里进行一下属性的获取，初始化，触模事件的处理就行了。关键代码如下：

属性的获取：

```
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
```
话说安卓这里真的没有一个更简单的方法来完成属性的获取吗？@(￣-￣)@  我改个属性名，改个类型都很麻烦诶……

然后是两个FrigneGroup的初始化：

```
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
```

在onDraw里View本身还需要画一个白底来盖住底下的View，在退出的时候白底渐变消失。

```
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
```
