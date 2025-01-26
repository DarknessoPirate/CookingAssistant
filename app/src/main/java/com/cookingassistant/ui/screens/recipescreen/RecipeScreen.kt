package com.cookingassistant.ui.screens.recipescreen

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.KeyboardDoubleArrowLeft
import androidx.compose.material.icons.filled.KeyboardDoubleArrowRight
import androidx.compose.material.icons.filled.WavingHand
import androidx.compose.material.icons.outlined.AddComment
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.WavingHand
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.cookingassistant.data.objects.TextFormatting
import com.cookingassistant.ui.screens.recipescreen.composables.RecipeDetailsPage
import com.cookingassistant.ui.screens.recipescreen.composables.RecipeEndPage
import com.cookingassistant.ui.screens.recipescreen.composables.RecipeRatingPage
import com.cookingassistant.ui.screens.recipescreen.composables.RecipeScreenFrontPage
import com.cookingassistant.ui.screens.recipescreen.composables.RecipeStepPage
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun RecipeScreen(
    recipeScreenViewModel : RecipeScreenViewModel,
    destinationDir: File,
) {
    val recipe by recipeScreenViewModel.recipe.collectAsState()
    val img by recipeScreenViewModel.recipeImg.collectAsState()
    val favorite by recipeScreenViewModel.markedFavorite.collectAsState()
    val userReview by recipeScreenViewModel.reviewGetDto.collectAsState()
    val stepsCount by recipeScreenViewModel.stepsCount.collectAsState()
    val currentPage by recipeScreenViewModel.currentPage.collectAsState()
    val touchlessControlls by recipeScreenViewModel.touchlessControls.collectAsState()

    // each step on separate page + 1 frontpage + 1 details
    val pagesCount by remember { mutableStateOf(2) }
    var offsetX by remember { mutableStateOf(0f) }
    var savedPage by remember { mutableStateOf(0) }

    val sizeAnim1 by animateFloatAsState(
        targetValue = if (currentPage % 2 == 0) 0.94f else 0f
    )
    val sizeAnim2 by animateFloatAsState(
        targetValue = if (currentPage % 2 == 1) 0.94f else 0f
    )

    //WOW
    val context = LocalContext.current
    if(touchlessControlls) {
        val sensorManager = remember { context.getSystemService(Context.SENSOR_SERVICE) as SensorManager }
        val gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION)
        val scope = rememberCoroutineScope()
        val threshold = 25f
        var lastUpdate by remember { mutableStateOf(System.currentTimeMillis()) }
        DisposableEffect(Unit) {
            val listener = object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent) {
                    val now = System.currentTimeMillis()
                    if (now - lastUpdate < 800) return
                    lastUpdate = now

                    val rotationY = event.values[2]
                    when {
                        rotationY > threshold -> {
                            scope.launch {
                                if(currentPage != 0) {
                                    if (currentPage == (pagesCount + stepsCount) + 1) {
                                        recipeScreenViewModel.setPage(savedPage)
                                    } else {
                                        recipeScreenViewModel.setPage(currentPage - 1)
                                    }
                                }
                            }
                        }
                        rotationY < -threshold -> {
                            scope.launch {
                                if (currentPage < (pagesCount + stepsCount)) {
                                    recipeScreenViewModel.setPage(currentPage + 1)
                                }
                            }
                        }
                    }
                }

                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
            }

            sensorManager.registerListener(listener, gyroscope, SensorManager.SENSOR_DELAY_UI)
            onDispose {
                sensorManager.unregisterListener(listener)
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(vertical = 40.dp, horizontal = 5.dp)
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onHorizontalDrag = {change, dragAmount ->
                        change.consume()
                        offsetX = dragAmount
                    },
                    onDragStart = {offsetX = 0f},
                    onDragEnd = {
                        if (offsetX < -5f && currentPage < (pagesCount+stepsCount))
                            recipeScreenViewModel.setPage(currentPage+1)
                        else if(offsetX > 5f && currentPage != 0) {
                            if(currentPage == (pagesCount+stepsCount) + 1 ) {
                                recipeScreenViewModel.setPage(savedPage)
                            } else {
                                recipeScreenViewModel.setPage(currentPage-1)
                            }
                        }
                    }
                )
            }
    ) {
        Column (Modifier
            .fillMaxHeight(if(currentPage % 2 == 0) sizeAnim1 else sizeAnim2)
            .align(Alignment.Center)
        ){
            Spacer(Modifier.fillMaxWidth().height(1.dp).padding(top=35.dp))
            when(currentPage) {
                0 -> {
                    RecipeScreenFrontPage(recipe.id, recipe.name,img,recipe.description,recipe.authorName,recipe.categoryName,recipe.occasionName, recipe.difficultyName ?: "not known", recipeScreenViewModel = recipeScreenViewModel, destinationDir = destinationDir)
                }
                1 -> {
                    RecipeDetailsPage(recipe.caloricity, TextFormatting.formatTime(recipe.timeInMinutes),
                        TextFormatting.formatIngredients(recipe.ingredients), recipe.serves, modifier = Modifier.padding(vertical = 8.dp, horizontal = 5.dp))
                }
                in 2..<(pagesCount+stepsCount) -> {
                    RecipeStepPage(stepNumber = currentPage-1,
                        stepText = recipe.steps?.get(currentPage-2)?.description ?: "")
                }
                (pagesCount+stepsCount) -> {
                    RecipeEndPage()
                }
                (pagesCount+stepsCount)+1 -> {
                    RecipeRatingPage(recipeScreenViewModel)
                }
            }
        }
        Row(Modifier.align(Alignment.TopStart)) {
            IconButton(onClick = {recipeScreenViewModel.switchTouchless()
                if(touchlessControlls)
                    Toast.makeText(context, "Disabled touch-less controls", Toast.LENGTH_LONG).show()
                else
                    Toast.makeText(context, "Enabled touch-less controls", Toast.LENGTH_LONG).show()
            }) {
                if(touchlessControlls) {
                    Icon(imageVector = Icons.Filled.WavingHand, contentDescription = "disable touchless controls")
                } else {
                    Icon(imageVector = Icons.Outlined.WavingHand, contentDescription = "enable touchless controls")
                }
            }
        }

        Row ( Modifier
            .align(Alignment.TopEnd)
        ) {

            IconButton(onClick = {recipeScreenViewModel.onFavoriteChanged(!favorite)}
            ) {
                if(favorite) {
                    Icon(imageVector = Icons.Filled.Favorite, contentDescription = "mark favorite", tint = Color.Red)
                } else {
                    Icon(imageVector = Icons.Outlined.FavoriteBorder, contentDescription = "remove form favorites")
                }
            }

            if(currentPage != (pagesCount+stepsCount) + 1) {
                IconButton(
                    onClick = {
                        recipeScreenViewModel.onUserEnterRecipeRatingPage()
                        savedPage = (currentPage)
                        recipeScreenViewModel.setPage((pagesCount+stepsCount) + 1)
                    },
                ) {
                    Icon(
                        Icons.Outlined.AddComment,
                        contentDescription = "rate recipe"
                    )
                }
            }

        }

        Row (
            modifier = Modifier.align(Alignment.BottomCenter)
                .fillMaxHeight(0.1f)
                .fillMaxWidth()
            ,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            if(currentPage != 0) {
                IconButton(
                    onClick = {
                        if(currentPage == (pagesCount+stepsCount) + 1) {
                            recipeScreenViewModel.setPage(savedPage)
                        } else {
                            recipeScreenViewModel.setPage(currentPage - 1)
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.KeyboardDoubleArrowLeft
                        ,null,
                        Modifier.size(50.dp),
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
            if(currentPage >= 0 && currentPage < (pagesCount+stepsCount)) {
                IconButton(
                    onClick = {recipeScreenViewModel.setPage(currentPage + 1)}
                ) {
                    Icon(
                        imageVector = Icons.Filled.KeyboardDoubleArrowRight
                        ,null,
                        Modifier.size(50.dp),
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

        }
    }
}