package com.plcoding.backgroundlocationtracking.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Client {

    val retrofit = Retrofit.Builder()
        .baseUrl("base_url")
        .addConverterFactory(GsonConverterFactory.create())
        .build()



    val apiService = retrofit.create(APIService::class.java)
}