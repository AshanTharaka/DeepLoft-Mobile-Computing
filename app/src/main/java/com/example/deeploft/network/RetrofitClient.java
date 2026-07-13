package com.example.deeploft.network;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    // Use http://10.0.2.2:8080/ for Android Emulator
    // Use http://192.168.1.24:8080/ for Physical Device (if your PC IP is correct)
    private static final String BASE_URL = "http://10.0.2.2:8080/";
    private static Retrofit retrofit = null;
    private static ApiService apiService = null;

    public static String getServiceUrl() {
        return BASE_URL;
    }

    public static ApiService getApiService() {
        if (apiService == null) {
            System.out.println("Initializing Retrofit with BASE_URL: " + BASE_URL);
            if (retrofit == null) {
                OkHttpClient okHttpClient = new OkHttpClient.Builder()
                        .connectTimeout(30, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS)
                        .writeTimeout(30, TimeUnit.SECONDS)
                        .build();

                retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .client(okHttpClient)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
            }
            apiService = retrofit.create(ApiService.class);
        }
        return apiService;
    }
}