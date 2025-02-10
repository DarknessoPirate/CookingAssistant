package com.cookingassistant.ui.composables

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RatingToStars(rating : Int,
                  modifier : Modifier = Modifier,
                  color : Color,
                  content: @Composable() () -> Unit
) {
    var i = 0
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {

        content()
        if(rating == 0 ) {
            Text(" not rated yet.",
                color = color,
                fontSize = 15.sp)
        } else {
            for (i in 0..<rating) {
                Icon(
                    Icons.Filled.Star,
                    contentDescription = "star",
                    modifier = modifier.padding(start = 3.dp),
                    tint = color
                )
            }
        }
    }
}