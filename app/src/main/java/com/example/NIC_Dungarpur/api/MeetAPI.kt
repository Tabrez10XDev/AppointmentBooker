package com.example.NIC_Dungarpur.api

import com.example.NIC_Dungarpur.data.MeetResponse
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST


interface MeetAPI {
    @POST("/meet")
    suspend fun getMeetLink(@Body body: JsonObject)
            : Response<MeetResponse>


}