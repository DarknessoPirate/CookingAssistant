package com.cookingassistant.ui.screens.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.cookingassistant.R
import com.cookingassistant.ui.screens.RecipesList.RecipesListViewModel
import com.cookingassistant.ui.screens.RecipesList.State
import com.cookingassistant.ui.screens.home.LoginViewModel
import com.cookingassistant.util.ImageConverter

@Composable
fun ProfileScreen(navController: NavController, recipeListViewModel: RecipesListViewModel, profileScreenViewModel: ProfileScreenViewModel = viewModel(), loginViewModel: LoginViewModel) {
    var showFirstDialog by remember { mutableStateOf(false) }
    var showSecondDialog by remember { mutableStateOf(false) }
    var showResultDialog by remember { mutableStateOf(false) }
    var passwordInput by remember { mutableStateOf("") }

    val success by profileScreenViewModel::success
    val operationFinished by profileScreenViewModel::operationFinished

    val username by loginViewModel.username.collectAsState()
    val email by loginViewModel.email.collectAsState()

    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize().padding(bottom = 60.dp)
    ) {
        item{profilImage(profileScreenViewModel)}
        item{Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "$username", fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "$email", fontSize = 18.sp)
            Spacer(modifier = Modifier.height(40.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                profilButton(
                    onClick = {
                        navController.navigate("recipeList")
                        recipeListViewModel.loadQuery(state = State.Favourite)
                    },
                    text = "Favorite recipes",
                    modifier = Modifier
                        .height(80.dp)
                        .weight(1f),
                    color = Color(0xFF5F75E1)
                )
                profilButton(
                    onClick = {
                        navController.navigate("recipeList")
                        recipeListViewModel.loadQuery(state = State.Own)
                    },
                    text = "Own recipes",
                    modifier = Modifier
                        .height(80.dp)
                        .weight(1f),
                    color = Color(0xFF5F75E1)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            profilButton(onClick = {
                navController.navigate("authorization")
                },
                text = "Change Password", color = Color(0xFF9E72E1))
            Spacer(modifier = Modifier.height(8.dp))
            profilButton(onClick = {
                showFirstDialog = true
            },
                text = "Delete Account", color = Color(0xFFEC544C))
            Spacer(modifier = Modifier.height(8.dp))
            profilButton(onClick = {
                profileScreenViewModel.resetToken()
                loginViewModel.clearInfoUser()
                navController.popBackStack("login", inclusive = false)
            }, text = "Log out", color = Color(0xFF5F75E1))
            Spacer(modifier = Modifier.height(8.dp))
        }}
    }



    if (showFirstDialog) {
        ConfirmDeleteDialog(
            onDismiss = { showFirstDialog = false },
            onConfirm = {
                showFirstDialog = false
                showSecondDialog = true
            }
        )
    }

    if (showSecondDialog) {
        PasswordVerificationDialog(
            passwordInput = passwordInput,
            onInputChange = { passwordInput = it },
            onDismiss = {
                passwordInput = ""
                showSecondDialog = false
                },
            onConfirm = {
                profileScreenViewModel.deleteAccount(passwordInput)
                passwordInput = ""
            }
        )
    }

    if (operationFinished) {
        showSecondDialog = false
        showResultDialog = true
        profileScreenViewModel.resetState()
    }

    if (showResultDialog) {
        ResultDialog(
            isPasswordCorrect = success,
            onDismiss = {
                showResultDialog = false
                if(success){
                    profileScreenViewModel.resetToken()
                    loginViewModel.clearInfoUser()
                    navController.popBackStack("login", inclusive = false)
                }
            }
        )
    }
}


@Composable
private fun profilButton(
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .height(50.dp),
    color: Color = Color(0xFFFFFFFF),
    text: String = "",
    onClick: () -> Unit = {}
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = color
        ), shape = RoundedCornerShape(20)
    ) {
        Text(text = text, color = Color(0xFFFFFFFF), fontSize = 20.sp, textAlign = TextAlign.Center)
    }
}

@Composable
fun profilImage(profileScreenViewModel: ProfileScreenViewModel) {
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            profileScreenViewModel.userProfilePicture = ImageConverter.uriToBitmap(context,uri)
            profileScreenViewModel.addUserProfilePicture(context.contentResolver.openInputStream(uri)!!,context.contentResolver.getType(uri)!!)
        }
    }

    ConstraintLayout {
        val (background, profile, editButton) = createRefs()
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(
                    brush = Brush.linearGradient(
                        0f to Color(0xFF6200EE),
                        1f to Color(0xFF9E72E1)
                    ),
                )
                .constrainAs(background) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                }
        )

        Image(
            bitmap = profileScreenViewModel.userProfilePicture?.asImageBitmap() ?: ImageBitmap.imageResource(id = R.drawable.projectlogotransparencycircular),
            contentDescription = "avatar",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(250.dp)
                .clip(CircleShape)
                .border(3.dp, Color.Gray, CircleShape)
                .constrainAs(profile) {
                    top.linkTo(background.bottom)
                    bottom.linkTo(background.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )

        IconButton(
            onClick = { launcher.launch("image/*") },
            modifier = Modifier
                .constrainAs(editButton) {
                    top.linkTo(profile.bottom)
                    bottom.linkTo(profile.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
                .offset(x = 60.dp, y = (-20).dp)
                .border(2.dp, Color.Gray, CircleShape)
                .clip(
                    CircleShape
                )
                .background(color = Color.White),
        ) {
            Icon(
                imageVector = Icons.Filled.Edit,
                contentDescription = "Profile",
                tint = Color.Black
            )
        }
    }
}


@Composable
fun ConfirmDeleteDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Surface {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Are you sure you want to delete your account? This operation cannot be undone!")
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Button(onClick = onDismiss) {
                        Text("No")
                    }
                    Button(onClick = onConfirm) {
                        Text("Yes")
                    }
                }
            }
        }
    }
}

@Composable
fun PasswordVerificationDialog(
    passwordInput: String,
    onInputChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Enter your password to confirm the deletion of your account.")
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = passwordInput,
                    onValueChange = onInputChange,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Password") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Button(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Button(onClick = onConfirm) {
                        Text("Accept")
                    }
                }
            }
        }
    }
}

@Composable
fun ResultDialog(isPasswordCorrect: Boolean, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Surface {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    if (isPasswordCorrect) "The account has been deleted!"
                    else "Unable to delete account! An error has occurred!"
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = onDismiss) {
                    Text("OK")
                }
            }
        }
    }
}