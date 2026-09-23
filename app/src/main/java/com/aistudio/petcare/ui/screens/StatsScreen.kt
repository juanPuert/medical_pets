package com.aistudio.petcare.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.health.connect.client.HealthConnectClient

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    val healthConnectAvailable = HealthConnectClient.getSdkStatus(context) == HealthConnectClient.SDK_AVAILABLE

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Health Statistics") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Text("Weight Trends (Last 5 months)", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth().height(150.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom
                ) {
                    val weights = listOf(10, 12, 11, 13, 12)
                    weights.forEach { weight ->
                        Box(
                            modifier = Modifier
                                .width(30.dp)
                                .fillMaxHeight(weight / 15f)
                                .background(MaterialTheme.colorScheme.primary, MaterialTheme.shapes.small)
                        )
                    }
                }
            }

            item {
                Text("Activity Levels", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth().height(150.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom
                ) {
                    val activity = listOf(5, 8, 3, 10, 6, 7, 4)
                    activity.forEach { level ->
                        Box(
                            modifier = Modifier
                                .width(20.dp)
                                .fillMaxHeight(level / 12f)
                                .background(MaterialTheme.colorScheme.secondary, MaterialTheme.shapes.small)
                        )
                    }
                }
            }

            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Google Health Connect", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        if (healthConnectAvailable) {
                            Text("Service available. Integration active.", color = Color(0xFF4CAF50))
                        } else {
                            Text("Service not available on this device.", color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }
}
