package com.shop.happy.happyshop.network;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by karthikeyan on 22/8/17.
 */

public interface RestServiceFactory {

    String BASE_URL = "http://sephora-mobile-takehome-apple.herokuapp.com/api/v1/";

    <T> T create(Class<T> clazz);

    class Impl implements RestServiceFactory {


        @Override
        public <T> T create(Class<T> clazz){
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

            Retrofit.Builder builder = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create());

            Retrofit retrofit = builder
                    .client(httpClient.build())
                    .build();
            return retrofit.create(clazz);
        }
    }
}
