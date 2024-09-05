package com.example.inertia.api

import com.example.inertia.DTO.GetLockersResponseDTO
import com.example.inertia.DTO.RentLockerDTO
import com.example.inertia.DTO.RentLockerRequestDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST


interface RetrofitService {

    @POST("locker/rent")
    fun rentLocker(@Body data: RentLockerRequestDTO): Call<RentLockerDTO>

    @GET("locker")
    fun getLockers(): Call<List<GetLockersResponseDTO>>
}