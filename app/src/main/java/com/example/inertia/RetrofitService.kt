package com.example.inertia

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST


interface RetrofitService {

    @POST("locker/rent")
    fun rentLocker(@Body data: RentLockerRequestDTO): Call<RentLockerDTO>

//    @GET("{estado}/{cidade}/{endereco}/json/")
//    fun getRCE(@Path(value = "estado") estado: String,
//               @Path(value = "cidade") cidade: String,
//               @Path(value = "endereco") endereco: String) : Call<List<CEP>>
}