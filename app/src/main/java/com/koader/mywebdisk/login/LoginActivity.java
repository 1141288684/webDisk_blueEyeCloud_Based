package com.koader.mywebdisk.login;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatTextView;

import com.google.gson.Gson;
import com.koader.mywebdisk.R;
import com.koader.mywebdisk.httpUtils.HttpMethods;
import com.koader.mywebdisk.httpUtils.HttpUtil;
import com.koader.mywebdisk.httpUtils.JsonBean;
import com.koader.mywebdisk.main.DiskActivity;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    /**
     * 登录activity
     */

    private TextInputEditText edtUser;
    private TextInputEditText edtPsw;
    private FloatingActionButton btnLogin;
    private AppCompatTextView btnRest;
    private ProgressBar progressBar;
    private ImageView pswCheck;
    private AppCompatCheckBox autoLogCheck;
    private boolean isChecked = false;



    @SuppressLint("ClickableViewAccessibility")
    protected void init(){
        progressBar = findViewById(R.id.progressBar);
        edtUser = findViewById(R.id.edtUsername);
        edtPsw = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRest = findViewById(R.id.btnRegister);
        LoginActOnClickListener listener = new LoginActOnClickListener();
        btnLogin.setOnClickListener(listener);
        btnRest.setOnClickListener(listener);

        autoLogCheck = findViewById(R.id.autoLogCheck);
        pswCheck = findViewById(R.id.pswCheck);
        pswCheck.setOnTouchListener((view, motionEvent) -> {
            if(edtPsw.getText()==null||edtPsw.getText().toString().equals(""))return false;
            if(!isChecked){
                edtPsw.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                isChecked = true;
            }else{
                edtPsw.setTransformationMethod(PasswordTransformationMethod.getInstance());
                isChecked = false;
            }
            edtPsw.setSelection(edtPsw.getText().length());
            return false;
        });

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }

        init();

        new LoginActOnClickListener().ifAutoLogin();

    }

    class LoginActOnClickListener implements View.OnClickListener{

        Context context;

        LoginActOnClickListener(){
            context = getApplicationContext();
        }

        @Override
        public void onClick(View view) {
            switch(view.getId()){
                case R.id.btnLogin:
                    btnLoginClick();break;
                case R.id.btnRegister:
                {
                    Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                    startActivity(intent);
                    break;
                }
            }
        }

        public void btnLoginClick(){
            String username = Objects.requireNonNull(edtUser.getText()).toString();
            String password = Objects.requireNonNull(edtPsw.getText()).toString();
            if(username.equals("")||password.equals("")){
                Toast.makeText(context,"用户名或密码不能为空",Toast.LENGTH_SHORT).show();
            }else {
                progressBar.setVisibility(View.VISIBLE);
                Login(username,password);
            }
        }

        public void ifAutoLogin(){
            SharedPreferences autoSf = getSharedPreferences("autoData",MODE_PRIVATE);
            if(autoSf.getBoolean("autoLogin",false)){
                autoLogCheck.setChecked(true);
                edtUser.setText(autoSf.getString("username",""));
                edtPsw.setText(autoSf.getString("password",""));
                btnLoginClick();
            }
        }

        public void Login(String username,String password) {

            SharedPreferences sharedPreferences = getSharedPreferences(username+"Data", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            String uuid = sharedPreferences.getString("uuid","null");

            Retrofit retrofit = new Retrofit.Builder().baseUrl(HttpMethods.remoteHost)
                    .addConverterFactory(GsonConverterFactory.create()).build();
            HttpMethods httpMethods = retrofit.create(HttpMethods.class);
            Call<ResponseBody> call = httpMethods.getUserCok(username, password,uuid);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    runOnUiThread(() -> {
                        if(response.body()==null){onFailure(call,new Exception()); return;}
                        HashMap<String,String> map = new HashMap<>();
                        try {
                            assert response.body() != null;
                            map = JsonBean.getUserdata(response.body().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        saveAutoLoginState(username,password);

                        assert uuid != null;
                        if(uuid.equals("null")){
                            editor.putString("username",username);
                            editor.putString("password",password);
                            editor.putString("uuid",map.get("uuid"));
                            editor.putString("Cookie",map.get("Cookie"));
                        }else {
                            editor.putString("Cookie",map.get("Cookie"));
                        }
                        editor.apply();
                        progressBar.setVisibility(View.INVISIBLE);
                        jumpToDisk(map.get("uuid"),map.get("Cookie"));

                    });


                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    runOnUiThread(() -> {
                        Toast.makeText(context, "连接失败", Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.INVISIBLE);
                    });
                }
            });


        }

        public void saveAutoLoginState(String username,String password){
            SharedPreferences sf = getSharedPreferences("autoData", MODE_PRIVATE);
            SharedPreferences.Editor edit = sf.edit();
            if(autoLogCheck.isChecked()&&sf.getBoolean("firstIn",true)){
                edit.putBoolean("firstIn",false);
                edit.putBoolean("autoLogin",true);
            }
            edit.putString("username",username);
            edit.putString("password",password);
            edit.apply();
        }
    }


    private void jumpToDisk(String uuid,String cookie){
        Toast.makeText(this,"登陆成功",Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(LoginActivity.this, DiskActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("uuid",uuid);
        bundle.putString("Cookie",cookie);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }
}