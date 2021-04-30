package com.koader.mywebdisk.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Icon;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.LinearLayout;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.koader.mywebdisk.R;
import com.koader.mywebdisk.main.views.CustomDialog;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;



public class FilePageAdapter extends RecyclerView.Adapter<FilePageAdapter.FileViewHolder> {


    /**
     * 云盘页面的recyclerView的Adapter
     */

    public Context lContext;
    public ArrayList<HashMap<String,String>> data;
    public Handler handler;


    public FilePageAdapter(Context context, ArrayList<HashMap<String,String>> data,Handler handler){
        lContext = context;
        this.data = data;
        this.handler = handler;
    }

    public ArrayList<HashMap<String,String>> getData(){
        return this.data;
    }

    @NonNull
    @Override
    public FileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(lContext).inflate(R.layout.file_page_item,parent,false);
        return new FileViewHolder(view);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull FileViewHolder holder, int position) {
        holder.filePageName.setText(data.get(position).get("name"));
        holder.filePageDate.setText(data.get(position).get("createTime"));

        holder.filePageIcon.setImageDrawable(lContext.getDrawable(R.drawable.dir_icon));
        if(Objects.equals(data.get(position).get("dir"), "false")){
            String[] args = Objects.requireNonNull(data.get(position).get("name")).split("\\.");
            String fileEx = args[args.length-1];
            if(fileEx.equals("pdf")){
                holder.filePageIcon.setImageDrawable(lContext.getDrawable(R.drawable.pdf_file_icon));
            }else if(fileEx.equals("ppt")){
                holder.filePageIcon.setImageDrawable(lContext.getDrawable(R.drawable.ppt_file_icon));
            }else if(fileEx.equals("mp4")||fileEx.equals("mkv")){
                holder.filePageIcon.setImageDrawable(lContext.getDrawable(R.drawable.video_file_icon));
            }else if(fileEx.contains("xls")){
                holder.filePageIcon.setImageDrawable(lContext.getDrawable(R.drawable.xls_file_icon));
            }else if(fileEx.contains("doc")){
                holder.filePageIcon.setImageDrawable(lContext.getDrawable(R.drawable.doc_file_icon));
            }else if(fileEx.equals("jpg")||fileEx.equals("png")){
                holder.filePageIcon.setImageDrawable(lContext.getDrawable(R.drawable.img_file_icon));
            }else if(fileEx.equals("mp3")||fileEx.equals("wav")||fileEx.equals("flac")){
                holder.filePageIcon.setImageDrawable(lContext.getDrawable(R.drawable.audio_file_icon));
            }else {
                holder.filePageIcon.setImageDrawable(lContext.getDrawable(R.drawable.file_icon));
            }

            String s = data.get(position).get("size");
            assert s !=null;
            double size = Double.parseDouble(s);
            int i=0;
            while(size>=1024){
                size = size/1024;
                i++;
            }
            size = new BigDecimal(size).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
            switch (i){
                case 0:s=size+"B";break;
                case 1:s=size+"KB";break;
                case 2:s=size+"MB";break;
                case 3:s=size+"GB";break;
            }
            holder.filePageSize.setText(s);
        }else {
            holder.filePageSize.setText("");
        }
        holder.FilePageItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Objects.requireNonNull(data.get(position).get("dir")).equals("true")){
                    Message msg = handler.obtainMessage(2,data.get(position).get("uuid")+"!"+data.get(position).get("name"));
                    handler.sendMessage(msg);
                }else {
                    handler.sendMessage(handler.obtainMessage(3,data.get(position).get("uuid")+"!"+data.get(position).get("name")));
                }
            }
        });
        holder.FilePageItem.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(Objects.requireNonNull(data.get(position).get("dir")).equals("true")){
                    handler.sendMessage(handler.obtainMessage(3,data.get(position).get("uuid")));
                }
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class FileViewHolder extends RecyclerView.ViewHolder{
        AppCompatImageView filePageIcon;
        AppCompatTextView filePageName;
        AppCompatTextView filePageDate;
        LinearLayout FilePageItem;
        AppCompatTextView filePageSize;

        public FileViewHolder(@NonNull View itemView) {
            super(itemView);
            filePageIcon = itemView.findViewById(R.id.filePageIcon);
            filePageName = itemView.findViewById(R.id.filePageName);
            filePageDate = itemView.findViewById(R.id.filePageDate);
            FilePageItem = itemView.findViewById(R.id.FilePageItem);
            filePageSize = itemView.findViewById(R.id.filePageSize);
        }
    }
}
