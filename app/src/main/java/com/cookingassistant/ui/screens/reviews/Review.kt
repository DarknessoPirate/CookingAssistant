package com.cookingassistant.ui.screens.reviews
import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.StarRate
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cookingassistant.data.DTO.ReviewGetDTO
import java.time.LocalDate
import java.util.Date

@Composable
fun Review(reviewGetDTO: ReviewGetDTO, userReviewDTO: ReviewGetDTO? ,images: MutableMap<Int, Bitmap?>, reviewViewModel: ReviewViewModel, recipeId: Int) {
    val isLoading = reviewViewModel.isLoading.collectAsState()
    val userRating = reviewViewModel.userRating.collectAsState()
    val userComment = reviewViewModel.userComment.collectAsState()
    val isDialogVisible = reviewViewModel.isDialogVisible.collectAsState()

    Box(Modifier.padding(10.dp)
        .shadow(5.dp, RoundedCornerShape(20.dp))
        .background(MaterialTheme.colorScheme.surfaceContainerLow, RoundedCornerShape(20.dp)) // Tło kafelka
        .padding(4.dp)) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val image = images.getOrDefault(reviewGetDTO.id, null)
                    if (image != null) {
                        Image(
                            bitmap = image.asImageBitmap(),
                            contentDescription = "profile-pic",
                            modifier = Modifier.width(48.dp).height(48.dp).clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }
                    else {
                        Icon(
                            imageVector = Icons.Filled.AccountCircle,
                            tint = Color.Gray,
                            contentDescription = "profile-pic",
                            modifier = Modifier.width(48.dp).height(48.dp),
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    Text(reviewGetDTO.reviewAuthor.toString(), fontSize = 18.sp, color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.width(90.dp))
                }

                Row {
                    for (i in 1..reviewGetDTO.value) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            tint = Color.Yellow,
                            contentDescription = "star",
                            modifier = Modifier
                                .padding(2.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.inverseSurface.copy(0.5f))
                                .size(24.dp)
                                .padding(4.dp)

                        )
                    }
                    for (j in reviewGetDTO.value + 1..5) {
                        Icon(
                            imageVector = Icons.Outlined.Star,
                            contentDescription = "star",
                            modifier = Modifier
                                .padding(2.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.inverseSurface.copy(0.5f))
                                .size(24.dp)
                                .padding(4.dp)

                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            Box(
                Modifier
                    .fillMaxWidth()
                    .border(2.dp, Color.LightGray, RoundedCornerShape(20.dp))
                    .padding(12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = reviewGetDTO.description.toString(),
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Spacer(Modifier.height(5.dp))
            Row (
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Reviewed at:",
                        Modifier.padding(start = 8.dp),
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "${reviewGetDTO.dateCreated.substring(0,10)} ${reviewGetDTO.dateCreated.substring(11,19)}",
                        Modifier.padding(start = 8.dp),
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                if (reviewGetDTO.dateModified != null) {
                    Column {
                        Text(
                            text = "Modified at:",
                            Modifier.padding(start = 8.dp),
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "${reviewGetDTO.dateModified!!.substring(0,10)} ${reviewGetDTO.dateModified!!.substring(11,19)}",
                            Modifier.padding(start = 8.dp),
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                }

            }

            Spacer(Modifier.height(5.dp))
            if (userReviewDTO != null) {
                if (reviewGetDTO.id == userReviewDTO.id)
                    Row (Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween){
                        Button(onClick = {reviewViewModel.DeleteReview(recipeId)}, Modifier.padding(horizontal = 5.dp).weight(0.9f)) {
                            Text("Delete", fontSize = 18.sp)
                        }
                        if (isLoading.value) {
                            CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
                        }

                        Button(onClick = {reviewViewModel.showResultDialog()}, Modifier.padding(horizontal = 5.dp).weight(0.9f)) {
                            Text("Modify", fontSize = 18.sp)
                        }

                        if (isDialogVisible.value) {

                            AlertDialog(
                                modifier = Modifier.background(color = Color.Transparent),
                                shape = RoundedCornerShape(20.dp),
                                onDismissRequest = { reviewViewModel.hideResultDialog() },
                                title = { Text("Modify your review") },
                                confirmButton = {
                                        Column (
                                            Modifier
                                                .padding(top = 5.dp)
                                                .wrapContentWidth(Alignment.CenterHorizontally),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            Row(modifier = Modifier.padding(bottom = 10.dp))
                                            {
                                                for (i in 1..userRating.value) {
                                                    IconButton(
                                                        onClick = {if(userRating.value != i) {reviewViewModel.onUserRatingChanged(i)} else{reviewViewModel.onUserRatingChanged(0)} },
                                                        modifier = Modifier
                                                            .clip(RoundedCornerShape(50))
                                                            .background(MaterialTheme.colorScheme.inverseSurface.copy(alpha = 0.5f))
                                                    ) {
                                                        Icon(imageVector = Icons.Filled.Star, contentDescription = "Rating star", tint = Color.Yellow,
                                                        )
                                                    }
                                                }
                                                for(i in userRating.value+1..5) {
                                                    IconButton(
                                                        onClick = {reviewViewModel.onUserRatingChanged(i)},
                                                    ) {
                                                        Icon(imageVector = Icons.Outlined.StarRate, contentDescription = "Rating star", tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                                                        )
                                                    }
                                                }
                                            }

                                            TextField(
                                                value = userComment.value,
                                                onValueChange = {if (it.length <= 150) reviewViewModel.onUserCommentChanged(it)},
                                                maxLines = 4,
                                                modifier = Modifier.fillMaxWidth(0.8f),
                                                label = { Text("Additional comment") },
                                                supportingText = {
                                                    Text(
                                                        text = "${userComment.value.length} / ${150}",
                                                        modifier = Modifier.fillMaxWidth(),
                                                        textAlign = TextAlign.End,
                                                    )
                                                }
                                            )
                                            Button (
                                                onClick = {
                                                    reviewViewModel.ModifyReview(recipeId,userRating.value,userComment.value)
                                                    reviewViewModel.hideResultDialog()
                                                },
                                                enabled = userRating.value != 0,
                                                modifier = Modifier
                                                    .padding(top = 20.dp)
                                                    .fillMaxWidth(0.8f)
                                            ) {
                                                Text("Submit rating", fontSize = 20.sp)
                                            }
                                        }
                                    }

                            )
                        }
                    }
            }
        }
    }

}
