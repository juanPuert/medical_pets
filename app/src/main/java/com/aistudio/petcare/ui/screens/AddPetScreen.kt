package com.aistudio.petcare.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun AddPetScreen(
    onAddClick: (String, String, String, Long, Float) -> Unit,
    onBackClick: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var species by remember { mutableStateOf("") }
    var breed by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Pet") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth().testTag("pet_name_input")
            )
            OutlinedTextField(
                value = species,
                onValueChange = { species = it },
                label = { Text("Species") },
                modifier = Modifier.fillMaxWidth().testTag("pet_species_input")
            )
            OutlinedTextField(
                value = breed,
                onValueChange = { breed = it },
                label = { Text("Breed") },
                modifier = Modifier.fillMaxWidth().testTag("pet_breed_input")
            )
            OutlinedTextField(
                value = weight,
                onValueChange = { weight = it },
                label = { Text("Weight (kg)") },
                modifier = Modifier.fillMaxWidth().testTag("pet_weight_input")
            )
            Button(
                onClick = {
                    onAddClick(name, species, breed, System.currentTimeMillis(), weight.toFloatOrNull() ?: 0f)
                },
                modifier = Modifier.fillMaxWidth().testTag("save_pet_button"),
                enabled = name.isNotBlank()
            ) {
                Text("Save Pet")
            }
        }
    }
}
