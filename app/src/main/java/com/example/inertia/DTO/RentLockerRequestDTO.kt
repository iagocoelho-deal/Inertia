package com.example.inertia.DTO

data class RentLockerRequestDTO(
    val lockerId: String,
    val userId: Int,
    val rentStartDate: String,
    val rentFinishDate: String
)
