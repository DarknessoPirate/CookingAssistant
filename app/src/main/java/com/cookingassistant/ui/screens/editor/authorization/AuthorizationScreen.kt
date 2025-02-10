package com.cookingassistant.ui.screens.editor.authorization

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@Composable
fun AuthorizationScreen(navController: NavController, viewModel: AuthorizationScreenViewModel = viewModel()) {
    var oldPassword by remember {mutableStateOf("")}
    var newPassword by remember {mutableStateOf("")}
    var confirmPassword by remember {mutableStateOf("")}
    var showDialog by remember { mutableStateOf(false) }

    val success by viewModel::success
    val operationFinished by viewModel::operationFinished

    Box(modifier = Modifier.fillMaxSize().background(color = MaterialTheme.colorScheme.background)){
        ConstraintLayout(modifier = Modifier.fillMaxSize().background(color = MaterialTheme.colorScheme.background)) {
            val (exitButton, centerLayout) = createRefs()

            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .constrainAs(exitButton) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                    }
                    .offset(20.dp,40.dp)
                    .clip(
                        CircleShape
                    )
                    .background(color = Color(0xFF3700B3)),
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Profile",
                    tint = Color.White
                )
            }

            Column (modifier = Modifier.fillMaxWidth(0.7f).constrainAs(centerLayout)
            {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            })
            {
                OutlinedTextField(
                    value = oldPassword,
                    onValueChange = { oldPassword = it },
                    label = { Text("Old Password!") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("New Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("New Password Confirm") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        viewModel.changePassword(oldPassword, newPassword, confirmPassword) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Accept")
                }
            }
        }

        if (operationFinished) {
            showDialog = true
            viewModel.resetState()
        }

        if (showDialog) {
            Dialog(onDismissRequest = { showDialog = false
                if (success) {
                    navController.popBackStack()
                }
            }
            ) {
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(if (success) "Password changed successfully!" else "Password change failed!")
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                showDialog = false
                                if (success) {
                                    navController.navigate("profile")
                                }
                            },
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Text(text = "OK")
                        }
                    }
                }
            }
        }
    }
}

