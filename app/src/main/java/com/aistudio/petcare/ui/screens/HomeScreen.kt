package com.aistudio.petcare.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
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
import coil.compose.rememberAsyncImagePainter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onPetClick: (Int) -> Unit,
    onAddPetClick: () -> Unit,
    onForumClick: () -> Unit,
    onMapClick: () -> Unit,
    onStatsClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    val pets by viewModel.pets.collectAsState()
    val context = LocalContext.current
    var currentUser by remember { mutableStateOf(FirebaseAuth.getInstance().currentUser) }
    val profile by viewModel.userProfile.collectAsState()

    DisposableEffect(Unit) {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            currentUser = auth.currentUser
        }
        FirebaseAuth.getInstance().addAuthStateListener(listener)
        onDispose {
            FirebaseAuth.getInstance().removeAuthStateListener(listener)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("PetCare") },
                actions = {
                    IconButton(onClick = onProfileClick) {
                        Icon(Icons.Default.Person, contentDescription = "Profile")
                    }
                    if (currentUser != null) {
                        IconButton(onClick = {
                            FirebaseAuth.getInstance().signOut()
                        }) {
                            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Sign Out")
                        }
                    } else {
                        IconButton(onClick = onForumClick) {
                            Icon(Icons.Default.Login, contentDescription = "Login")
                        }
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

            if (currentUser != null && profile == null) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
                        onClick = onProfileClick
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Complete your profile", style = MaterialTheme.typography.titleMedium)
                            Text("Add your phone, address, and city to enable full features.", style = MaterialTheme.typography.bodySmall)
                            TextButton(onClick = onProfileClick, modifier = Modifier.align(Alignment.End)) {
                                Text("Register Now")
                            }
                        }
                    }
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
                    colors = CardDefaults.cardColors(
                        containerColor = if (currentUser != null) 
                            MaterialTheme.colorScheme.surfaceVariant 
                        else 
                            MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = if (currentUser != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (currentUser != null) 
                                "End-to-End Encrypted Sync Active" 
                            else 
                                "Local Only - Sign In to Sync & Secure",
                            style = MaterialTheme.typography.labelMedium,
                            color = if (currentUser != null) 
                                MaterialTheme.colorScheme.primary 
                            else 
                                MaterialTheme.colorScheme.error
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
            Surface(
                modifier = Modifier.size(64.dp),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    if (pet.photoUrl != null) {
                        Image(
                            painter = rememberAsyncImagePainter(pet.photoUrl),
                            contentDescription = "Pet Photo",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Text(pet.name.take(1), style = MaterialTheme.typography.headlineLarge)
                    }
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
