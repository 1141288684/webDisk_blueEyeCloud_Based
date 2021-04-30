package com.koader.mywebdisk.httpUtils;

import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils {
    private String SDPATH;
    public String getSDPATH(){
        return SDPATH;
    }
    //构造函数，得到SD卡的目录，这行函数得到的目录名其实是叫"/SDCARD"
    public FileUtils() {
        SDPATH = Environment.getExternalStorageDirectory() +"/";
    }
    //在SD卡上创建文件
    public File createSDFile(String fileName) throws IOException {
        File file = new File(SDPATH + fileName);
        boolean is = file.createNewFile();
        return file;
    }
    //在SD卡上创建目录
    public File createSDDir(String dirName){
        File dir = new File(SDPATH + dirName);
        boolean is = dir.mkdir();
        return dir;
    }
    //判断SD卡上的文件夹是否存在
    public boolean isFileExist(String fileName){
        File file = new File(SDPATH + fileName);
        return file.exists();
    }

    public void changePath(String name){
        SDPATH = SDPATH+name;
    }



    //将一个InputStream里面的数据写入到SD卡中
    //将input写到path这个目录中的fileName文件上
    public File write2SDFromInput(String path, String fileName, InputStream input){
        File file = null;
        OutputStream output = null;
        try{
            createSDDir(path);
            file = createSDFile(path + fileName);
            //FileInputStream是读取数据，FileOutputStream是写入数据，写入到file这个文件上去
            output = new FileOutputStream(file);
            byte[] buffer = new byte[4 * 1024];
            while((input.read(buffer)) != -1){
                output.write(buffer);
            }
            output.flush();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally{
            try{
                output.close();
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
        return file;
    }
}
