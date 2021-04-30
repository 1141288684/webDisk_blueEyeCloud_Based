package com.koader.mywebdisk.main;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.koader.mywebdisk.R;
import com.koader.mywebdisk.main.adapter.UploadAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

public class UploadFragment extends Fragment {

    /**
     * 上传页面
     */

    public RecyclerView recycler;
    private AppCompatActivity activity;
    private SmartRefreshLayout refresh_upload;

    UploadFragment(AppCompatActivity act){
        activity=act;
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden)
            refresh_upload.autoRefresh();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.web_upload,container,false);
        @SuppressLint("HandlerLeak") Handler handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                recycler.setAdapter(new UploadAdapter(activity,getContext(),false));
                refresh_upload.finishRefresh(true);
            }
        };
        refresh_upload = view.findViewById(R.id.uploadRefresh);
        refresh_upload.setOnRefreshListener(refreshLayout -> handler.sendMessage(handler.obtainMessage(1)));
        recycler = view.findViewById(R.id.upLoadRecycler);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        recycler.setAdapter(new UploadAdapter(activity,getContext(),false));
        return view;
    }


}
