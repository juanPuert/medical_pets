package com.aistudio.petcare.ui.screens

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Vaccines
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.aistudio.petcare.data.local.Pet
import com.aistudio.petcare.ui.viewmodel.MainViewModel
import java.io.File
import java.io.FileOutputStream

fun exportPetToPdf(pet: Pet, context: android.content.Context) {
    val pdfDocument = PdfDocument()
    val pageInfo = PdfDocument.PageInfo.Builder(300, 600, 1).create()
    val page = pdfDocument.startPage(pageInfo)
    val canvas: Canvas = page.canvas
    val paint = Paint()

    paint.color = Color.BLACK
    paint.textSize = 12f
    canvas.drawText("Pet Health Report", 10f, 25f, paint)
    canvas.drawText("Name: ${pet.name}", 10f, 50f, paint)
    canvas.drawText("Species: ${pet.species}", 10f, 75f, paint)
    canvas.drawText("Breed: ${pet.breed}", 10f, 100f, paint)
    canvas.drawText("Weight: ${pet.weight} kg", 10f, 125f, paint)

    pdfDocument.finishPage(page)

    val filePath = File(context.getExternalFilesDir(null), "${pet.name}_report.pdf")
    try {
        pdfDocument.writeTo(FileOutputStream(filePath))
        Toast.makeText(context, "PDF exported to: ${filePath.absolutePath}", Toast.LENGTH_LONG).show()
    } catch (e: Exception) {
        Toast.makeText(context, "Failed to export PDF", Toast.LENGTH_SHORT).show()
    } finally {
        pdfDocument.close()
    }
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun PetDetailScreen(
    petId: Int,
    viewModel: MainViewModel,
    onBackClick: () -> Unit
) {
    val pets by viewModel.pets.collectAsState()
    val pet = pets.find { it.id == petId }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(pet?.name ?: "Pet Details") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        if (pet != null) {
                            exportPetToPdf(pet, context)
                        }
                    }) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = "Export PDF")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (pet == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                Text("Pet not found")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    PetInfoSection(pet)
                }
                item {
                    SectionHeader("Medical History", Icons.Default.MedicalServices)
                }
                // Placeholder for medical records
                item {
                    Text("No medical records yet.", style = MaterialTheme.typography.bodyMedium)
                }
                item {
                    SectionHeader("Vaccines", Icons.Default.Vaccines)
                }
                // Placeholder for vaccines
                item {
                    Text("No vaccines recorded.", style = MaterialTheme.typography.bodyMedium)
                }
                item {
                    SectionHeader("Feeding Reminders", Icons.Default.Restaurant)
                }
                // Placeholder for reminders
                item {
                    Text("No feeding reminders.", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
fun PetInfoSection(pet: Pet) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Breed: ${pet.breed}", style = MaterialTheme.typography.bodyLarge)
            Text(text = "Species: ${pet.species}", style = MaterialTheme.typography.bodyLarge)
            Text(text = "Weight: ${pet.weight} kg", style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
fun SectionHeader(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = title, style = MaterialTheme.typography.headlineSmall)
    }
}
