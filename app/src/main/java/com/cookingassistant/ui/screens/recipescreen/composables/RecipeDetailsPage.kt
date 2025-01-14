package com.cookingassistant.ui.screens.recipescreen.composables

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cookingassistant.data.ShoppingProduct
import com.cookingassistant.data.ShoppingProducts
import com.cookingassistant.data.ShoppingProducts.loadProducts
import com.cookingassistant.data.ShoppingProducts.saveProducts

@Composable
fun RecipeDetailsPage(
    calories : Int,
    totalTime : String,
    ingredients : List<String>,
    serves : Int,
    size: Float = 0.9f,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 20.sp,
    context: Context = LocalContext.current
) {
    var refresh by remember { mutableStateOf(false) }
    Column (
        Modifier
            .fillMaxHeight(size)
            .fillMaxWidth()
            .padding(10.dp)
        ,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Details",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 34.sp,
                fontWeight = FontWeight.Black
            )
            HorizontalDivider(Modifier.padding(bottom = 20.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.Center,
            ) {
                item {
                    Text(
                        "Calories:",
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = modifier,
                        fontSize = fontSize,
                        textAlign = TextAlign.Right,
                    )
                }
                item {
                    Text(
                        calories.toString(),
                        color = MaterialTheme.colorScheme.tertiary,
                        modifier = modifier,
                        fontSize = fontSize
                    )
                }
                item {
                    Text(
                        "Time to complete:",
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = modifier,
                        textAlign = TextAlign.Right,
                        fontSize = fontSize
                    )
                }
                item {
                    Text(
                        totalTime,
                        color = MaterialTheme.colorScheme.tertiary,
                        modifier = modifier,
                        fontSize = fontSize
                    )
                }
                item {
                    Text(
                        "Serves number:",
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = modifier,
                        textAlign = TextAlign.Right,
                        fontSize = fontSize
                    )
                }
                item {
                    Text(
                        text = serves.toString(),
                        color = MaterialTheme.colorScheme.tertiary,
                        modifier = modifier,
                        fontSize = fontSize
                    )
                }
            }
        }

        Row( modifier = Modifier
            .padding(bottom = 10.dp)
            .fillMaxWidth(), horizontalArrangement = Arrangement.Absolute.Center, verticalAlignment = Alignment.CenterVertically) {
            Text(text="Ingredients", color = MaterialTheme.colorScheme.onTertiaryContainer,
                fontSize = fontSize, textAlign = TextAlign.Center)
            IconButton(onClick = {
                for (product in ingredients) {
                    ShoppingProducts.addNotPresent(product)
                }
                refresh = !refresh
                ShoppingProducts.saveProducts(context)
                Toast.makeText(context, "Added all to shopping list", Toast.LENGTH_SHORT).show()
            }) {
                Icon(Icons.Filled.ShoppingCart, contentDescription = "add all ingredients to shopping list")
            }
            IconButton(onClick = {
                for (product in ingredients) {
                    ShoppingProducts.removePresent(product)
                }
                refresh = !refresh
                ShoppingProducts.saveProducts(context)
                Toast.makeText(context, "Removed all from shopping list", Toast.LENGTH_SHORT).show()
            }) {
                Icon(Icons.Outlined.ShoppingCart, contentDescription = "remove all ingredients from shopping list")
            }
        }


        LazyColumn(
            Modifier
                .wrapContentWidth(Alignment.CenterHorizontally)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if(refresh) {

            } else {

            }
            items(ingredients) { ingredient ->
                val inList = ShoppingProducts.isInList(ingredient)
                Row(modifier = Modifier
                    .clickable {
                        Toast.makeText(context, "Modified shopping list", Toast.LENGTH_SHORT).show()
                        ShoppingProducts.changePresense(ingredient)
                        saveProducts(context)
                        refresh = !refresh
                    }
                    .padding(vertical = 20.dp)
                    .fillMaxWidth()) {
                    Text(
                        text= if (inList) "◼ $ingredient" else "◻ $ingredient",
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}