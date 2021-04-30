package com.koader.mywebdisk.main.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AlertDialogLayout;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;

import com.google.android.material.snackbar.Snackbar;
import com.koader.mywebdisk.R;
import com.koader.mywebdisk.httpUtils.HttpMethods;

import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CustomDialog extends AlertDialog {

    /**
     * 重命名的dialog
     */

    AppCompatTextView deleteFile;
    AlertDialog dialog;
//    Handler Lhandler;
    AppCompatTextView negative;
    AppCompatTextView download;
    AppCompatTextView btnRename;
    Context lContext;

    @SuppressLint("InflateParams")
    public CustomDialog(@NonNull Context context) {
        super(context);
        lContext = context;
        dialog = new AlertDialog.Builder(context).create();
//        Lhandler=handler;
        View view = LayoutInflater.from(context).inflate(R.layout.file_selected_dialog,null);
        deleteFile = view.findViewById(R.id.dialog_delete_file);
        negative = view.findViewById(R.id.negative);
        download = view.findViewById(R.id.dialog_down_file);
        btnRename = view.findViewById(R.id.dialog_rename_file);
        negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancel();
            }
        });

        dialog.setView(view);
    }

    public void setOnDeleteListener(View.OnClickListener onClickListener){
        deleteFile.setOnClickListener(onClickListener);
    }

    public void setOnDownloadListener(View.OnClickListener onClickListener){
        download.setOnClickListener(onClickListener);
    }

    public void setOnRenameListener(View.OnClickListener onClickListener){
        btnRename.setOnClickListener(onClickListener);
    }

    public void show(){
        this.dialog.show();
    }

    public void cancel(){
        this.dialog.cancel();
    }

    @SuppressLint("InflateParams")
    public void renameAFile(String cookie,String uuid,String oldName,Callback<ResponseBody> callback){
        AlertDialog alertDialog = new AlertDialog.Builder(lContext).create();
//        Lhandler=handler;
        View view = LayoutInflater.from(lContext).inflate(R.layout.rename_dir_or_file,null);
        AppCompatEditText rename = view.findViewById(R.id.rename_name);
        AppCompatTextView rename_cancel = view.findViewById(R.id.rename_cancel);
        AppCompatTextView rename_confirm = view.findViewById(R.id.rename_confirm);
        rename.setText(oldName);
        rename_cancel.setOnClickListener((view1 -> {
            alertDialog.cancel();
        }));
        rename_confirm.setOnClickListener((view1 -> {
            String name = Objects.requireNonNull(rename.getText()).toString();
            if(!name.equals("")){
                renameFile(cookie, uuid, name, callback);
                alertDialog.cancel();
            }
        }));
        alertDialog.setView(view);
        alertDialog.setCancelable(true);
        alertDialog.show();
    }

    public void renameFile(String cookie,String uuid,String name,Callback<ResponseBody> callback){
        Retrofit retrofit = new Retrofit.Builder().baseUrl(HttpMethods.apiHost).build();
        HttpMethods methods = retrofit.create(HttpMethods.class);
        Call<ResponseBody> call = methods.renameFile(cookie, uuid, name);
        call.enqueue(callback);
    }

}
