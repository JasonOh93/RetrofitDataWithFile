package com.jasonoh.retrofitdatawithfileex;

import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitHelper {

    public static Retrofit getInstance () {
        Retrofit.Builder builder = new Retrofit.Builder();
        builder.baseUrl( "http://jasonoh93.dothome.co.kr" );
        builder.addConverterFactory(ScalarsConverterFactory.create());

        return builder.build();
    }


}
