package com.dummychallenge.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.dummychallenge.ui.components.AppScaffold
import com.dummychallenge.ui.components.ErrorState
import com.dummychallenge.ui.components.ProfileImageGenerator
import com.dummychallenge.ui.components.ScreenType
import com.dummychallenge.viewmodel.CreateUserScreenViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateUserScreen(
    navController: NavHostController,
    viewModel: CreateUserScreenViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Local states for the form - precargados con datos de prueba
    var title by remember { mutableStateOf("Mr") }
    var firstName by remember { mutableStateOf("John") }
    var lastName by remember { mutableStateOf("Doe") }
    var gender by remember { mutableStateOf("Male") }
    var email by remember { mutableStateOf("john.doe@example.com") }
    var dateOfBirth by remember { mutableStateOf("1990-01-15") }
    var phone by remember { mutableStateOf("1234567890") }
    var picture by remember { mutableStateOf("") }

    // Dropdown states
    var titleExpanded by remember { mutableStateOf(false) }
    var genderExpanded by remember { mutableStateOf(false) }

    // Date picker state
    var showDatePicker by remember { mutableStateOf(false) }

    // Navigate back after successfully creating the user
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            navController.popBackStack()
        }
    }

    AppScaffold(
        screenType = ScreenType.CREATE_USER,
        title = "Create User",
        onBackClick = {
            navController.popBackStack()
        },
        onSaveClick = {
            viewModel.createUser(
                title = title,
                firstName = firstName,
                lastName = lastName,
                gender = gender,
                email = email,
                dateOfBirth = dateOfBirth,
                phone = phone,
                picture = picture
            )
        },
        onDiscardClick = {
            navController.popBackStack()
        }
    ) {
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.error != null -> {
                ErrorState(
                    message = uiState.error ?: "Unknown error",
                    onRetry = {
                        viewModel.clearError()
                    }
                )
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "User Information",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )

                    // Profile Image Generator
                    ProfileImageGenerator(
                        firstName = firstName,
                        lastName = lastName,
                        onImageUrlChange = { picture = it }
                    )

                    // Personal Information Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "Personal Information",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )

                            // Title Dropdown
                            ExposedDropdownMenuBox(
                                expanded = titleExpanded,
                                onExpandedChange = { titleExpanded = !titleExpanded }
                            ) {
                                OutlinedTextField(
                                    value = title,
                                    onValueChange = { title = it },
                                    readOnly = true,
                                    label = { Text("Title *") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = titleExpanded) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor()
                                )
                                ExposedDropdownMenu(
                                    expanded = titleExpanded,
                                    onDismissRequest = { titleExpanded = false }
                                ) {
                                    listOf("Mr", "Ms", "Mrs", "Miss").forEach { option ->
                                        DropdownMenuItem(
                                            text = { Text(option) },
                                            onClick = {
                                                title = option
                                                titleExpanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            // First Name and Last Name Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = firstName,
                                    onValueChange = { firstName = it },
                                    label = { Text("First Name *") },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = lastName,
                                    onValueChange = { lastName = it },
                                    label = { Text("Last Name *") },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                            }

                            // Gender Dropdown
                            ExposedDropdownMenuBox(
                                expanded = genderExpanded,
                                onExpandedChange = { genderExpanded = !genderExpanded }
                            ) {
                                OutlinedTextField(
                                    value = gender,
                                    onValueChange = { gender = it },
                                    readOnly = true,
                                    label = { Text("Gender *") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = genderExpanded) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor()
                                )
                                ExposedDropdownMenu(
                                    expanded = genderExpanded,
                                    onDismissRequest = { genderExpanded = false }
                                ) {
                                    listOf("Male", "Female").forEach { option ->
                                        DropdownMenuItem(
                                            text = { Text(option) },
                                            onClick = {
                                                gender = option
                                                genderExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Contact Information Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "Contact Information",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )

                            OutlinedTextField(
                                value = email,
                                onValueChange = { email = it },
                                label = { Text("Email *") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            // Date of Birth
                            OutlinedTextField(
                                value = dateOfBirth,
                                onValueChange = { },
                                label = { Text("Date of Birth *") },
                                placeholder = { Text("Select date") },
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.DateRange,
                                        contentDescription = "Select Date",
                                        modifier = Modifier.clickable { showDatePicker = true }
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                readOnly = true,
                                singleLine = true
                            )

                            // Phone
                            OutlinedTextField(
                                value = phone,
                                onValueChange = { newValue ->
                                    // Solo permitir números
                                    if (newValue.all { it.isDigit() }) {
                                        phone = newValue
                                    }
                                },
                                label = { Text("Phone *") },
                                placeholder = { Text("1234567890") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                        }
                    }

                    if (uiState.isSuccess) {
                        Text(
                            text = "User created successfully",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        // Date Picker Dialog
        if (showDatePicker) {
            val datePickerState = rememberDatePickerState()
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val calendar = Calendar.getInstance()
                            calendar.timeInMillis = millis
                            val year = calendar.get(Calendar.YEAR)
                            val month = calendar.get(Calendar.MONTH) + 1
                            val day = calendar.get(Calendar.DAY_OF_MONTH)
                            dateOfBirth = String.format("%04d-%02d-%02d", year, month, day)
                        }
                        showDatePicker = false
                    }) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("Cancel")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
    }
}