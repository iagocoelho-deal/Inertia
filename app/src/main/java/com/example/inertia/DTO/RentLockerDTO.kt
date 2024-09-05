package com.example.inertia.DTO

import java.util.Date

data class RentLockerDTO(
    val id: Number,
    val locker_id: String,
    val user_id: Number,
    val rentStartDate: Date,
    val rentFinishDate: Date
)