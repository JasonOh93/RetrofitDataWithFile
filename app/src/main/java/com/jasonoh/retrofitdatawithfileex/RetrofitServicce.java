package com.jasonoh.retrofitdatawithfileex;

import java.util.Map;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;

public interface RetrofitServicce {

    @Multipart
    @POST("/Retrofit/uploadDataMyTest.php")
    Call<String> postDataWithFile(@PartMap Map<String, String> dataPart, @Part MultipartBody.Part filePart);

}
