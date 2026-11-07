package com.example.week3_assignment.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class DeviceRepository {

    private val _devices = MutableStateFlow(initialDevices())
    val devices: StateFlow<List<Device>> = _devices.asStateFlow()

    /** Thread-safe: atomik olarak ilgili cihazın aktiflik durumunu tersine çevirir. Offline cihaz değişmez. */
    fun toggleDevice(id: Int) {
        _devices.update { list ->
            list.map { if (it.id == id && it.isOnline) it.copy(isActive = !it.isActive) else it }
        }
    }

    private fun initialDevices() = listOf(
        Device(1, "Köşe Lambası", DeviceCategory.LIGHT, "Salon", true, true, 15.0),
        Device(2, "Tavan Avizesi", DeviceCategory.LIGHT, "Salon", true, false, 60.0),
        Device(3, "Bahçe Kamerası", DeviceCategory.CAMERA, "Bahçe", true, true, 8.5),
        Device(4, "Giriş Kamerası", DeviceCategory.CAMERA, "Antre", true, true, 8.5),
        Device(5, "Merkezi Termostat", DeviceCategory.THERMOSTAT, "Salon", true, true, 12.0),
        Device(6, "Yatak Odası Kliması", DeviceCategory.THERMOSTAT, "Yatak Odası", true, false, 1200.0),
        Device(7, "Mutfak Spotları", DeviceCategory.LIGHT, "Mutfak", true, false, 45.0),
        Device(8, "Roborock S7", DeviceCategory.VACUUM, "Koridor", true, true, 50.0),
        Device(9, "Balkon Aydınlatması", DeviceCategory.LIGHT, "Balkon", false, false, 25.0),
        Device(10, "Çalışma Odası Lambası", DeviceCategory.LIGHT, "Ofis", true, true, 12.0)
    )
}
