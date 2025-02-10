package com.cookingassistant.ui.screens.editor.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.constraintlayout.compose.ConstraintLayout
import com.cookingassistant.data.DTO.RecipeIngredientGetDTO
import com.cookingassistant.ui.screens.editor.EditorScreenViewModel


@Composable
fun DetailsPage(viewModel: EditorScreenViewModel) {
    var firstLazyColumnBottom by remember { mutableStateOf(0f) }
    var secondLazyColumnTop by remember { mutableStateOf(0f) }
    var secondLazyColumnHeight by remember { mutableStateOf(0f) }


    val ingredients by viewModel.ingredientsOptions.collectAsState()
    val units by viewModel.unitOptions.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize()
    ){
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(vertical = 50.dp, horizontal = 10.dp).align(Alignment.TopCenter).
                onGloballyPositioned { coordinates ->
                    val position = coordinates.positionInRoot().y + coordinates.size.height
                    firstLazyColumnBottom = position
                },
        ) {
            item{Text(
                text = "Calories",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 18.sp,
            )}
            item{NumberPicker("Calories",viewModel.calories,0,99999){
                    selectedCalories ->
                viewModel.calories = selectedCalories
            }}
            item{Text(
                text = "Preparation time",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 18.sp,
            )}
            item{TimePicker(
                timeInMinutes = viewModel.time,
                onTimeSelected = { selectedMinutes -> viewModel.time = selectedMinutes }
            )}
            item{Text(
                text = "Serves",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 18.sp,
            )}
            item{NumberPicker("Serves",viewModel.serves,0,200){
                    selectedServes ->
                viewModel.serves = selectedServes
            }}
            item{Text(
                text = "Ingredients",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 18.sp,
            )}
            item{IngredientPicker(
                onPositionChanged = { position ->
                    secondLazyColumnTop = position
                    secondLazyColumnHeight = firstLazyColumnBottom - secondLazyColumnTop
                },
                availableHeight = secondLazyColumnHeight,
                ingredientList = viewModel.ingredients,
                ingredientOptions = ingredients,
                unitOptions = units,
                onIngredientListChange = { updatedList ->
                    viewModel.ingredients = updatedList
                }
            )}
        }

        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
            val (left,right) = createRefs()

            IconButton(
                onClick = { viewModel.navigateTo("front") },
                modifier = Modifier
                    .size(56.dp)
                    .background(Color(0xFF3700B3), shape = CircleShape)
                    .padding(8.dp)
                    .constrainAs(left) {
                        bottom.linkTo(parent.bottom, margin = 40.dp)
                        start.linkTo(parent.start, margin = 16.dp)
                    }
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "right arrow",
                    tint = Color.White
                )
            }

            IconButton(
                onClick = { viewModel.navigateTo("steps") },
                modifier = Modifier
                    .size(56.dp)
                    .background(Color(0xFF3700B3), shape = CircleShape)
                    .padding(8.dp)
                    .constrainAs(right) {
                        bottom.linkTo(parent.bottom, margin = 40.dp)
                        end.linkTo(parent.end, margin = 16.dp)
                    }
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowForward,
                    contentDescription = "left arrow",
                    tint = Color.White
                )
            }
        }
    }


}

@Composable
fun NumberPicker(
    header: String,
    number: Int?,
    minValue: Int = 0,
    maxValue: Int = 100,
    onSelectOption: (Int) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    Button(
        onClick = { showDialog = true },
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9E72E1), contentColor = Color.White),
        shape = RoundedCornerShape(20)
    ) {
        Text(text = number?.toString() ?: "Select number")
    }

    if (showDialog) {
        Dialog(onDismissRequest = { showDialog = false }) {
            Surface(
                modifier = Modifier.padding(16.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                var currentNumber by remember { mutableStateOf(number?.toString() ?: minValue.toString()) }

                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text=header)

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = {
                            val newValue = (currentNumber.toIntOrNull() ?: minValue) - 1
                            if (newValue >= minValue) {
                                currentNumber = newValue.toString()
                            }
                        }) {
                            Icon(Icons.Default.Remove, contentDescription = "Decrease")
                        }

                        TextField(
                            value = currentNumber,
                            onValueChange = { newText ->
                                val newValue = newText.filter { it.isDigit() }.toIntOrNull() ?: minValue
                                if (newValue in minValue..maxValue) {
                                    currentNumber = newValue.toString()
                                }
                            },
                            modifier = Modifier.width(80.dp),
                            singleLine = true
                        )

                        IconButton(onClick = {
                            val newValue = (currentNumber.toIntOrNull() ?: minValue) + 1
                            if (newValue <= maxValue) {
                                currentNumber = newValue.toString()
                            }
                        }) {
                            Icon(Icons.Default.Add, contentDescription = "Increase")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(onClick = { showDialog = false }) {
                            Text("Cancel")
                        }
                        Button(onClick = {
                            val selectedNumber = currentNumber.toIntOrNull() ?: minValue
                            onSelectOption(selectedNumber)
                            showDialog = false
                        }) {
                            Text("Accept")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TimePicker(
    timeInMinutes: Int?,
    onTimeSelected: (Int) -> Unit
) {
    var isDialogOpen by remember { mutableStateOf(false) }
    var selectedTime by remember { mutableStateOf(timeInMinutes?.let { formatTime(it / 60, it % 60) } ?: "Select time") }
    var errorText by remember { mutableStateOf("") }

    Button(
        onClick = { isDialogOpen = true },
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9E72E1),contentColor = Color.White),
        shape = RoundedCornerShape(20)
    ) {
        Text(text = selectedTime)
    }


    if (isDialogOpen) {
        Dialog(onDismissRequest = {
            isDialogOpen = false
            errorText = ""}) {
            var firstHour by remember { mutableStateOf((timeInMinutes ?: 0) / 60) }
            var firstMinute by remember { mutableStateOf((timeInMinutes ?: 0) % 60) }
            Surface {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Preparation time")
                    Spacer(modifier = Modifier.height(8.dp))
                    Row {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Hour",
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                            IconButton(onClick = {
                                val newValue = firstHour + 1
                                if (newValue <= 99) {
                                    firstHour = newValue
                                }
                            }) {
                                Icon(Icons.Default.Add, contentDescription = "Increase")
                            }
                            TextField(
                                value = firstHour.toString(),
                                onValueChange = { newText ->
                                    val newValue =
                                        newText.filter { it.isDigit() }.toIntOrNull() ?: 0
                                    if (newValue in 0..99) {
                                        firstHour = newValue
                                    }
                                },
                                modifier = Modifier.width(80.dp),
                                textStyle = LocalTextStyle.current.copy(
                                    textAlign = TextAlign.Center
                                ),
                                singleLine = true
                            )
                            IconButton(onClick = {
                                val newValue = firstHour - 1
                                if (newValue >= 0) {
                                    firstHour = newValue
                                }
                            }) {
                                Icon(Icons.Default.Remove, contentDescription = "Decrease")
                            }
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Minute",
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                            IconButton(onClick = {
                                val newValue = firstMinute + 1
                                if (newValue <= 59) {
                                    firstMinute = newValue
                                }
                            }) {
                                Icon(Icons.Default.Add, contentDescription = "Increase")
                            }
                            TextField(
                                value = firstMinute.toString(),
                                onValueChange = { newText ->
                                    val newValue =
                                        newText.filter { it.isDigit() }.toIntOrNull() ?: 0
                                    if (newValue in 0..59) {
                                        firstMinute = newValue
                                    }
                                },
                                modifier = Modifier.width(80.dp),
                                textStyle = LocalTextStyle.current.copy(
                                    textAlign = TextAlign.Center
                                ),
                                singleLine = true
                            )
                            IconButton(onClick = {
                                val newValue = firstMinute - 1
                                if (newValue >= 0) {
                                    firstMinute = newValue
                                }
                            }) {
                                Icon(Icons.Default.Remove, contentDescription = "Decrease")
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = errorText)
                    Spacer(modifier = Modifier.height(16.dp))


                    Row(modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(onClick = {
                            errorText = ""
                            isDialogOpen = false }) {
                            Text("Cancel")
                        }
                        Button(onClick = {
                            if (firstHour == 0 && firstMinute == 0) {
                                errorText = "Need to set time!"
                            } else {
                                val totalMinutes = firstHour * 60 + firstMinute
                                selectedTime = formatTime(firstHour, firstMinute)
                                onTimeSelected(totalMinutes)
                                errorText = ""
                                isDialogOpen = false
                            }

                        }) {
                            Text("Accept")
                        }
                    }
                }
            }
        }
    }
}

fun formatTime(hour: Int, minute: Int): String {
    val hourText = if (hour > 0) "$hour hours" else ""
    val minuteText = if (minute > 0) "$minute minutes" else ""

    return when {
        hour > 0 && minute > 0 -> "$hourText and $minuteText"
        hour > 0 -> hourText
        minute > 0 -> minuteText
        else -> ""
    }
}

@Composable
fun IngredientPicker(
    onPositionChanged: (Float) -> Unit,
    availableHeight: Float,
    ingredientList: List<RecipeIngredientGetDTO>,
    ingredientOptions: List<String>,
    unitOptions: List <String>,
    onIngredientListChange: (List<RecipeIngredientGetDTO>) -> Unit) {

    val density = LocalDensity.current.density

    var showDialog by remember { mutableStateOf(false) }
    var editingIndex by remember { mutableStateOf<Int?>(null) }

    Button(
        onClick = { editingIndex = null
            showDialog = true },
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9E72E1),contentColor = Color.White),
        shape = RoundedCornerShape(20)
    ) {
        Text("Add Ingredient")
    }

    Spacer(modifier = Modifier.height(8.dp))

    LazyColumn(modifier = Modifier
        .fillMaxWidth()
        .height(if((availableHeight / density).dp > 300.dp) (availableHeight / density).dp else 300.dp)
        .border(2.dp, Color.Gray, shape = RoundedCornerShape(8.dp)).
        onGloballyPositioned { coordinates ->
            val position = coordinates.positionInRoot().y
            onPositionChanged(position)
        }
    ) {
        items(ingredientList.size) { index ->
            IngredientItem(
                upEnabled = index > 0,
                downEnabled = index < ingredientList.size - 1,
                ingredient = ingredientList[index],
                onEdit = {
                    editingIndex = index
                    showDialog = true
                },
                onDelete = {
                    val updatedList = ingredientList.toMutableList().apply { removeAt(index) }
                    onIngredientListChange(updatedList)
                },
                onMoveUp = {
                    if (index > 0) {
                        val updatedList = ingredientList.toMutableList().apply {
                            swap(index, index - 1)
                        }
                        onIngredientListChange(updatedList)
                    }
                },
                onMoveDown = {
                    if (index < ingredientList.size - 1) {
                        val updatedList = ingredientList.toMutableList().apply {
                            swap(index, index + 1)
                        }
                        onIngredientListChange(updatedList)
                    }
                }
            )
        }
    }

    if (showDialog) {
        IngredientDialog(
            ingredient = editingIndex?.let { ingredientList[it] },
            ingredientOptions = ingredientOptions,
            unitOptions = unitOptions,
            onDismiss = { showDialog = false },
            onSave = { ingredient ->
                val updatedList = ingredientList.toMutableList().apply {
                    if (editingIndex == null) {
                        add(ingredient)
                    } else {
                        set(editingIndex!!, ingredient)
                    }
                }
                onIngredientListChange(updatedList)
                showDialog = false
                editingIndex = null
            }
        )
    }

}

@Composable
fun IngredientItem(
    upEnabled: Boolean,
    downEnabled: Boolean,
    ingredient: RecipeIngredientGetDTO,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit
) {
    Row(modifier = Modifier
        .clickable(onClick = onEdit)
        .fillMaxWidth()
        .padding(8.dp)
        .border(2.dp, Color.LightGray, shape = RoundedCornerShape(8.dp)),
        verticalAlignment = Alignment.CenterVertically) {
        Text("${ingredient.quantity} ${ingredient.unit} ${ingredient.ingredientName}", modifier = Modifier
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
fun IngredientDialog(
    ingredient: RecipeIngredientGetDTO? = null,
    ingredientOptions: List<String>,
    unitOptions: List <String>,
    onDismiss: () -> Unit,
    onSave: (RecipeIngredientGetDTO) -> Unit
) {
    var name by remember { mutableStateOf(ingredient?.ingredientName ?: "") }
    var amount by remember { mutableStateOf(ingredient?.quantity ?: "") }
    var unit by remember { mutableStateOf(ingredient?.unit ?: "") }

    Dialog(onDismissRequest = { onDismiss() }) {
        Surface(shape = MaterialTheme.shapes.medium, modifier = Modifier.padding(16.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text="Add/Edit Ingredient",
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                ComboBox(
                    label = "Ingredient",
                    options = ingredientOptions,
                    selectedOption = name,
                    onOptionSelected = { name = it }
                )

                DecimalNumericTextField(
                    label = "Amount",
                    value = amount,
                    onValueChange = { amount = it }
                )

                TextComboBox (
                    label = "Unit",
                    options = unitOptions,
                    selectedOption = unit,
                    onOptionSelected = { unit = it }
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
                        if (name.isNotBlank() && amount.isNotEmpty() && unit.isNotBlank()) {
                            onSave(RecipeIngredientGetDTO(name, amount, unit))
                        }
                    }) {
                        Text("Save")
                    }
                }
            }
        }
    }
}

@Composable
fun DecimalNumericTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = { input ->
            if (input.isEmpty() || input.matches(Regex("^\\d{1,5}(\\.\\d)?\\.?\$"))) {
                onValueChange(input)
            }
        },
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun ComboBox(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    Box(modifier = Modifier.fillMaxWidth()) {
        Button(
            onClick = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9E72E1),contentColor = Color.White),
            shape = RoundedCornerShape(20)
        ) {
            Text(text = if (selectedOption.isEmpty()) label else selectedOption)
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Dropdown",
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.6f)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search $label") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )

            Column(
                modifier = Modifier
                    .height(200.dp)
                    .verticalScroll(scrollState)
                    .padding(8.dp)
            ) {
                options.filter { it.contains(searchQuery, ignoreCase = true) }.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onOptionSelected(option)
                            expanded = false
                            searchQuery = ""
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TextComboBox(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
){
    var expanded by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    Box(modifier = Modifier.fillMaxWidth()) {
        Button(
            onClick = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9E72E1),contentColor = Color.White),
            shape = RoundedCornerShape(20)
        ) {
            Text(text = if (selectedOption.isEmpty()) label else selectedOption)
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Dropdown",
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.6f)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Add/Search $label") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = { onOptionSelected(searchQuery)
                        expanded = false
                        searchQuery = "" },
                    modifier = Modifier
                        .size(56.dp)
                        .background(Color(0xFF9E72E1), shape = CircleShape)
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "add unit",
                        tint = Color.White
                    )
                }
            }



            Column(
                modifier = Modifier
                    .height(200.dp)
                    .verticalScroll(scrollState)
                    .padding(8.dp)
            ) {
                options.filter { it.contains(searchQuery, ignoreCase = true) }.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onOptionSelected(option)
                            expanded = false
                            searchQuery = ""
                        }
                    )
                }
            }
        }
    }
}

fun <T> MutableList<T>.swap(index1: Int, index2: Int) {
    val temp = this[index1]
    this[index1] = this[index2]
    this[index2] = temp
}