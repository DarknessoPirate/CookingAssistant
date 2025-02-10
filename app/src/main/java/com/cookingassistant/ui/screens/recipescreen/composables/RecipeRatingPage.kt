package com.cookingassistant.ui.screens.recipescreen.composables

import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarRate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cookingassistant.ui.screens.recipescreen.RecipeScreenViewModel

@Composable
fun RecipeRatingPage(
    recipeScreenViewModel: RecipeScreenViewModel,
    size : Float = 0.9f
) {
    val ratingSelection by recipeScreenViewModel.userRating.collectAsState()
    val userComment by recipeScreenViewModel.userComment.collectAsState()
    val maxCommentLength = 150
    val context = LocalContext.current
    val ratingRespone by recipeScreenViewModel.ratingResponse.collectAsState()
    val review by recipeScreenViewModel.reviewGetDto.collectAsState()
    val dialog by recipeScreenViewModel.showDialog.collectAsState()

    val sizeAnim1 by animateDpAsState(
        targetValue = if (dialog) 100.dp else 0.dp,
    )
    Box(
        Modifier
            .fillMaxHeight(size)
            .fillMaxWidth()
    ) {
        Box(
            Modifier.fillMaxWidth(0.8f)
                .height(sizeAnim1)
                .align(Alignment. TopCenter)
                .background(
                    when(ratingRespone) {
                        "Rating successfully changed" -> {MaterialTheme.colorScheme.tertiaryContainer}
                        "Rating successfully submitted" -> {MaterialTheme.colorScheme.tertiaryContainer}
                        "You can't review your own recipe" -> {MaterialTheme.colorScheme.errorContainer}
                        "You can't review recipe twice" -> {MaterialTheme.colorScheme.errorContainer}
                        "System failed to post your rating. We are sorry for inconvenience!" -> {MaterialTheme.colorScheme.errorContainer}
                        else -> {MaterialTheme.colorScheme.errorContainer}
                    }
                )
        ) {
            Text(
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center),
                fontSize = 20.sp,
                text = ratingRespone,
                color = when(ratingRespone) {
                    "Rating successfully changed" -> {MaterialTheme.colorScheme.onTertiaryContainer}
                    "Rating successfully submitted" -> {MaterialTheme.colorScheme.onTertiaryContainer}
                    "System failed to post your rating. We are sorry for inconvenience!" -> {MaterialTheme.colorScheme.onErrorContainer}
                    else -> {MaterialTheme.colorScheme.onErrorContainer}
                }
            )
        }

        Column (
            Modifier
                .align(Alignment.Center)
                .padding(top = 40.dp)
                .wrapContentWidth(Alignment.CenterHorizontally),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("My recipe review:", color = MaterialTheme.colorScheme.onBackground, fontSize = 20.sp)
            Row(modifier = Modifier.padding(bottom = 10.dp))
            {
                for (i in 1..ratingSelection) {
                    IconButton(
                        onClick = {if(ratingSelection != i) {recipeScreenViewModel.onUserRatingChanged(i)} else{recipeScreenViewModel.onUserRatingChanged(0)} },
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(MaterialTheme.colorScheme.inverseSurface.copy(alpha = 0.5f))

                    ) {
                        Icon(imageVector = Icons.Filled.Star, contentDescription = "Rating star", tint = Color.Yellow,
                        )
                    }
                }
                for(i in ratingSelection+1..5) {
                    IconButton(
                        onClick = {recipeScreenViewModel.onUserRatingChanged(i)},
                    ) {
                        Icon(imageVector = Icons.Outlined.StarRate, contentDescription = "Rating star", tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            TextField(
                value = userComment,
                onValueChange = {if (it.length <= maxCommentLength) recipeScreenViewModel.onUserCommentChanged(it)},
                maxLines = 3,
                modifier = Modifier.fillMaxWidth(0.8f),
                label = { Text("Additional comment") },
                supportingText = {
                    Text(
                        text = "${userComment.length} / ${maxCommentLength}",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.End,
                    )
                }
            )
            Button (
            onClick = {
                        recipeScreenViewModel.onRatingSubmitted(ratingSelection, userComment)
                },
                enabled = ratingSelection != 0 && userComment != "",
                modifier = Modifier
                .padding(top = 20.dp)
                .fillMaxWidth(0.8f)
            ) {
                Text("Submit review", fontSize = 20.sp)
            }
            Button (
                onClick = {
                    recipeScreenViewModel.onSeeReviews()
                },
                modifier = Modifier
                    .padding(top = 20.dp)
                    .fillMaxWidth(0.8f)
            ) {
                Text("See reviews", fontSize = 20.sp)
            }
            if(review != null) {
                Button (
                    onClick = {
                        recipeScreenViewModel.onDeleteReview()
                    },
                    modifier = Modifier
                        .padding(top = 40.dp)
                        .fillMaxWidth(0.8f),
                    colors = ButtonDefaults.buttonColors().copy(containerColor = MaterialTheme.colorScheme.errorContainer, contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete my review", fontSize = 20.sp, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}