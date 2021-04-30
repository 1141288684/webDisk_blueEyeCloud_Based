package com.koader.mywebdisk.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.koader.mywebdisk.R;
import com.koader.mywebdisk.main.inter.FileDir;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class DiskActivity extends AppCompatActivity implements OnFileBackPress {

    /**
     * 实现核心功能的activity
     * 五个页面，五个fragment
     * 用tabLayout切换fragment
     */


    private TabLayout tab_disk;
    private ArrayList<Fragment> fragments = new ArrayList<>();

    private String uuid;
    private String cookie;
    public FilePageFragment fileFragment;

    AppCompatTextView filePageDir;
    AppCompatTextView currentDir;

    public String createNotificationChannel(String channelID, String channelNAME, int level) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel(channelID, channelNAME, level);
            manager.createNotificationChannel(channel);
            return channelID;
        } else {
            return null;
        }
    }

    private void init(){
//        SQL_Init();
        fragments.add(new HomeFragment(this));
        fragments.add(new FilePageFragment(uuid,cookie,this,this));
        fragments.add(new DownloadFragment(this));
        fragments.add(new UploadFragment(this));
        fragments.add(new MineFragment(cookie,this));
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        for(int i=0;i<fragments.size();i++){
            transaction.add(R.id.fragment_disk,fragments.get(i));
            transaction.hide(fragments.get(i));
        }
        transaction.show(fragments.get(0));
        transaction.commit();
        tab_disk = findViewById(R.id.tab_disk);
        filePageDir = findViewById(R.id.filePageDir);
        currentDir = findViewById(R.id.currentDir);
        tab_disk.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                getSupportFragmentManager().beginTransaction().show(fragments.get(tab.getPosition())).commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                getSupportFragmentManager().beginTransaction().hide(fragments.get(tab.getPosition())).commit();
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }




    @SuppressLint("InflateParams")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disk);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        uuid = bundle.getString("uuid");
        cookie = bundle.getString("Cookie");

        init();






    }

    public void setCurrentDir(String Dir,boolean isBack){
        String str;

        if(!isBack){
            str = currentDir.getText()+"/"+Dir;
        }else {
            String s = currentDir.getText().toString();
            str = s.substring(0,s.lastIndexOf("/"));
        }
        currentDir.setText(str);

    }


    @Override
    public void onBackPressHistory(FilePageFragment fragment) {
        this.fileFragment = fragment;
    }

    @Override
    public void onBackPressed() {
        if(!fragments.get(1).isHidden())
        {
            if(fileFragment.onBackPressed()){
                return;
            }
        }
        super.onBackPressed();
    }




}