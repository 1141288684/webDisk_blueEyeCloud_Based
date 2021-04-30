package com.koader.mywebdisk.login;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.koader.mywebdisk.R;
import com.koader.mywebdisk.httpUtils.HttpMethods;

import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RegisterActivity extends AppCompatActivity {

    /**
     * 注册activity
     */

    private TextInputEditText resUser;
    private TextInputEditText resPass;
    private TextInputEditText resEnsPass;
    private FloatingActionButton goRes;

    private String username;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        goRes = findViewById(R.id.goRegister);
        resUser = findViewById(R.id.resUsername);
        resPass = findViewById(R.id.resPassword);
        resEnsPass = findViewById(R.id.resEnsurePassword);

        goRes.setOnClickListener(view -> {
            if(isLegal()){
                register();
            }
        });
    }

    public boolean isLegal(){
        username = Objects.requireNonNull(resUser.getText()).toString();
        password = Objects.requireNonNull(resPass.getText()).toString();
        String ensPass = Objects.requireNonNull(resEnsPass.getText()).toString();
        if(!ensPass.equals(password)){
            Snackbar.make(goRes,"两次密码不一致",Snackbar.LENGTH_SHORT).show();
            return false;
        }else{
            Snackbar.make(goRes,"用户名或密码非法",Snackbar.LENGTH_SHORT).show();
            return !username.equals("");
        }
    }

    public void register(){
        Retrofit retrofit = new Retrofit.Builder().baseUrl(HttpMethods.apiHost).build();
        HttpMethods methods = retrofit.create(HttpMethods.class);
        Call<ResponseBody> call = methods.register(username, password);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Snackbar.make(goRes,"注册成功",Snackbar.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Snackbar.make(goRes,"注册失败",Snackbar.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}