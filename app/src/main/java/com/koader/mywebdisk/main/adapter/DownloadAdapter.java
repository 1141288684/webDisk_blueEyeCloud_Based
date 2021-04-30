package com.koader.mywebdisk.main.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

public class DownloadAdapter extends RecyclerView.Adapter<DownloadAdapter.mViewHolder>{

    /**
     * 下载页面
     * RecyclerView的Adapter
     */

    Cursor cursor;
    Context lContext;
    HashMap<String,HashMap<String,String>> data = new HashMap<>();
    LinkedList<String> pos = new LinkedList<>();
    private AppCompatActivity activity;
    public void getData(boolean is_home){
        SQLiteDatabase database = sqlGetInstance();
        database.execSQL("create table if not exists downloadHistory('[name] varchar(100)','[path] varchar(1000)','[date] TimeStamp')");
        if(is_home){
            cursor=database.rawQuery("select * from downloadHistory order by '[date]' asc limit 0,10",null);
        }
        else {
            cursor=database.rawQuery("select * from downloadHistory order by '[date]' asc limit 0,30",null);
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



    public DownloadAdapter(AppCompatActivity activity, Context context,boolean is_home){
        lContext = context;

        this.activity = activity;
        getData(is_home);
    }

    @NonNull
    @Override
    public DownloadAdapter.mViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(lContext).inflate(R.layout.download_item,parent,false);
        return new DownloadAdapter.mViewHolder(view);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull DownloadAdapter.mViewHolder holder, int position) {
        String filename = pos.get(position);
        String[] file = filename.split("\\.");
        String fileEx = file[file.length-1];
        holder.downloadName.setText(filename);
//        holder.uploadDate.setText(data.get(position).get("date"));
        if(fileEx.equals("pdf")){
            holder.downloadIcon.setImageDrawable(lContext.getDrawable(R.drawable.pdf_file_icon));
        }else if(fileEx.equals("ppt")){
            holder.downloadIcon.setImageDrawable(lContext.getDrawable(R.drawable.ppt_file_icon));
        }else if(fileEx.equals("mp4")||fileEx.equals("mkv")){
            holder.downloadIcon.setImageDrawable(lContext.getDrawable(R.drawable.video_file_icon));
        }else if(fileEx.contains("xls")){
            holder.downloadIcon.setImageDrawable(lContext.getDrawable(R.drawable.xls_file_icon));
        }else if(fileEx.contains("doc")){
            holder.downloadIcon.setImageDrawable(lContext.getDrawable(R.drawable.doc_file_icon));
        }else if(fileEx.equals("jpg")||fileEx.equals("png")){
            holder.downloadIcon.setImageDrawable(lContext.getDrawable(R.drawable.img_file_icon));
        }else if(fileEx.equals("mp3")||fileEx.equals("wav")||fileEx.equals("flac")){
            holder.downloadIcon.setImageDrawable(lContext.getDrawable(R.drawable.audio_file_icon));
        }else {
            holder.downloadIcon.setImageDrawable(lContext.getDrawable(R.drawable.file_icon));
        }
        holder.downloadDate.setText(data.get(pos.get(position)).get("date"));

    }

    @Override
    public int getItemCount() {
        return pos.size();
    }

    class mViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;
        AppCompatTextView downloadName;
        AppCompatTextView downloadDate;
        LinearLayout downloadItem;
        AppCompatImageView downloadIcon;
        public mViewHolder(@NonNull View itemView) {
            super(itemView);
            downloadDate = itemView.findViewById(R.id.downloadDate);
            downloadName = itemView.findViewById(R.id.downloadFileName);
            downloadItem = itemView.findViewById(R.id.downloadPageItem);
            downloadIcon = itemView.findViewById(R.id.downloadItem);
        }
    }
}
