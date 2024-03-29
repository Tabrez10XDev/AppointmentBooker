package `in`.nic.raj_dungarpur.api

import `in`.nic.raj_dungarpur.data.MeetResponse
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST


interface MeetAPI {
    @POST("/meet")
    suspend fun getMeetLink(@Body body: JsonObject)
            : Response<MeetResponse>


}