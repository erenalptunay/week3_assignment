package com.example.week3_assignment.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.week3_assignment.data.Device
import com.example.week3_assignment.data.DeviceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Locale

data class HomeUiState(
    val devices: List<Device> = emptyList(),
    val rooms: List<String> = emptyList(),
    val selectedRoom: String? = null,
    val totalPowerText: String = "0.0 W",
    val activeDeviceCount: Int = 0
)

class HomeViewModel(
    private val repository: DeviceRepository = DeviceRepository()
) : ViewModel() {

    private val query = MutableStateFlow("")
    private val selectedRoom = MutableStateFlow<String?>(null)

    // Metin alanı imleç sorunu yaşamasın diye senkron ayrı akış olarak sunulur.
    val searchQuery: StateFlow<String> = query.asStateFlow()

    val uiState: StateFlow<HomeUiState> = combine(
        repository.devices, query, selectedRoom
    ) { all, q, room ->
        // Tüm filtreleme ve hesaplamalar burada, Default dispatcher üzerinde yapılır.
        val filtered = all.filter { device ->
            (room == null || device.room == room) &&
                device.name.contains(q.trim(), ignoreCase = true)
        }
        val running = all.filter { it.isOnline && it.isActive }
        HomeUiState(
            devices = filtered,
            rooms = all.map { it.room }.distinct(),
            selectedRoom = room,
            totalPowerText = String.format(Locale.US, "%.1f W", running.sumOf { it.powerWatt }),
            activeDeviceCount = running.size
        )
    }
        .flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())

    fun onQueryChange(newQuery: String) {
        query.value = newQuery
    }

    fun onRoomSelected(room: String?) {
        selectedRoom.value = room
    }

    fun onToggleDevice(id: Int) {
        viewModelScope.launch(Dispatchers.Default) { repository.toggleDevice(id) }
    }
}
