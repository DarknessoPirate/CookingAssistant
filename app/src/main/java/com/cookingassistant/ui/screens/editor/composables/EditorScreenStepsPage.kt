package com.cookingassistant.ui.screens.editor.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.cookingassistant.data.DTO.RecipePostDTO
import com.cookingassistant.ui.screens.editor.EditorScreenViewModel
import com.cookingassistant.ui.screens.editor.State
import com.cookingassistant.util.ImageConverter

@Composable
fun StepsPage(navController: NavController,viewModel: EditorScreenViewModel) {
    var showAcceptDialog by remember { mutableStateOf(false) }
    var showAcceptedConfirmationDialog by remember { mutableStateOf(false) }

    var firstLazyColumnBottom by remember { mutableStateOf(0f) }
    var secondLazyColumnTop by remember { mutableStateOf(0f) }
    var secondLazyColumnHeight by remember { mutableStateOf(0f) }

    Box(
        modifier = Modifier.fillMaxSize()
    ){
        LazyColumn(
            modifier = Modifier
                .fillMaxSize().background(MaterialTheme.colorScheme.background)
                .padding(vertical = 50.dp, horizontal = 10.dp).align(Alignment.TopCenter).
                onGloballyPositioned { coordinates ->
                    val position = coordinates.positionInRoot().y + coordinates.size.height
                    firstLazyColumnBottom = position
                }
            ,
        ) {
            item{Text(
                text = "Steps",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 18.sp,
            )}
            item{StepsMaker(
                onPositionChanged = { position ->
                    secondLazyColumnTop = position
                    secondLazyColumnHeight = firstLazyColumnBottom - secondLazyColumnTop
                },
                availableHeight = secondLazyColumnHeight,
                stepList = viewModel.steps,
                onStepListChange = { updatedList ->
                    viewModel.steps = updatedList
                }
            )}
        }

        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
            val (right, accept) = createRefs()


            IconButton(
                onClick = { viewModel.navigateTo("details") },
                modifier = Modifier
                    .size(56.dp)
                    .background(Color(0xFF3700B3), shape = CircleShape)
                    .padding(8.dp)
                    .constrainAs(right) {
                        bottom.linkTo(parent.bottom, margin = 40.dp)
                        start.linkTo(parent.start, margin = 16.dp)
                    }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "right arrow",
                    tint = Color.White
                )
            }

            IconButton(
                onClick = {showAcceptDialog = true },
                modifier = Modifier
                    .size(56.dp)
                    .background(Color(0xFF8FD65C), shape = CircleShape)
                    .padding(8.dp)
                    .constrainAs(accept) {
                        bottom.linkTo(parent.bottom, margin = 40.dp)
                        end.linkTo(parent.end, margin = 16.dp)
                    }
            ) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = "accept",
                    tint = Color.White
                )
            }
        }
    }

    if (showAcceptDialog) {
        AcceptDialog(
            onConfirm = {
                showAcceptDialog = false
                showAcceptedConfirmationDialog = true
            },
            onDismiss = { showAcceptDialog = false }
        )
    }
    if (showAcceptedConfirmationDialog) {
        AcceptedConfirmationDialog(
            navController = navController,
            viewModel = viewModel,
            onDismiss = { showAcceptedConfirmationDialog = false }
        )
    }
}

@Composable
fun StepsMaker(
    onPositionChanged: (Float) -> Unit,
    availableHeight: Float,
    stepList: List<String>,
    onStepListChange: (List<String>) -> Unit
) {
    val density = LocalDensity.current.density

    var showDialog by remember { mutableStateOf(false) }
    var editingIndex by remember { mutableStateOf<Int?>(null) }



    Button(
        onClick = {
            editingIndex = null
            showDialog = true
        },
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9E72E1),contentColor = Color.White),
        shape = RoundedCornerShape(20)
    ) {
        Text("Add Step")
    }

    Spacer(modifier = Modifier.height(8.dp))

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .height(if((availableHeight / density).dp > 300.dp) (availableHeight / density).dp else 300.dp)
            .border(2.dp, Color.Gray, shape = RoundedCornerShape(8.dp)).
            onGloballyPositioned { coordinates ->
                val position = coordinates.positionInRoot().y
                onPositionChanged(position)
            }
    ) {
        items(stepList.size) { index ->
            StepItem(
                upEnabled = index > 0,
                downEnabled = index < stepList.size - 1,
                text = stepList[index],
                onEdit = {
                    editingIndex = index
                    showDialog = true
                },
                onDelete = {
                    val updatedList = stepList.toMutableList().apply { removeAt(index) }
                    onStepListChange(updatedList)
                },
                onMoveUp = {
                    if (index > 0) {
                        val updatedList = stepList.toMutableList().apply {
                            swap(index, index - 1)
                        }
                        onStepListChange(updatedList)
                    }
                },
                onMoveDown = {
                    if (index < stepList.size - 1) {
                        val updatedList = stepList.toMutableList().apply {
                            swap(index, index + 1)
                        }
                        onStepListChange(updatedList)
                    }
                }
            )
        }
    }

    if (showDialog) {
        StepDialog(
            step = editingIndex?.let { stepList[it] },
            onDismiss = { showDialog = false },
            onSave = { step ->
                val updatedList = stepList.toMutableList().apply {
                    if (editingIndex == null) {
                        add(step)
                    } else {
                        set(editingIndex!!, step)
                    }
                }
                onStepListChange(updatedList)
                showDialog = false
                editingIndex = null
            }
        )
    }

}

@Composable
fun StepItem(
    upEnabled: Boolean,
    downEnabled: Boolean,
    text: String,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit
) {
    Row(
        modifier = Modifier
            .clickable(onClick = onEdit)
            .fillMaxWidth()
            .padding(8.dp)
            .border(2.dp, Color.LightGray, shape = RoundedCornerShape(8.dp)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text, modifier = Modifier
            .weight(1f)
            .padding(8.dp))
        IconButton(onClick = onMoveUp, enabled = upEnabled) {
            Icon(
                Icons.Default.ArrowUpward,
                contentDescription = "Move Up"
            )
        }
        IconButton(onClick = onMoveDown, enabled = downEnabled) {
            Icon(
                Icons.Default.ArrowDownward,
                contentDescription = "Move Down"
            )
        }
        IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, contentDescription = "Delete") }
    }
}

@Composable
fun StepDialog(
    step: String? = null,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var text by remember { mutableStateOf(step ?: "") }

    Dialog(onDismissRequest = { onDismiss() }) {
        Surface(shape = MaterialTheme.shapes.medium, modifier = Modifier.padding(16.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Add/Edit Step")

                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("Edit Item") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.7f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                    Button(onClick = { onDismiss() }) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        if (text.isNotEmpty()) {
                            onSave(text)
                        }
                    }) {
                        Text("Accept")
                    }
                }
            }
        }
    }
}

@Composable
fun AcceptDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Surface {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Are you sure you want to accept the changes?")
                Spacer(modifier = Modifier.height(16.dp))
                Row {
                    Button(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = onConfirm) {
                        Text("Accept")
                    }
                }
            }
        }
    }
}

@Composable
fun AcceptedConfirmationDialog(navController: NavController, viewModel: EditorScreenViewModel, onDismiss: () -> Unit) {
    if (viewModel.checkRecipe()) {
        val ingredientName = viewModel.ingredients.map { it.ingredientName }
        val quantity = viewModel.ingredients.map { it.quantity }
        val unit = viewModel.ingredients.map { it.unit }

        val convertedImage = ImageConverter.bitmapToByteArray(viewModel.image!!)

        val recipe = RecipePostDTO(
            name = viewModel.name,
            description = viewModel.description,
            imageData = convertedImage,
            serves = viewModel.serves,
            difficultyId = viewModel.difficulty?.id!!,
            timeInMinutes = viewModel.time,
            categoryId = viewModel.category?.id!!,
            occasionId = viewModel.occasion?.id!!,
            caloricity = viewModel.calories!!,
            ingredientNames = ingredientName,
            ingredientQuantities = quantity,
            ingredientUnits = unit,
            steps = viewModel.steps,
        )

        if(viewModel.state == State.Create)
        {
            viewModel.createRecipe(recipe)
        }
        else if(viewModel.state == State.Modify)
        {
            viewModel.modifyRecipe(recipe)
        }

        Dialog(onDismissRequest = {
            onDismiss()
            navController.navigate("home")
        }) {
            Surface {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Your changes have been successfully accepted!")
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = {
                        onDismiss()
                        navController.navigate("home")
                    }) {
                        Text("OK")
                    }
                }
            }
        }
    } else {
        Dialog(onDismissRequest = onDismiss) {
            Surface {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Text("The form is not fully completed!")
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onDismiss) {
                        Text("OK")
                    }
                }
            }
        }
    }
}