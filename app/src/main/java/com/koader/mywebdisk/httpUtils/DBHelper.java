package com.koader.mywebdisk.httpUtils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;

public class DBHelper extends SQLiteOpenHelper {
    private static final String tag="DBHelper";

    public DBHelper(Context context) {

        /*
         * 参数1：上下文
         * 参数2：数据库文件名
         * 参数3：游标工厂，可以设置为null，使用默认的
         * 参数4：版本号
         *
         *
         */
        super(context, "History.db", null, 2);
    }

    private static boolean mainTmpDirSet = false;
    @Override
    public SQLiteDatabase getReadableDatabase() {
        if (!mainTmpDirSet) {
            boolean rs = new File("/data/data/com.koader.mywebdisk/databases/main").mkdir();
            Log.d("ahang", rs + "");
            super.getReadableDatabase().execSQL("PRAGMA temp_store_directory = '/data/data/com.koader.mywebdisk/databases/main'");
            mainTmpDirSet = true;
            return super.getReadableDatabase();
        }
        return super.getReadableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        Log.i(tag, "数据库创建了！！！");

    }

    /**
     * 库升级时，系统自动调用
     * 一般写，修改表结构的语句
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.i(tag, "数据库升级了！！！");
    }

}
