package com.example.week3_assignment.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.week3_assignment.data.Device

/** State hoisting giriş noktası: ViewModel'e sadece burada dokunulur. */
@Composable
fun HomeRoute(modifier: Modifier = Modifier, viewModel: HomeViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val query by viewModel.searchQuery.collectAsStateWithLifecycle()
    HomeScreen(
        state = state,
        query = query,
        onQueryChange = viewModel::onQueryChange,
        onRoomSelected = viewModel::onRoomSelected,
        onToggleDevice = viewModel::onToggleDevice,
        modifier = modifier
    )
}

/** Tamamen stateless: sadece gelen veriyi çizer, olayları yukarı iletir. */
@Composable
fun HomeScreen(
    state: HomeUiState,
    query: String,
    onQueryChange: (String) -> Unit,
    onRoomSelected: (String?) -> Unit,
    onToggleDevice: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(horizontal = 16.dp)) {
        EnergyCard(totalPowerText = state.totalPowerText, activeCount = state.activeDeviceCount)

        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            label = { Text("Cihaz ara") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            item {
                FilterChip(
                    selected = state.selectedRoom == null,
                    onClick = { onRoomSelected(null) },
                    label = { Text("Tümü") }
                )
            }
            items(state.rooms, key = { it }) { room ->
                FilterChip(
                    selected = state.selectedRoom == room,
                    onClick = { onRoomSelected(room) },
                    label = { Text(room) }
                )
            }
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(state.devices, key = { it.id }) { device ->
                DeviceItem(device = device, onToggle = { onToggleDevice(device.id) })
            }
        }
    }
}

@Composable
private fun EnergyCard(totalPowerText: String, activeCount: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Anlık Toplam Tüketim", style = MaterialTheme.typography.labelLarge)
            Text(totalPowerText, style = MaterialTheme.typography.headlineLarge)
            Text("$activeCount aktif cihaz", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun DeviceItem(device: Device, onToggle: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(device.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    "${device.room} • ${device.category} • ${device.powerWatt} W",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    if (device.isOnline) "Online" else "Offline",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (device.isOnline) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.error
                )
            }
            Button(onClick = onToggle, enabled = device.isOnline) {
                Text(if (device.isActive) "Kapat" else "Aç")
            }
        }
    }
}
