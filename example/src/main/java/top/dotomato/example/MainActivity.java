package top.dotomato.example;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import top.dotomato.library.FringeLaunchView;

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
