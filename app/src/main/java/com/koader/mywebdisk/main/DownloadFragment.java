package com.koader.mywebdisk.main;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.koader.mywebdisk.R;
import com.koader.mywebdisk.main.adapter.DownloadAdapter;
import com.koader.mywebdisk.main.adapter.UploadAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

public class DownloadFragment extends Fragment {

    /**
     * 显示下载记录的页面
     */

    public RecyclerView recycler;
    private AppCompatActivity activity;
    private SmartRefreshLayout refresh_download;

    DownloadFragment(AppCompatActivity act){
        activity=act;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden)
            refresh_download.autoRefresh();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.web_download,container,false);
        @SuppressLint("HandlerLeak") Handler handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                recycler.setAdapter(new DownloadAdapter(activity,getContext(),false));
                refresh_download.finishRefresh(true);
            }
        };
        refresh_download = view.findViewById(R.id.downloadRefresh);
        refresh_download.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                handler.sendMessage(handler.obtainMessage(1));
            }
        });
        recycler = view.findViewById(R.id.downLoadRecycler);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        recycler.setAdapter(new DownloadAdapter(activity,getContext(),false));
        return view;
    }


}
