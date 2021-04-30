package com.koader.mywebdisk.httpUtils;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Streaming;

public interface HttpMethods {
    String remoteHost = "http://106.52.176.225:6020";//python中转端口
    String apiHost = "http://106.52.176.225:6010"; //云盘api端口

    String root = "root";

    @FormUrlEncoded
    @POST("/getUserCok")
    Call<ResponseBody> getUserCok(@Field("username") String usn, @Field("password") String psw,@Field("uuid") String uuid);

    @GET("/getFilePage")
    Call<ResponseBody> getFilePage(@Query("Cookie") String cookie,
                                   @Query("userUuid") String uuid,@Query("p_uuid") String p_uuid);

    @Multipart
    @POST("/api/matter/upload")
    Call<ResponseBody> uploadFile(@Header("Cookie") String cookie, @Part("userUuid") RequestBody userUuid,
                                  @Part("puuid") RequestBody p_uuid, @Part MultipartBody.Part file);

    @FormUrlEncoded
    @POST("/api/matter/delete")
    Call<ResponseBody> deleteFile(@Header("Cookie") String cookie,@Field("uuid") String uuid);

    @FormUrlEncoded
    @POST("/api/matter/create/directory")
    Call<ResponseBody> newDirectory(@Header("Cookie") String cookie,@Field("userUuid")String uuid,
                                    @Field("puuid")String p_uuid,@Field("name")String name);

    @Streaming
    @GET("/api/alien/download/{uuid}/{filename}")
    Call<ResponseBody> downloadFile(@Header("Cookie") String cookie, @Path("uuid") String uuid,@Path("filename") String filename);

    @FormUrlEncoded
    @POST("/api/user/register")
    Call<ResponseBody> register(@Field("username") String username,@Field("password") String password);

    @FormUrlEncoded
    @POST("/api/matter/rename")
    Call<ResponseBody> renameFile(@Header("Cookie") String cookie,@Field("uuid") String uuid,@Field("name") String name);

    @FormUrlEncoded
    @POST("/api/user/change/password")
    Call<ResponseBody> password(@Header("Cookie")String cookie,@Field("oldPassword") String oldPassword,@Field("newPassword") String newPassword);
}
