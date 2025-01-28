package com.cookingassistant.ui.screens.RecipesList

import android.graphics.Bitmap
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.AbsoluteCutCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardDoubleArrowLeft
import androidx.compose.material.icons.filled.KeyboardDoubleArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.cookingassistant.R
import com.cookingassistant.data.objects.TextFormatting
import com.cookingassistant.ui.composables.RatingToStars
import com.cookingassistant.ui.screens.editor.EditorScreenViewModel
import com.cookingassistant.ui.screens.recipescreen.RecipeScreenViewModel
import kotlin.math.roundToInt


@Composable
fun RecipeList(
    _navController : NavHostController,
    _recipeScreenViewModel: RecipeScreenViewModel,
    _editorScreenViewModel: EditorScreenViewModel,
    viewModel: RecipesListViewModel = viewModel()
) {
    val recipesPreviewList by viewModel.recipes.collectAsState()
    val totalPages by viewModel.totalPages.collectAsState()
    val currentPage by viewModel.currentPage.collectAsState()
    val inputPageNumber by viewModel.inputPageNumber.collectAsState()
    val images by  viewModel.recipeImages.collectAsState()
    val foundResult by viewModel.foundResults.collectAsState()
    val currentState by viewModel.currentState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val loadedImages by viewModel.loadedImages.collectAsState()

    var offsetX by remember { mutableStateOf(0f) }
    var isDialogDelete by remember { mutableStateOf(false) }
    var tempIdDelete by remember { mutableStateOf(0) }

    val sizeAnim1 by animateFloatAsState(
        targetValue = if (foundResult == 1) 1f else 0f
    )

    if(foundResult == 2) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceContainer),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                textAlign = TextAlign.Center,
                fontSize = 30.sp,
                color = MaterialTheme.colorScheme.error,
                text = "No results found or failed to fetch recipes."
            )
        }
        return
    } else if(foundResult == 0) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                textAlign = TextAlign.Center,
                fontSize = 30.sp,
                color = MaterialTheme.colorScheme.onBackground,
                text = "Loading..."
            )
        }
        return
    }
    Box(
        Modifier
            .fillMaxWidth()
            .fillMaxHeight(sizeAnim1)
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onHorizontalDrag = {change, dragAmount ->
                        change.consume()
                        offsetX = dragAmount
                    },
                    onDragStart = {offsetX = 0f},
                    onDragEnd = {
                        if (offsetX < -5f && currentPage != 0)
                        {
                            viewModel.onPageButtonClicked(1)
                        }
                        else if(offsetX > 5f && currentPage <= totalPages) {
                            viewModel.onPageButtonClicked(-1)
                        }
                    }
                )
            }
    ) {
        LazyColumn(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceContainerLow)
                .padding(bottom = 50.dp)
                .wrapContentWidth(Alignment.CenterHorizontally)
        ) {
            item { Spacer(
                Modifier
                    .fillMaxWidth()
                    .padding(bottom = 50.dp)) }
            item {
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp), verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                    if(currentPage != 1) {
                        IconButton(
                            modifier = Modifier.padding(end = 10.dp),
                            onClick = { viewModel.onPageButtonClicked(-1) }
                        ) {
                            Icon(
                                modifier = Modifier.fillMaxSize(),
                                imageVector = Icons.Filled.KeyboardDoubleArrowLeft,
                                contentDescription = "Page back",
                                tint = MaterialTheme.colorScheme.inverseSurface
                            )
                        }
                    }
                    if(currentPage < totalPages) {
                        IconButton(
                            onClick = { viewModel.onPageButtonClicked(1) }
                        ) {
                            Icon(
                                modifier = Modifier.fillMaxSize(),
                                imageVector = Icons.Filled.KeyboardDoubleArrowRight,
                                contentDescription = "Page next",
                                tint = MaterialTheme.colorScheme.inverseSurface
                            )
                        }
                    }
                    Text(
                        textAlign = TextAlign.End,
                        fontSize = 20.sp,
                        text = "Page $currentPage out of $totalPages",
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 10.dp)
                            .weight(1f)
                    )

                }

            }
            item{
                Row(modifier= Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = inputPageNumber,
                        onValueChange = {viewModel.onInputPageNumberChange(it)},
                        singleLine = true,
                        shape = AbsoluteCutCornerShape(0.dp),
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
                        keyboardActions = KeyboardActions(onGo = {
                            viewModel.onPageNumberSubmit()
                        }),
                        label = {Text(
                            text="Go to page",
                            modifier = Modifier.padding(end = 10.dp),
                            color = MaterialTheme.colorScheme.onBackground
                        )}
                    )
                }

            }
            items(recipesPreviewList) {item ->
                Column(
                    Modifier
                        .padding(10.dp)
                        .height(500.dp)
                        .clickable {
                            _recipeScreenViewModel.loadRecipe(item.id)
                            _navController.navigate("recipeScreen")
                        }
                        .background(MaterialTheme.colorScheme.background)
                        .wrapContentHeight(Alignment.CenterVertically)
                        .padding(bottom = 10.dp)
                    ,
                    horizontalAlignment = Alignment.CenterHorizontally

                ) {
                    //----------------------
                    //Edit and Delete recipe
                    //----------------------
                    if (currentState == State.Own) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = {
                                    _editorScreenViewModel.loadRecipe(item.id)
                                    _navController.navigate("editor")
                                },
                                modifier = Modifier
                                    .size(56.dp)
                                    .background(Color(0xFF89CE91), shape = CircleShape)
                                    .padding(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Edit,
                                    contentDescription = "edit recipe",
                                    tint = Color.White
                                )
                            }

                            IconButton(
                                onClick = {
                                    isDialogDelete = true
                                    tempIdDelete = item.id
                                },
                                modifier = Modifier
                                    .size(56.dp)
                                    .background(Color(0xFFD23B3B), shape = CircleShape)
                                    .padding(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Delete,
                                    contentDescription = "delete recipe",
                                    tint = Color.White
                                )
                            }
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(all = 10.dp)
                            .height(100.dp)
                    ) {
                        Text(
                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                            text = item.name,
                            fontWeight = FontWeight(500),
                            fontSize = 18.sp,
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(1f)
                                .wrapContentHeight(align = Alignment.CenterVertically),
                            textAlign = TextAlign.Center,
                            overflow = TextOverflow.Ellipsis,

                            )
                        Column(
                            horizontalAlignment = Alignment.Start,
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 10.dp)
                                .align(Alignment.CenterVertically)
                        ) {
                            Text(
                                color = MaterialTheme.colorScheme.onBackground,
                                text = "Difficulty: ${item.difficultyName}",
                                modifier = Modifier.verticalScroll(rememberScrollState()),
                                fontSize = 15.sp
                            )
                            RatingToStars(item.ratings.roundToInt(),
                                modifier = Modifier
                                    .size(16.dp)
                                    .shadow(elevation = 4.dp),
                                color = MaterialTheme.colorScheme.onBackground
                            ) {
                                Text(
                                    color = MaterialTheme.colorScheme.onBackground,
                                    text = "Rating:",
                                    fontSize = 15.sp
                                )
                            }
                            Text(
                                color = MaterialTheme.colorScheme.onBackground,
                                text = "Vote count: ${item.voteCount}",
                                modifier = Modifier.verticalScroll(rememberScrollState()),
                                fontSize = 15.sp
                            )
                        }
                    }
                    Text(
                        color = MaterialTheme.colorScheme.onBackground,
                        text = "Category: ${item.categoryName}, Occasion: ${item.occasionName}",
                        fontSize = 15.sp,
                    )
                    Text(
                        color = MaterialTheme.colorScheme.onBackground,
                        text = "Calories: ${item.caloricity}, Completion time: ${TextFormatting.formatTime(item.timeInMinutes)}",
                        fontSize = 15.sp
                    )
                    if(images[item.id] != null ) {
                        val rawBitmap : Bitmap = images.getValue(item.id)!!
                        Image(
                            contentScale = ContentScale.Crop,
                            bitmap = rawBitmap.asImageBitmap(),
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .weight(1f)
                                .fillMaxWidth()
                                .padding(10.dp),
                            contentDescription = null,
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .padding(10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            // Show a loading indicator or placeholder
                            Image(
                                painter = painterResource(R.drawable.projectlogotransparencycircular), null)
                            CircularProgressIndicator()
                        }
                    }

                }
            }
            item {
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp), verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text(
                        textAlign = TextAlign.Start,
                        fontSize = 20.sp,
                        text = "Page $currentPage out of $totalPages",
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 10.dp)
                            .weight(1f)
                    )

                    if(currentPage != 1) {
                        IconButton(
                            modifier = Modifier.padding(end = 10.dp),
                            onClick = { viewModel.onPageButtonClicked(-1) }
                        ) {
                            Icon(
                                modifier = Modifier.fillMaxSize(),
                                imageVector = Icons.Filled.KeyboardDoubleArrowLeft,
                                contentDescription = "Page back",
                                tint = MaterialTheme.colorScheme.inverseSurface
                            )
                        }
                    }
                    if(currentPage < totalPages) {
                        IconButton(
                            onClick = { viewModel.onPageButtonClicked(1) }
                        ) {
                            Icon(
                                modifier = Modifier.fillMaxSize(),
                                imageVector = Icons.Filled.KeyboardDoubleArrowRight,
                                contentDescription = "Page next",
                                tint = MaterialTheme.colorScheme.inverseSurface
                            )
                        }
                    }
                }

            }
            item {
                Spacer(modifier = Modifier.padding(bottom = 10.dp))
            }
        }
    }

    if (isDialogDelete) {
        dialogDelete(
            onConfirm = {
                isDialogDelete = false
                viewModel.deleteRecipe(tempIdDelete)
            },
            onDismiss = { isDialogDelete = false }
        )
    }

    if(isLoading){
        viewModel.resetLoading()
        viewModel.loadQuery(state = State.Own)
    }
}


@Composable
fun dialogDelete(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Surface {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Are you sure you want to delete this recipe?")
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