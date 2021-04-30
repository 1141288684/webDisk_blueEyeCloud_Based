package com.koader.mywebdisk;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.koader.mywebdisk.login.LoginActivity;
import com.koader.mywebdisk.main.DiskActivity;

public class MainActivity extends AppCompatActivity {

    /**
     * 3秒广告页并自动登录如果已选择自动登录的话
     */

    TextView mCountDownTextView;
    MyCountDownTimer mCountDownTimer;
    private boolean isIntentJumping = false;

    public void ifAutoLogin(){
        SharedPreferences autoSf = getSharedPreferences("autoData",MODE_PRIVATE);
        if(autoSf.getBoolean("autoLogin",false)){
            String username = autoSf.getString("username","");
            SharedPreferences userdata = getSharedPreferences(username+"Data",MODE_PRIVATE);
            Intent intent = new Intent(MainActivity.this, DiskActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("uuid",userdata.getString("uuid","null"));
            bundle.putString("Cookie",userdata.getString("Cookie","null"));
            intent.putExtras(bundle);
            startActivity(intent);
        }else {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy() {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        mCountDownTextView = (TextView) findViewById(R.id.start_skip_count_down);

        mCountDownTextView.setText("3s 跳过");
        //创建倒计时类
        mCountDownTimer = new MyCountDownTimer(3000, 1000);
        mCountDownTimer.start();
        //这是一个 Handler 里面的逻辑是从 Splash 界面跳转到 Main 界面，这里的逻辑每个公司基本上一致
        Handler tmpHandler = new Handler();
        mCountDownTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isIntentJumping)return;
                ifAutoLogin();
                isIntentJumping=true;
                finish();
            }
        });

        tmpHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mCountDownTextView.callOnClick();
                    }
                });
            }
        }, 3000);


    }

    class MyCountDownTimer extends CountDownTimer {
        /**
         * @param millisInFuture
         *   表示以「 毫秒 」为单位倒计时的总数
         *   例如 millisInFuture = 1000 表示1秒
         *
         * @param countDownInterval
         *   表示 间隔 多少微秒 调用一次 onTick()
         *   例如: countDownInterval = 1000 ; 表示每 1000 毫秒调用一次 onTick()
         *
         */

        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }


        public void onFinish() {
            mCountDownTextView.setText("0s 跳过");
        }

        public void onTick(long millisUntilFinished) {
            mCountDownTextView.setText( millisUntilFinished / 1000 + "s 跳过");
        }

    }
}