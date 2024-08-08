package com.example.inertia

import java.util.Date

data class RentLockerDTO(
    val id: Number,
    val locker_id: String,
    val user_id: Number,
    val rent_start_date: Date
)