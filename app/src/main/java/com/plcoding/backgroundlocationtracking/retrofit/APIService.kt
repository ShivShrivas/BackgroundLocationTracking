package com.plcoding.backgroundlocationtracking.retrofit

import com.google.gson.JsonObject
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface APIService {
    @POST("PDL.Mobile.API/api/LiveTrack/CreateLiveTrack")
    fun trackUser(@Body jsonObject: JsonObject): Call<ResponseBody>
}