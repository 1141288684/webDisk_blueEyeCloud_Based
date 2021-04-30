package com.koader.mywebdisk.main.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.koader.mywebdisk.R;
import com.koader.mywebdisk.httpUtils.DBHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Set;

public class UploadAdapter extends RecyclerView.Adapter<UploadAdapter.mViewHolder> {

    /**
     * 上传页面
     * RecyclerView的Adapter
     */

    Cursor cursor;
    Context lContext;
    HashMap<String,HashMap<String,String>> data = new HashMap<>();
    LinkedList<String> pos = new LinkedList<>();
    private AppCompatActivity activity;
    public void getData(boolean is_home){
        SQLiteDatabase database = sqlGetInstance();
        database.execSQL("create table if not exists uploadHistory('[name] varchar(100)','[path] varchar(1000)','[date] TimeStamp')");
        if (is_home){
            cursor=database.rawQuery("select * from uploadHistory order by '[date]' asc limit 0,10",null);
        }
        else {
            cursor=database.rawQuery("select * from uploadHistory order by '[date]' asc limit 0,30",null);
        }
        while (cursor.moveToNext()){
            pos.add(cursor.getString(0));
            HashMap<String,String> map = new HashMap<>();
            map.put("path",cursor.getString(1));
            map.put("date",cursor.getString(2));
            data.put(cursor.getString(0),map);
        }
        Collections.reverse(pos);
        database.close();
    }

    public SQLiteDatabase sqlGetInstance(){
        DBHelper helper = new DBHelper(lContext);
        return helper.getWritableDatabase();
    }



    public UploadAdapter(AppCompatActivity activity, Context context,boolean is_home){
        lContext = context;
        this.activity = activity;
        getData(is_home);
    }

    @NonNull
    @Override
    public UploadAdapter.mViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(lContext).inflate(R.layout.upload_item,parent,false);
        return new mViewHolder(view);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull UploadAdapter.mViewHolder holder, int position) {
        String filename = pos.get(position);
        String[] file = filename.split("\\.");
        String fileEx = file[file.length-1];
        holder.uploadName.setText(filename);
        if(fileEx.equals("pdf")){
            holder.uploadIcon.setImageDrawable(lContext.getDrawable(R.drawable.pdf_file_icon));
        }else if(fileEx.equals("ppt")){
            holder.uploadIcon.setImageDrawable(lContext.getDrawable(R.drawable.ppt_file_icon));
        }else if(fileEx.equals("mp4")||fileEx.equals("mkv")){
            holder.uploadIcon.setImageDrawable(lContext.getDrawable(R.drawable.video_file_icon));
        }else if(fileEx.contains("xls")){
            holder.uploadIcon.setImageDrawable(lContext.getDrawable(R.drawable.xls_file_icon));
        }else if(fileEx.contains("doc")){
            holder.uploadIcon.setImageDrawable(lContext.getDrawable(R.drawable.doc_file_icon));
        }else if(fileEx.equals("jpg")||fileEx.equals("png")){
            holder.uploadIcon.setImageDrawable(lContext.getDrawable(R.drawable.img_file_icon));
        }else if(fileEx.equals("mp3")||fileEx.equals("wav")||fileEx.equals("flac")){
            holder.uploadIcon.setImageDrawable(lContext.getDrawable(R.drawable.audio_file_icon));
        }else {
            holder.uploadIcon.setImageDrawable(lContext.getDrawable(R.drawable.file_icon));
        }
        holder.uploadDate.setText(data.get(pos.get(position)).get("date"));

    }

    @Override
    public int getItemCount() {
        return pos.size();
    }

    class mViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;
        AppCompatTextView uploadName;
        AppCompatTextView uploadDate;
        LinearLayout uploadItem;
        AppCompatImageView uploadIcon;
        public mViewHolder(@NonNull View itemView) {
            super(itemView);
            uploadDate = itemView.findViewById(R.id.uploadDate);
            uploadName = itemView.findViewById(R.id.uploadFileName);
            uploadItem = itemView.findViewById(R.id.uploadPageItem);
            uploadIcon = itemView.findViewById(R.id.uploadItem);
        }
    }
}
