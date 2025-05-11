package com.example.eventcountdown.presentation.screens.addevent

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.wear.compose.material.ContentAlpha
import coil.compose.rememberAsyncImagePainter
import com.example.eventcountdown.data.local.Event
import com.example.eventcountdown.presentation.EventViewModel
import java.util.*
import android.net.Uri
import androidx.compose.ui.res.stringResource
import com.example.eventcountdown.R
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEventScreen(navController: NavController, viewModel: EventViewModel) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf<Date?>(null) }
    val context = LocalContext.current
    val calendar = remember { Calendar.getInstance() }
    var selectedColor by remember { mutableStateOf(Color.Blue) }
    var backgroundImageUri by remember { mutableStateOf<String?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val savedUri = saveImageToInternalStorage(context, uri)
            backgroundImageUri = savedUri
        }
    }

    val isFormValid = title.isNotBlank() && selectedDate != null
    val toastText = stringResource(id = R.string.add_toast)

    val datePicker = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            TimePickerDialog(
                context,
                { _, hour, minute ->
                    calendar.set(year, month, dayOfMonth, hour, minute, 0)
                    if (calendar.timeInMillis >= System.currentTimeMillis()) {
                        selectedDate = calendar.time
                    } else {
                        Toast.makeText(
                            context,
                            "$toastText",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    ).apply {
        datePicker.minDate = System.currentTimeMillis() - 1000
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.add_new_event), color = MaterialTheme.colorScheme.onPrimary)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        floatingActionButton = {
            Box(
                modifier = Modifier
                    .shadow(8.dp, shape = CircleShape)
                    .clip(CircleShape)
            ) {
                CompositionLocalProvider(LocalContentAlpha provides if (isFormValid) 1f else ContentAlpha.disabled) {
                    FloatingActionButton(
                        onClick = {
                            if (isFormValid) {
                                selectedDate?.let { date ->
                                    viewModel.addEvent(
                                        Event(
                                            title = title,
                                            description = description,
                                            date = date,
                                            color = selectedColor.toArgb(),
                                            backgroundImageUri = backgroundImageUri
                                        )
                                    )
                                    navController.popBackStack()
                                }
                            }
                        },
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ) {
                        Icon(Icons.Default.Check, contentDescription = "Save")
                    }
                }
            }
        }

    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = stringResource(R.string.select_event_color), style = MaterialTheme.typography.labelLarge)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf(
                    Color(0xFF474E93),
                    Color(0xFF16C47F),
                    Color(0xFFFFD65A),
                    Color(0xFFFF9D23),
                    Color(0xFFF93827),
                    Color(0xFF69247C),
                    Color(0xFFDA498D),
                ).forEach { color ->
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(color, CircleShape)
                            .border(
                                width = if (selectedColor == color) 3.dp else 0.dp,
                                color = MaterialTheme.colorScheme.onSurface,
                                shape = CircleShape
                            )
                            .clickable { selectedColor = color }
                    )
                }
            }

            Button(
                onClick = { launcher.launch("image/*") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(R.string.select_background_image))
            }

            backgroundImageUri?.let {
                Image(
                    painter = rememberAsyncImagePainter(it),
                    contentDescription = "Selected background",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text(text = stringResource(R.string.title)) },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                )
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text(text = stringResource(R.string.desc)) },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3,
                shape = MaterialTheme.shapes.large,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                )
            )

            FilledTonalButton(
                onClick = {
                    datePicker.show()
                },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            ) {
                Icon(Icons.Default.DateRange, contentDescription = "Pick Date")
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = stringResource(R.string.edit_date_time))
            }

            Text(
                text = selectedDate?.let {
                    "${stringResource(R.string.selected)}: ${it}"
                } ?: stringResource(R.string.non_selected),
                color = if (selectedDate?.before(Date()) == true) Color.Red else Color.Unspecified
            )
        }
    }
}

fun saveImageToInternalStorage(context: Context, uri: Uri): String {
    val contentResolver = context.contentResolver
    val directory = File(context.filesDir, "event_images")
    if (!directory.exists()) {
        directory.mkdirs()
    }

    val inputStream: InputStream? = contentResolver.openInputStream(uri)
    val fileName = "event_${System.currentTimeMillis()}.jpg"
    val file = File(directory, fileName)

    inputStream?.use { input ->
        FileOutputStream(file).use { output ->
            input.copyTo(output)
        }
    }

    return file.absolutePath
}