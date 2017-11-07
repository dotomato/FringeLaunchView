# FringeLaunchView

一个漂亮的条纹动画。

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

