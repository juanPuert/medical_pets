package com.aistudio.petcare.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.platform.LocalContext
import android.content.Intent
import android.net.Uri

data class Clinic(val name: String, val address: String, val lat: Double, val lng: Double)

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    val clinics = listOf(
        Clinic("City Vet Center", "123 Main St", 40.7128, -74.0060),
        Clinic("Happy Paws Hospital", "456 Oak Ave", 40.7200, -74.0100),
        Clinic("Urgency Pet Care 24/7", "789 Pine Rd", 40.7300, -73.9900)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nearby Clinics") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text("Clinics and Emergency Services", style = MaterialTheme.typography.titleLarge)
            }
            items(clinics) { clinic ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = clinic.name, style = MaterialTheme.typography.titleMedium)
                            Text(text = clinic.address, style = MaterialTheme.typography.bodySmall)
                        }
                        Button(onClick = {
                            val gmmIntentUri = Uri.parse("geo:${clinic.lat},${clinic.lng}?q=${Uri.encode(clinic.name)}")
                            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                            mapIntent.setPackage("com.google.android.apps.maps")
                            context.startActivity(mapIntent)
                        }) {
                            Text("Map")
                        }
                    }
                }
            }
        }
    }
}
