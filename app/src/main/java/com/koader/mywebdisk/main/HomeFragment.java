package com.koader.mywebdisk.main;

import android.os.Bundle;
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

public class HomeFragment extends Fragment {
    /**
     * 主页面
     */

    AppCompatActivity activity;
    RecyclerView new_add;
    RecyclerView new_down;
    HomeFragment(AppCompatActivity lActivity){
        activity = lActivity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.web_home,container,false);
        new_add = view.findViewById(R.id.new_up);
        new_down = view.findViewById(R.id.new_down);
        new_add.setLayoutManager(new LinearLayoutManager(getContext()));
        new_add.setAdapter(new UploadAdapter(activity,getContext(),true));
        new_down.setLayoutManager(new LinearLayoutManager(getContext()));
        new_down.setAdapter(new DownloadAdapter(activity,getContext(),true));
        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden){
            new_add.setAdapter(new UploadAdapter(activity,getContext(),true));
            new_down.setAdapter(new DownloadAdapter(activity,getContext(),true));
        }
    }
}
