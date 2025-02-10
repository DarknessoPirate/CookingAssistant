package com.cookingassistant.ui.screens.recipescreen.composables

import android.graphics.Bitmap
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.createBitmap
import coil3.compose.AsyncImage
import com.cookingassistant.services.RecipeService
import com.cookingassistant.ui.screens.recipescreen.RecipeScreenViewModel
import com.cookingassistant.ui.screens.reviews.ReviewList
import com.cookingassistant.ui.screens.reviews.ReviewViewModel
import java.io.File

@Composable
fun RecipeScreenFrontPage(
    id: Int,
    name : String,
    img : Bitmap,
    desc : String,
    author : String,
    category : String,
    occasion : String,
    difficulty : String,
    size: Float = 0.9f,
    recipeScreenViewModel: RecipeScreenViewModel,
    destinationDir: File
) {

    val dialog by recipeScreenViewModel.showDialog.collectAsState()
    val ratingResponse by recipeScreenViewModel.ratingResponse.collectAsState()
    val isLoading by recipeScreenViewModel.isLoading.collectAsState()

    val sizeAnim1 by animateDpAsState(
        targetValue = if (dialog) 100.dp else 0.dp,
    )

    Box(
        Modifier
            .fillMaxHeight(size)
            .fillMaxWidth()
    ) {



        Column(
            Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .padding(10.dp),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .wrapContentWidth(Alignment.CenterHorizontally)
            ) {
                Text(
                    fontWeight = FontWeight.Bold,
                    text = name,
                    fontSize = 26.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.padding(vertical = 5.dp))
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.Center
            ) {
                item {
                    Text(
                        text = "Author: ${author}",
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(10.dp).fillMaxWidth())
                }
                item {
                    Text(
                        text = "Difficulty: ${difficulty}",
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(10.dp).fillMaxWidth())
                }
                item {
                    HorizontalDivider(
                        thickness = 5.dp,
                        modifier = Modifier.padding(vertical = 10.dp)
                    )
                }
                item {
                    HorizontalDivider(
                        thickness = 5.dp,
                        modifier = Modifier.padding(vertical = 10.dp)
                    )
                }
                item {
                    Text(
                        text = "Category: ${category}",
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center
                    )
                }
                item {
                    Text(
                        text = "Occasion: ${occasion}",
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center
                    )
                }
            }

            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 10.dp)
            ) {
                item {
                    if (img != createBitmap(1, 1)) {
                        Image(
                            modifier = Modifier
                                .height(250.dp)
                                .clip(RoundedCornerShape(10)),
                            bitmap = img.asImageBitmap(),
                            contentDescription = null,
                            contentScale = ContentScale.FillHeight
                        )
                    }
                }

                item {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(vertical = 10.dp)
                    ) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.tertiary)
                                .padding(5.dp)
                        ) {
                            Text(
                                fontSize = 20.sp,
                                textAlign = TextAlign.Justify,
                                text = desc,
                                color = MaterialTheme.colorScheme.onTertiary
                            )
                        }
                    }

                   Row {
                       Button(
                           onClick = { recipeScreenViewModel.downloadPdf(id, destinationDir) }
                       ) {
                           Text("Download Pdf")
                       }
                       Spacer(Modifier.width(10.dp))
                       if (isLoading) {
                           Row {
                               CircularProgressIndicator()
                           }
                       }
                   }

                }

            }


        }
        Box(
            Modifier.fillMaxWidth(0.8f)
                .height(sizeAnim1)
                .align(Alignment.TopCenter)
                .background(
                    when (ratingResponse) {
                        "File saved succesfully in downloads folder" -> {
                            MaterialTheme.colorScheme.tertiaryContainer
                        }

                        "Server error" -> {
                            MaterialTheme.colorScheme.errorContainer
                        }

                        "File can't be saved" -> {
                            MaterialTheme.colorScheme.errorContainer
                        }

                        else -> {
                            MaterialTheme.colorScheme.errorContainer
                        }
                    }
                )
        ) {
            Text(
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center),
                fontSize = 20.sp,
                text = ratingResponse,
                color = when (ratingResponse) {
                    "File saved succesfully in downloads folder" -> {
                        MaterialTheme.colorScheme.onTertiaryContainer
                    }

                    "Server error" -> {
                        MaterialTheme.colorScheme.onErrorContainer
                    }

                    "File can't be saved" -> {
                        MaterialTheme.colorScheme.onErrorContainer
                    }

                    else -> {
                        MaterialTheme.colorScheme.onErrorContainer
                    }
                }
            )
        }
    }
}