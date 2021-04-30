package com.koader.mywebdisk.httpUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Response;

public class JsonBean {

    /**
     *
     * 解析json
     *
     */

    public static HashMap<String,String> getUserdata(String json){
        Type type = new TypeToken<HashMap<String,String>>(){}.getType();
        Gson gson = new Gson();
        return gson.fromJson(json,type);
    }

    public static ArrayList<HashMap<String,String>> get_file_page(String json){
        Type type = new TypeToken<ArrayList<HashMap<String,String>>>(){}.getType();
        Gson gson = new Gson();
        return gson.fromJson(json,type);
    }


}
