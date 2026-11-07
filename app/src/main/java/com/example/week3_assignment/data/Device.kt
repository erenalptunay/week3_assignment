package com.example.week3_assignment.data

enum class DeviceCategory { LIGHT, CAMERA, THERMOSTAT, VACUUM }

data class Device(
    val id: Int,
    val name: String,
    val category: DeviceCategory,
    val room: String,
    val isOnline: Boolean,
    val isActive: Boolean,
    val powerWatt: Double
)
