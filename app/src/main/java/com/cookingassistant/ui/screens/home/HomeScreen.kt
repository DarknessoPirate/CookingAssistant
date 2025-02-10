package com.cookingassistant.ui.screens.home

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Environment
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme


import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.cookingassistant.data.DTO.RecipePostDTO
import com.cookingassistant.data.objects.TextFormatting
import com.cookingassistant.ui.composables.RatingToStars
import com.cookingassistant.ui.composables.topappbar.TopAppBar
import com.cookingassistant.util.ImageConverter
import kotlin.math.roundToInt

@Composable
fun HomeScreen(homeScreenViewModel: HomeScreenViewModel) {
    LaunchedEffect (Unit){
        homeScreenViewModel.fetchDailyRecipe()
    }

    val recipeImage by homeScreenViewModel.recipeImage.collectAsState()
    val recipe by homeScreenViewModel.recipe.collectAsState()
    val isLoading by homeScreenViewModel.isLoading.collectAsState()

        Column(modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                color = MaterialTheme.colorScheme.onTertiaryContainer,
                text = "Recipe for Today",
                fontWeight = FontWeight(500),
                fontSize = 40.sp,
                )
            Spacer(Modifier.height(20.dp))
            if (isLoading) {
                Row (Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.Top){
                    CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
                }
            }
            else {
                Column(
                    Modifier.padding(10.dp)
                        .height(500.dp)
                        .clickable { homeScreenViewModel.onRecipeClick(recipe.id) }
                        .background(MaterialTheme.colorScheme.background)
                        .wrapContentHeight(Alignment.CenterVertically)
                        .padding(bottom = 10.dp)
                    ,
                    horizontalAlignment = Alignment.CenterHorizontally

                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(all = 10.dp)
                            .height(100.dp)
                    ) {
                        Text(
                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                            text = recipe.name,
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
                                text = "Difficulty: ${recipe.difficultyName}",
                                modifier = Modifier.verticalScroll(rememberScrollState()),
                                fontSize = 15.sp
                            )
                            RatingToStars(recipe.ratings.roundToInt(),
                                modifier = Modifier.size(16.dp).shadow(elevation = 4.dp),
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
                                text = "Vote count: ${recipe.voteCount}",
                                modifier = Modifier.verticalScroll(rememberScrollState()),
                                fontSize = 15.sp
                            )
                        }
                    }
                    Text(
                        color = MaterialTheme.colorScheme.onBackground,
                        text = "Category: ${recipe.categoryName}, Occasion: ${recipe.occasionName}",
                        fontSize = 15.sp,
                    )
                    Text(
                        color = MaterialTheme.colorScheme.onBackground,
                        text = "Calories: ${recipe.caloricity}, Completion time: ${TextFormatting.formatTime(recipe.timeInMinutes)}",
                        fontSize = 15.sp
                    )
                    if (recipeImage != null) {
                        Image(
                            contentScale = ContentScale.Crop,
                            bitmap = recipeImage!!.asImageBitmap(),
                            modifier = Modifier.clip(RoundedCornerShape(10.dp)).weight(1f).fillMaxWidth().padding(10.dp),
                            contentDescription = null,
                        )
                    }

                }
            }

        }
    }


