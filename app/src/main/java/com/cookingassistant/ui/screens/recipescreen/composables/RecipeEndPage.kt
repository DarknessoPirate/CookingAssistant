package com.cookingassistant.ui.screens.recipescreen.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddReaction
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.StarRate
import androidx.compose.material.icons.rounded.Celebration
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastCbrt
import com.cookingassistant.R
import com.cookingassistant.ui.screens.recipescreen.RecipeScreenViewModel

@Composable
fun RecipeEndPage(
     size : Float = 0.9f
) {
    Box(
        Modifier
            .fillMaxHeight(size)
            .fillMaxWidth()
    ) {
       Box(Modifier
           .align(Alignment.Center)
           .fillMaxWidth()
           .fillMaxHeight(0.9f)) {
           Image(
               painter = painterResource(R.drawable.projectlogo2kcircular),
               contentDescription = "Cooking assistant app",
               modifier = Modifier.fillMaxWidth()
           )
           Icon(imageVector = Icons.Filled.Check,
               contentDescription = "Bravo",
               modifier = Modifier
                   .fillMaxSize(1f)
                   .offset(y = -40.dp)
               ,
               tint = MaterialTheme.colorScheme.tertiary
           )

           Text(text="\nDONE!",
               color= MaterialTheme.colorScheme.tertiary,
               modifier = Modifier.align(Alignment.BottomCenter),
               textAlign = TextAlign.Center,
               fontSize = 40.sp,
               fontWeight = FontWeight.Bold
           )
       }
    }
}