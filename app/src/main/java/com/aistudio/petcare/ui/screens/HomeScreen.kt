package com.aistudio.petcare.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.aistudio.petcare.R
import com.aistudio.petcare.data.local.Pet
import com.aistudio.petcare.ui.viewmodel.MainViewModel
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onPetClick: (Int) -> Unit,
    onAddPetClick: () -> Unit,
    onForumClick: () -> Unit,
    onMapClick: () -> Unit,
    onStatsClick: () -> Unit
) {
    val pets by viewModel.pets.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("PetCare") },
                actions = {
                    IconButton(onClick = {
                        FirebaseAuth.getInstance().signOut()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Sign Out")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddPetClick,
                modifier = Modifier.testTag("add_pet_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Pet")
            }
        },
        bottomBar = {
            BottomAppBar {
                IconButton(onClick = onForumClick, modifier = Modifier.weight(1f)) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Chat, contentDescription = "Forum")
                        Text("Forum", style = MaterialTheme.typography.labelSmall)
                    }
                }
                IconButton(onClick = onMapClick, modifier = Modifier.weight(1f)) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Map, contentDescription = "Clinics")
                        Text("Map", style = MaterialTheme.typography.labelSmall)
                    }
                }
                IconButton(onClick = onStatsClick, modifier = Modifier.weight(1f)) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.BarChart, contentDescription = "Stats")
                        Text("Stats", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    shape = MaterialTheme.shapes.large
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_hero_pets),
                        contentDescription = "Hero",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            item {
                Text(
                    text = "My Pets",
                    style = MaterialTheme.typography.headlineMedium
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "End-to-End Encrypted Sync Active",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            if (pets.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No pets added yet.", color = MaterialTheme.colorScheme.outline)
                    }
                }
            } else {
                items(pets) { pet ->
                    PetCard(pet = pet, onClick = { onPetClick(pet.id) })
                }
            }
        }
    }
}

@Composable
fun PetCard(pet: Pet, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("pet_card_${pet.id}"),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Placeholder for pet icon
            Surface(
                modifier = Modifier.size(64.dp),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(pet.name.take(1), style = MaterialTheme.typography.headlineLarge)
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = pet.name, style = MaterialTheme.typography.titleLarge)
                Text(text = "${pet.species} • ${pet.breed}", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
