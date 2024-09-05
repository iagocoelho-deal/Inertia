package com.example.inertia.DTO

import java.util.UUID

data class GetLockersResponseDTO(
    val id: UUID,
    val address: String,
    val free: Boolean
)
