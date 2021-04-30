package com.koader.mywebdisk.main.views;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.koader.mywebdisk.R;

public class DownloadNotice extends Notification{

    /**
     * 上传or下载通知服务
     */

    public NotificationManager mNotificationManager;
    public Notification mNotification;
    public RemoteViews mRemoteViews;
    public Context lContext;
    public Notification.Builder builder;
    public Notification notice;

    static int lid=1;
    private int id;

    boolean is;
    NotificationCompat.Builder notification;
    NotificationManagerCompat notificationManager;
    /**
     * 系统下拉栏自定义的通知
     */
    public DownloadNotice(Context context,String channelId,String filename) {
        lContext = context;


        notification = new NotificationCompat.Builder(context, channelId)
//                .setContentTitle("正在下载")
                .setContentText(filename)
//                .setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(NotificationCompat.PRIORITY_MAX)
//                .addAction(R.drawable.icon, "按钮", pendingIntent)
                .setAutoCancel(true);
        // Issue the initial notification with zero progress
        int PROGRESS_MAX = 100;
        int PROGRESS_CURRENT = 0;
        notification.setProgress(PROGRESS_MAX, PROGRESS_CURRENT, true);
        notificationManager = NotificationManagerCompat.from(context);




    }
    public void show(boolean isDownload){

        is=isDownload;
        if(isDownload)
            notification.setContentTitle("正在下载");
        else
            notification.setContentTitle("正在上传");
        this.id=lid++;
        notificationManager.notify(this.id, notification.build());
    }
    public void setProgress(int percent){
        mRemoteViews.setTextViewText(R.id.text_content, "下载进度");
        mRemoteViews.setProgressBar(R.id.pb, 100, percent, false);

        mNotification.contentView = mRemoteViews;
        mNotificationManager.notify(Notification.FLAG_AUTO_CANCEL,mNotification);

    }

    public void finish(){

        if(is)
            notification.setContentTitle("下载完成");
        else
            notification.setContentTitle("上传完成");
        notification.setProgress(100,100,false);
        notificationManager.notify(this.id,notification.build());
    }
}
