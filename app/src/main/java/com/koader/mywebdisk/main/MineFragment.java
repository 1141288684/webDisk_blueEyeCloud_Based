package com.koader.mywebdisk.main;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.koader.mywebdisk.R;
import com.koader.mywebdisk.httpUtils.HttpMethods;
import com.koader.mywebdisk.httpUtils.HttpUtil;
import com.koader.mywebdisk.login.LoginActivity;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static android.content.Context.MODE_PRIVATE;

public class MineFragment extends Fragment {

    /**
     * 我的页面
     */

    private DiskActivity activity;
    LinearLayout mHiddenLayout;
    float mDensity;
    int mHiddenViewMeasuredHeight;
    ImageView mIv;

    EditText oldPsw;
    EditText newPsw;
    EditText esPsw;
    AppCompatButton btnPsw;
    String cookie;
    String username;
    public MineFragment(String Cookie,DiskActivity diskActivity){
        activity=diskActivity;
        cookie=Cookie;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        SharedPreferences sf = activity.getSharedPreferences("autoData", MODE_PRIVATE);
        username = sf.getString("username","");
        View view = inflater.inflate(R.layout.web_mine,container,false);
        TextView user = view.findViewById(R.id.mineId);
        TextView downDir = view.findViewById(R.id.mineDownDir);
        user.setText(("用户名："+username));
        downDir.setText(("下载路径：内置存储目录/kuaDiskDownload/"));

        mHiddenLayout = view.findViewById(R.id.hidden);

        mDensity = getResources().getDisplayMetrics().density;
        mHiddenViewMeasuredHeight = (int) (mDensity * 280 + 0.5);
        mIv = view.findViewById(R.id.mtv);
        mIv.setOnClickListener(this::onClick);
        LinearLayout miLay = view.findViewById(R.id.mi_lay);
        miLay.setOnClickListener(view1 -> mIv.callOnClick());
        oldPsw = view.findViewById(R.id.mi_old_psw);
        newPsw = view.findViewById(R.id.mi_new_psw);
        esPsw = view.findViewById(R.id.mi_new_es_psw);
        btnPsw = view.findViewById(R.id.mi_btn_psw);
        btnPsw.setOnClickListener((view1 -> {
            if(oldPsw.getText().toString().equals(""))
                Snackbar.make(mIv,"旧密码不能为空",Snackbar.LENGTH_SHORT).show();
            if(!newPsw.getText().toString().equals(esPsw.getText().toString())){
                Snackbar.make(mIv,"两次密码不一致",Snackbar.LENGTH_SHORT).show();
            }
            password();
        }));
        return view;

    }

    public void password(){
        Retrofit retrofit = new Retrofit.Builder().baseUrl(HttpMethods.apiHost).build();
        HttpMethods methods = retrofit.create(HttpMethods.class);
        Call<ResponseBody> call = methods.password(cookie,oldPsw.getText().toString(),newPsw.getText().toString());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.code()!=200){
                    Snackbar.make(btnPsw,"旧密码错误",Snackbar.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getContext(),"修改完成",Toast.LENGTH_SHORT).show();
                    SharedPreferences autoSf = activity.getSharedPreferences("autoData",MODE_PRIVATE);
                    SharedPreferences.Editor ase = autoSf.edit();

                    ase.putBoolean("autoLogin",false);
                    ase.putBoolean("firstIn",true);
                    ase.apply();

                    SharedPreferences sharedPreferences = activity.getSharedPreferences(username+"Data", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("uuid","null");
                    editor.apply();
                    Intent intent = new Intent(activity, LoginActivity.class);
                    startActivity(intent);
                    activity.finish();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    public void onClick(View v) {
        if (mHiddenLayout.getVisibility() == View.GONE) {
            animateOpen(mHiddenLayout);
            animationIvOpen();
        } else {
            animateClose(mHiddenLayout);
            animationIvClose();
        }
    }

    private void animateOpen(View v) {
        v.setVisibility(View.VISIBLE);
        ValueAnimator animator = createDropAnimator(v, 0,
                mHiddenViewMeasuredHeight);
        animator.start();

    }

    private void animationIvOpen() {
        RotateAnimation animation = new RotateAnimation(0, 180,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        animation.setFillAfter(true);
        animation.setDuration(100);
        mIv.startAnimation(animation);
    }

    private void animationIvClose() {
        RotateAnimation animation = new RotateAnimation(180, 0,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        animation.setFillAfter(true);
        animation.setDuration(100);
        mIv.startAnimation(animation);
    }

    private void animateClose(final View view) {
        int origHeight = view.getHeight();
        ValueAnimator animator = createDropAnimator(view, origHeight, 0);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.GONE);
            }

        });
        animator.start();
    }

    private ValueAnimator createDropAnimator(final View v, int start, int end) {
        ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator arg0) {
                int value = (int) arg0.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
                layoutParams.height = value;
                v.setLayoutParams(layoutParams);

            }
        });
        return animator;
    }



}
