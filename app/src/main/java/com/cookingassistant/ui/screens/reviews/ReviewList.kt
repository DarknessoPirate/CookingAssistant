package com.cookingassistant.ui.screens.reviews

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.cookingassistant.data.DTO.ReviewGetDTO

@Composable
fun ReviewList(
    reviewViewModel: ReviewViewModel,
) {
    val recipeId by reviewViewModel.recipeId.collectAsState()
    val reviews by reviewViewModel.reviews.collectAsState()
    val userReview by reviewViewModel.currentUserReview.collectAsState()
    reviewViewModel.LoadReviewsImage(reviews)
    val images by reviewViewModel.reviewImages.collectAsState()
    val dialog by reviewViewModel.showDialog.collectAsState()
    val loadingResult by reviewViewModel.loadingResult.collectAsState()

    val sizeAnim1 by animateDpAsState(
        targetValue = if (dialog) 100.dp else 0.dp,
    )


    Box (Modifier.fillMaxSize()){
        LazyColumn(
            Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(top = 60.dp),
        ) {
            if(reviews == null) {
                return@LazyColumn
            }

            items(reviews!!) { review ->
                Review(review, userReview,images, reviewViewModel, recipeId)
            }

        }
        Box(
            Modifier.fillMaxWidth(0.8f)
                .height(sizeAnim1)
                .background(
                    when(loadingResult) {
                        "Review deleted!" -> {MaterialTheme.colorScheme.tertiaryContainer}
                        "Rating successfully submitted" -> {MaterialTheme.colorScheme.tertiaryContainer}
                        "Delete failed: no access to server" -> {MaterialTheme.colorScheme.errorContainer}
                        "Modify failed: no access to server" -> {MaterialTheme.colorScheme.errorContainer}
                        "Can't delete review" -> {MaterialTheme.colorScheme.errorContainer}
                        "Can't submit review" -> {MaterialTheme.colorScheme.errorContainer}
                        else -> {MaterialTheme.colorScheme.errorContainer}
                    }
                ).align(Alignment.Center)
        ) {
            Text(
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center),
                fontSize = 20.sp,
                text = loadingResult,
                color = when(loadingResult) {
                    "Review deleted!" -> {MaterialTheme.colorScheme.onTertiaryContainer}
                    "Rating successfully submitted" -> {MaterialTheme.colorScheme.onTertiaryContainer}
                    "Delete failed: no access to server" -> {MaterialTheme.colorScheme.onErrorContainer}
                    "Modify failed: no access to server" -> {MaterialTheme.colorScheme.onErrorContainer}
                    "Can't delete review" -> {MaterialTheme.colorScheme.onErrorContainer}
                    "Can't submit review" -> {MaterialTheme.colorScheme.onErrorContainer}
                    else -> {MaterialTheme.colorScheme.onErrorContainer}
                }
            )
        }

    }

}