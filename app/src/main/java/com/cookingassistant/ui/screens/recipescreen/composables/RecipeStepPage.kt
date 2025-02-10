package com.cookingassistant.ui.screens.recipescreen.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RecipeStepPage(
    stepNumber : Int,
    stepText : String,
    size : Float = 0.9f,
) {
    Box(
        Modifier
            .fillMaxHeight(size)
            .fillMaxWidth()
            .padding(10.dp)
        ,
    ) {
        LazyColumn(
            modifier = Modifier.align(Alignment.TopCenter),
        ) {
            item {
                Text(
                    text = "Step " + stepNumber.toString(),
                    fontSize = 50.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                )
            }
        }
        LazyColumn(
            modifier = Modifier.align(Alignment.Center)
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(10.dp)
                .fillMaxHeight(0.7f)
                .fillMaxWidth(0.95f)
        ) {
            item {
                Text(
                    textAlign = TextAlign.Justify,
                    text=stepText,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontSize = 20.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}