package com.ncs.nyayvedika.Domain.Api

import com.ncs.nyayvedika.Constants.ApiEndpoints
import okhttp3.Interceptor
import okhttp3.Interceptor.*
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


/*
File : RetrofitClient.kt -> com.ncs.nyayvedika.Domain.Api
Description : Client for Retrofit 

Author : Alok Ranjan (VC uname : apple)
Link : https://github.com/arpitmx
From : Bitpolarity x Noshbae (@Project : NyayVedika Android)

Creation : 12:16 am on 18/09/23

Todo >
Tasks CLEAN CODE : 
Tasks BUG FIXES : 
Tasks FEATURE MUST HAVE : 
Tasks FUTURE ADDITION : 


*/

object RetrofitClient {

    private const val BASE_URL: String = ApiEndpoints.BASE_URL
    private const val AUTH_TOKEN: String = ApiEndpoints.AUTH_TOKEN

    var client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        . addInterceptor(Interceptor { chain ->
        val newRequest: Request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $AUTH_TOKEN")
            .build()
            chain.proceed(newRequest)
            }).build()


    fun getInstance(): Retrofit {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

    }

}