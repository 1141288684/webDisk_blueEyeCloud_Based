package com.koader.mywebdisk.main.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Space;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;

import com.koader.mywebdisk.R;

import java.util.Objects;

@SuppressLint("InflateParams")
public class NewDirDialog extends AlertDialog {

    /**
     * 新建文件夹dialog
     */

    AlertDialog dialog;
    AppCompatEditText new_dir_name;
    AppCompatTextView new_dir_confirm;
    AppCompatTextView new_dir_cancel;

    public NewDirDialog(@NonNull Context context) {
        super(context);
        View view = LayoutInflater.from(context).inflate(R.layout.new_a_directory,null);
        new_dir_name = view.findViewById(R.id.new_a_dir_name);
        new_dir_confirm = view.findViewById(R.id.new_dir_confirm);
        new_dir_cancel = view.findViewById(R.id.new_dir_cancel);
        new_dir_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancel();
            }
        });
        dialog = new AlertDialog.Builder(context).create();
        dialog.setView(view);
    }

    public void setOnConfirmListener(View.OnClickListener onClickListener){
        new_dir_confirm.setOnClickListener(onClickListener);
    }

    public String getDirname(){
        return Objects.requireNonNull(new_dir_name.getText()).toString();
    }
    public void show(){
        this.dialog.show();
    }

    public void cancel(){
        this.dialog.cancel();
    }
}
