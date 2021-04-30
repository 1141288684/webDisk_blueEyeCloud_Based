package com.koader.mywebdisk.main;

import android.annotation.SuppressLint;

import android.app.NotificationManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.koader.mywebdisk.R;
import com.koader.mywebdisk.httpUtils.DBHelper;
import com.koader.mywebdisk.httpUtils.FileUtils;
import com.koader.mywebdisk.httpUtils.HttpMethods;
import com.koader.mywebdisk.httpUtils.HttpUtil;
import com.koader.mywebdisk.httpUtils.JsonBean;
import com.koader.mywebdisk.login.LoginActivity;
import com.koader.mywebdisk.main.inter.FileDir;
import com.koader.mywebdisk.main.views.CustomDialog;
import com.koader.mywebdisk.main.views.DownloadNotice;
import com.koader.mywebdisk.main.views.NewDirDialog;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static android.app.Activity.RESULT_OK;

public class FilePageFragment extends Fragment implements FileDir {

    /**
     * 云盘页面
     */

    private HttpUtil methods = new HttpUtil();

    private RecyclerView fileRecycler;
    private String uuid;
    private String cookie;
    public DiskActivity activity;
    public ArrayList<HashMap<String,String>> fileData;
    public Handler handler_Refresh;

    private OnFileBackPress onFileBackPress;
    private HashMap<String,FilePageAdapter> filePageMap;
    private LinkedList<String> stack;
    private LinkedList<String> currentDirStack;
    public Context lContext;

    private SmartRefreshLayout refreshLayout;
    FloatingActionButton btnFilePlus;

    FrameLayout plusFrameTab;
    TabLayout plusTab;


    SQLiteDatabase database;


    FilePageFragment(String uuid, String Cookie,DiskActivity appCompatActivity,Context context){
        this.uuid=uuid;
        cookie = Cookie;
        activity = appCompatActivity;
        filePageMap = new HashMap<>();
        stack = new LinkedList<>();
        currentDirStack = new LinkedList<>();
        lContext = context;
    }


    public void dialog(String d_uuid,String filename){
        CustomDialog customDialog = new CustomDialog(lContext);
        customDialog.setOnDeleteListener(view -> {
            deleteFile(d_uuid);
            customDialog.cancel();
        });
        customDialog.setOnDownloadListener(view -> {
            if(filename==null||filename.equals("")){
                Snackbar.make(fileRecycler,"文件夹无法下载",Snackbar.LENGTH_SHORT).show();
            }else {
                downloadFile(d_uuid,filename);
            }
            customDialog.cancel();
        });
        customDialog.setOnRenameListener(view -> {
            customDialog.renameAFile(cookie,d_uuid,filename,new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    refreshLayout.autoRefresh();
                        Snackbar.make(refreshLayout,"重命名完成",Snackbar.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Snackbar.make(refreshLayout,"重命名失败",Snackbar.LENGTH_SHORT).show();
                }
            });
            customDialog.cancel();
        });
        customDialog.show();
    }

    public void downloadFile(String p_uuid,String filename){
        if(filename==null){Toast.makeText(lContext,"连接失败",Toast.LENGTH_SHORT).show();return;}

        Call<ResponseBody> call = methods.getMethods().downloadFile(cookie,p_uuid,filename);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                assert response.body() != null;
                new Thread(){
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void run() {
                        super.run();

                        Snackbar.make(refreshLayout,filename+"开始下载",Snackbar.LENGTH_SHORT).show();

                        DownloadNotice notice = new DownloadNotice(lContext, activity.createNotificationChannel("808053","kuaDisk",
                                NotificationManager.IMPORTANCE_HIGH),filename);
                        notice.show(true);


                        InputStream inputStream = response.body().byteStream();
                        long length = response.body().contentLength();
                        int process = 0;
                        byte[] bytes = new byte[4096];
//                File file = new File(Environment.getExternalStorageState()+"/kuaDiskDownload");
                        FileUtils utils = new FileUtils();
                        if(!utils.isFileExist("kuaDiskDownload")){
                            utils.createSDDir("kuaDiskDownload");
                        }

                        utils.changePath("kuaDiskDownload/");

                        if(currentDirStack.size()>1){
                            for(int i=1;i<currentDirStack.size();i++){
                                String dir = currentDirStack.get(i)+"/";
                                utils.createSDDir(dir);
                                utils.changePath(dir);
                            }
                        }

                        try {
                            File file = utils.createSDFile(filename);
                            FileOutputStream outputStream = new FileOutputStream(file);

                            while (true){
                                int i = inputStream.read(bytes);
                                if(i==-1)break;
                                outputStream.write(bytes,0,i);
                                process+=i;

                            }

                            outputStream.flush();
                            outputStream.close();
                            notice.finish();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        insertData(filename,utils.getSDPATH()+filename,true);

                        Snackbar.make(refreshLayout,filename+"下载完成",Snackbar.LENGTH_SHORT).show();

                    }
                }.start();

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }


    public void deleteFile(String d_uuid){

        Call<ResponseBody> call = methods.getMethods().deleteFile(cookie,d_uuid);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                activity.runOnUiThread(() -> {
                    Toast.makeText(getContext(),"删除成功",Toast.LENGTH_SHORT).show();
                    refreshLayout.autoRefresh();
                });
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    public void newDirectory(String p_name){

        Call<ResponseBody> call = methods.getMethods().newDirectory(cookie,uuid,stack.getLast(),p_name);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                activity.runOnUiThread(() -> {
                    Toast.makeText(lContext,"新建成功",Toast.LENGTH_SHORT).show();
                    refreshLayout.autoRefresh();
                });
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
     }


    @SuppressLint("HandlerLeak")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.file_page,container,false);
        fileRecycler = view.findViewById(R.id.fileRecycler);
        fileRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        refreshLayout = view.findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                if(plusFrameTab.getVisibility()==View.VISIBLE)plusFrameTab.setVisibility(View.GONE);
                getFilePage(stack.getLast());
            }
        });
        getFilePage("root");

        plusFrameTab = view.findViewById(R.id.plusFrameTab);
        plusTab = view.findViewById(R.id.plusTab);

        stack.add("root");
        currentDirStack.add("根目录");

        handler_Refresh = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                String[] ss = ((String) msg.obj).split("!");
                String p_uuid = ss[0];
                String filename = ss.length>1?ss[1]:null;
                switch (msg.what){
                    case 1:filePageMap.put(p_uuid,new FilePageAdapter(getContext(),fileData,handler_Refresh));break;
                    case 2:{stack.add(p_uuid);currentDirStack.add(filename);setFileDir(filename,false);break;}
                    case 3:dialog(p_uuid,filename);break;
                }
                if(filePageMap.get(p_uuid)!=null&&msg.what!=3){
                    fileRecycler.setAdapter(filePageMap.get(p_uuid));
                }else if(msg.what!=3&&filePageMap.get(p_uuid)==null){
                    getFilePage(p_uuid);
                }


            }
        };
        btnFilePlus = view.findViewById(R.id.btnFilePlus);
        btnFilePlus.setOnClickListener(view1 -> {
            if(plusFrameTab.getVisibility()==View.VISIBLE)
                plusFrameTab.setVisibility(View.GONE);
            else
                plusFrameTab.setVisibility(View.VISIBLE);
        });

        plusTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tabSelected(tab);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                tabSelected(tab);
            }
        });

        return view;
    }

    public void tabSelected(TabLayout.Tab tab){
        if(tab.getPosition()==1){
            plusFrameTab.setVisibility(View.GONE);
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(intent,1);
        }else {
            NewDirDialog dialog = new NewDirDialog(lContext);
            dialog.setOnConfirmListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String name = dialog.getDirname();
                    if(name.equals("")){
                        Toast.makeText(lContext,"不能为空",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(name.contains(">")||name.contains("<")||name.contains("/")||name.contains("\\"))
                        Toast.makeText(lContext,"名称不合法",Toast.LENGTH_SHORT).show();
                    else {
                        newDirectory(name);
                    }
                    dialog.cancel();
                }
            });
            dialog.show();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                assert data != null;
                Uri uri = data.getData();
                if (uri != null) {
                    String path = getPath(getContext(), uri); //文件真实路径在这
                    Log.println(Log.DEBUG,"真实路径",path);
                    if (path != null) {
                        File file = new File(path);
                        if (file.exists()) {
//                            String upLoadFilePath = file.toString();
//                            String upLoadFileName = file.getName();
                            uploadFile(file,stack.getLast());
                        }
                    }
                }
            }
        }
    }

    public String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = true;

        // DocumentProvider
        if (DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
//                Log.i(TAG,"isExternalStorageDocument***"+uri.toString());
//                Log.i(TAG,"docId***"+docId);
//                以下是打印示例：
//                isExternalStorageDocument***content://com.android.externalstorage.documents/document/primary%3ATset%2FROC2018421103253.wav
//                docId***primary:Test/ROC2018421103253.wav
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
//                Log.i(TAG,"isDownloadsDocument***"+uri.toString());
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.parseLong(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
//                Log.i(TAG,"isMediaDocument***"+uri.toString());
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
//            Log.i(TAG,"content***"+uri.toString());
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
//            Log.i(TAG,"file***"+uri.toString());
            return uri.getPath();
        }
        return null;
    }


    public String getDataColumn(Context context, Uri uri, String selection,
                                String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    public boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.onFileBackPress = (OnFileBackPress) getActivity();
    }

    private void getFilePage(String p_uuid){
        Retrofit retrofit = new Retrofit.Builder().baseUrl(HttpMethods.remoteHost).build();
        Call<ResponseBody> call = retrofit.create(HttpMethods.class).getFilePage(cookie,uuid,p_uuid);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    refreshLayout.finishRefresh(true);
                    if(response.body()==null){
                        Toast.makeText(lContext,"登陆异常",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(lContext, LoginActivity.class));
                        activity.finish();
                    }else
                        fileData = JsonBean.get_file_page(response.body().string());
                    if(fileData!=null)
                        handler_Refresh.sendMessage(handler_Refresh.obtainMessage(1,p_uuid));
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        onFileBackPress.onBackPressHistory(this);
    }

    public boolean onBackPressed(){
        if(plusFrameTab.getVisibility()==View.VISIBLE){plusFrameTab.setVisibility(View.GONE);return true;}
        if(stack.size()==1){
            return false;
        }
        stack.removeLast();
        currentDirStack.removeLast();
        setFileDir(currentDirStack.getLast(),true);
        handler_Refresh.sendMessage(handler_Refresh.obtainMessage(4,stack.getLast()));
        return true;
    }



    @RequiresApi(api = Build.VERSION_CODES.N)
    public void uploadFile(File file, String p_uuid) {


        // 创建 RequestBody，用于封装构建RequestBody
         RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);


        // MultipartBody.Part  这里的partName是用file
        String filename = file.getName();
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", filename, requestFile);

        RequestBody uuid = RequestBody.create(MediaType.parse("multipart/form-data"),this.uuid);
        // 添加描述

        RequestBody puuid = RequestBody.create(MediaType.parse("multipart/form-data"), p_uuid);


        Call<ResponseBody> call = methods.getMethods().uploadFile(cookie,uuid,puuid,body);
        // 执行请求
        Snackbar.make(refreshLayout,filename+"开始上传",Snackbar.LENGTH_SHORT).show();


        DownloadNotice notice = new DownloadNotice(lContext, activity.createNotificationChannel("808053","kuaDisk",
                NotificationManager.IMPORTANCE_HIGH),filename);
        notice.show(false);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                activity.runOnUiThread(() -> {

                    Snackbar.make(refreshLayout,filename+"上传成功",Snackbar.LENGTH_SHORT).show();
                    refreshLayout.autoRefresh();

                    notice.finish();
                    insertData(filename,file.getPath(),false);
                });
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                activity.runOnUiThread(() -> Toast.makeText(getContext(),"上传失败",Toast.LENGTH_SHORT).show());
            }


        });

    }


    public SQLiteDatabase sqlGetInstance(){
        DBHelper helper = new DBHelper(lContext);
        return helper.getWritableDatabase();
    }


    public void sqlTest(){
        SQLiteDatabase ldatabase = sqlGetInstance();

        Cursor cursor = ldatabase.rawQuery("select * from downloadHistory limit 1",null);
        cursor.moveToNext();
        Log.i("",cursor.getString(0));

        cursor.close();
        ldatabase.close();
    }

    public void insertData(String name, String path,boolean isDownload){
        database = sqlGetInstance();

        Date date = new Date();
        date.getTime();
        database.execSQL("create table if not exists downloadHistory('[name] varchar(100)','[path] varchar(1000)','[date] TimeStamp')");
        database.execSQL("create table if not exists uploadHistory('[name] varchar(100)','[path] varchar(1000)','[date] TimeStamp')");
        if(isDownload){
            database.execSQL("insert into downloadHistory values('"+name+"','"+path+"',datetime('now'))");
        }
        else {
            database.execSQL("insert into uploadHistory values('"+name+"','"+path+"',datetime('now'))");
        }

        database.close();

    }

    @Override
    public void setFileDir(String dir,boolean isBack) {
        activity.setCurrentDir(dir,isBack);
    }


}
