package com.cookingassistant.ui.screens.registration

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun RegistrationScreen(navController: NavController, registrationViewModel: RegistrationViewModel) {
    val email by registrationViewModel.email.collectAsState()
    val username by registrationViewModel.username.collectAsState()
    val password by registrationViewModel.password.collectAsState()
    val registrationResult by registrationViewModel.registrationResult.collectAsState()
    val isLoading by registrationViewModel.isLoading.collectAsState()
    val isDialogVisible by registrationViewModel.isDialogVisible.collectAsState()
    val isRegistrationSuccessful by registrationViewModel.isRegistrationSuccessful.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        // Email input
        TextField(
            value = email,
            onValueChange = { registrationViewModel.onEmailChanged(it) },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Username input
        TextField(
            value = username,
            onValueChange = { registrationViewModel.onUsernameChanged(it) },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Password input
        TextField(
            value = password,
            onValueChange = { registrationViewModel.onPasswordChanged(it) },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Register Button
        Button(
            onClick = {
                registrationViewModel.registerUser()
                if(isRegistrationSuccessful)
                    navController.navigate("login")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Register")
        }

        // Loading indicator

        if (isLoading) {
            Row (Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center){
                CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
            }
        }

        // Show result (either token or error message) in a dialog
        if (isDialogVisible && registrationResult != null) {
            AlertDialog(
                onDismissRequest = { /* Dismiss the dialog */ },
                title = { Text("Registration result") },
                text = { Text(registrationResult ?: "") },
                confirmButton = {
                    Button(onClick = {
                        registrationViewModel.hideResultDialog()
                        if(isRegistrationSuccessful){
                            navController.navigate("login")
                        }
                    }){
                        Text("OK")
                    }
                }
            )
        }
    }
}