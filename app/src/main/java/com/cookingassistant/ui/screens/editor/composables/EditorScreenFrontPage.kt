package com.cookingassistant.ui.screens.editor.composables

import android.graphics.BitmapFactory
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.constraintlayout.compose.ConstraintLayout
import coil3.compose.rememberAsyncImagePainter
import com.cookingassistant.R
import com.cookingassistant.data.DTO.CategoriesGetDTO
import com.cookingassistant.data.DTO.DifficultiesGetDTO
import com.cookingassistant.data.DTO.OccasionsGetDTO
import com.cookingassistant.data.DTO.idNameClassType
import com.cookingassistant.ui.screens.editor.EditorScreenViewModel
import com.cookingassistant.util.ImageConverter

@Composable
fun FrontPage(viewModel: EditorScreenViewModel) {
    val context = LocalContext.current

    if (viewModel.image == null) {
        viewModel.image = BitmapFactory.decodeResource(context.resources, R.drawable.ic_launcher_background)
    }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            viewModel.image = ImageConverter.uriToBitmap(context,uri)
        }
    }

    val difficulties by viewModel.difficultyOptions.collectAsState()
    val occasions by viewModel.occasionOptions.collectAsState()
    val categories by viewModel.categoryOptions.collectAsState()



    Box(
        modifier = Modifier.fillMaxSize()
    ){
        LazyColumn (
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).
            padding(vertical = 50.dp, horizontal = 10.dp).align(Alignment.TopCenter),
        ) {
            item { Text(text = "Name", fontSize = 18.sp, modifier = Modifier.padding(start = 16.dp)) }
            item { Spacer(modifier = Modifier.height(8.dp)) }
            item {TextField(
                value = viewModel.name,
                label = { Text("Enter your name") },
                onValueChange = {
                    viewModel.name = it
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )}
            item {Spacer(modifier = Modifier.height(8.dp))}
            item{
                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ){
                    Column(modifier =  Modifier.weight(1f)) {
                        Text(text = "Difficulty", color = MaterialTheme.colorScheme.onBackground,fontSize = 18.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
                        SearchableDropdownButton(
                            header = "Difficulty",
                            choosenOption = viewModel.difficulty,
                            options = difficulties,
                            modifier = Modifier.fillMaxWidth().align(Alignment.CenterHorizontally)
                        ){
                                selectedDifficulty ->
                            viewModel.difficulty = selectedDifficulty as DifficultiesGetDTO?
                        }
                    }
                    Column(modifier =  Modifier.weight(1f)) {
                        Text(text = "Occasion", color = MaterialTheme.colorScheme.onBackground, fontSize = 18.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
                        SearchableDropdownButton(
                            header = "Occasion",
                            choosenOption = viewModel.occasion,
                            options =  occasions,
                            modifier = Modifier.fillMaxWidth().align(Alignment.CenterHorizontally)
                        ){
                                selectedOccasion ->
                            viewModel.occasion = selectedOccasion as OccasionsGetDTO?
                        }
                    }
                }
            }
            item{Spacer(modifier = Modifier.height(8.dp))}
            item{Column(modifier = Modifier.fillMaxWidth()){
                Text(text = "Category", color = MaterialTheme.colorScheme.onBackground, fontSize = 18.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
                SearchableDropdownButton(
                    header = "Category",
                    choosenOption = viewModel.category,
                    options = categories,
                    modifier = Modifier.fillMaxWidth(0.8f).align(Alignment.CenterHorizontally)
                ){
                        selectedCategory ->
                    viewModel.category = selectedCategory as CategoriesGetDTO?
                }
            }}
            item{Spacer(modifier = Modifier.height(8.dp))}
            item{Text(text = "Recipe image", color = MaterialTheme.colorScheme.onBackground, fontSize = 18.sp, modifier = Modifier.fillMaxWidth().wrapContentWidth(Alignment.CenterHorizontally))}
            item{Spacer(modifier = Modifier.height(8.dp))}

            item{ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
                val (profile, editButton) = createRefs()

                Image(
                    painter = rememberAsyncImagePainter(viewModel.image ?: R.drawable.ic_launcher_background),
                    contentDescription = "recipePicture",
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier.constrainAs(profile){
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
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
            }}
            item{Spacer(modifier = Modifier.height(8.dp))}
            item{Text(text = "Description",color = MaterialTheme.colorScheme.onBackground, fontSize = 18.sp, modifier = Modifier.padding(start = 16.dp))}

            item{Spacer(modifier = Modifier.height(8.dp))}

            item{TextField(
                value = viewModel.description,
                label = { Text("Enter your description") },
                onValueChange = {
                    viewModel.description = it
                },
                modifier = Modifier.fillMaxWidth().height(200.dp)
            )}
        }
        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
            val (right) = createRefs()

            IconButton(
                onClick = { viewModel.navigateTo("details") },
                modifier = Modifier.size(56.dp).background(Color(0xFF3700B3), shape = CircleShape)
                    .padding(8.dp).constrainAs(right) {
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
fun SearchableDropdownButton(
    header: String,
    choosenOption: idNameClassType?,
    options: List<idNameClassType>,
    modifier: Modifier = Modifier,
    onSelectOption: (idNameClassType?) -> Unit
) {
    var selectedOption by remember { mutableStateOf(choosenOption) }
    var isDialogOpen by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }
    val filteredOptions = options.filter { it.name.contains(searchText, ignoreCase = true) }

    Button(
        modifier = modifier,
        onClick = { isDialogOpen = true },
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9E72E1), contentColor = Color.White),
        shape = RoundedCornerShape(20)
    ) {
        Text(selectedOption?.name ?: "Select an option")
    }

    if (isDialogOpen) {
        Dialog(onDismissRequest = { isDialogOpen = false }) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                tonalElevation = 8.dp,
            ) {
                Column(modifier = Modifier.padding(8.dp)) {

                    Text(text=header,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    OutlinedTextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        label = { Text("Search...") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyColumn(modifier = Modifier.fillMaxHeight(0.3f)){
                        items(filteredOptions) { option ->
                            TextButton(
                                onClick = {
                                    selectedOption = option
                                    onSelectOption(option)
                                    isDialogOpen = false
                                    searchText = ""
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(option.name)
                            }
                        }
                    }
                    Button(onClick = { isDialogOpen = false }, modifier =Modifier.align(Alignment.CenterHorizontally).fillMaxWidth(0.6f)) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
        }
    }
}