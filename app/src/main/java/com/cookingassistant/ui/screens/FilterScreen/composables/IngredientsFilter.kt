package com.cookingassistant.ui.screens.FilterScreen.composables

import androidx.compose.animation.animateContentSize
import com.cookingassistant.ui.composables.ShoppingList.ShoppingListViewModel
import com.cookingassistant.ui.screens.FilterScreen.FilterScreenViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.AbsoluteCutCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.CheckBoxOutlineBlank
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cookingassistant.data.ShoppingProduct
import com.cookingassistant.data.ShoppingProducts


@Composable
fun IngredientsFilter(filterScreenViewModel: FilterScreenViewModel, modifier : Modifier) {
    val inputText by filterScreenViewModel.addIngredientText.collectAsState()
    val ingredients by filterScreenViewModel.selectedIngredients.collectAsState()
    val rollIngredients by filterScreenViewModel.unrollIngredients.collectAsState()
    val suggestedIngredient by filterScreenViewModel.suggestedIngredient.collectAsState()

    Column(
        modifier = modifier.background(Color.Transparent.copy(alpha = 0.2f))
        ,
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        TextField(
            value = inputText,
            onValueChange = {filterScreenViewModel.onAddIngredientTextChange(it)},
            maxLines = 1,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Filter by ingredients")},
            textStyle = TextStyle.Default.copy(fontSize = 18.sp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                filterScreenViewModel.onIngredientAdd(suggestedIngredient.trim())
            },
            ),
            supportingText = {
                Text(text = suggestedIngredient.lowercase(),
                textAlign = TextAlign.End,
                    fontSize = 16.sp,
                color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.fillMaxWidth()
            )}
        )
        Column (
            modifier = Modifier
                .padding(all = 2.dp)
                .wrapContentSize(Alignment.TopCenter)
                .animateContentSize()
            ,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if(rollIngredients && ingredients.size != 0) {
                Row (
                    modifier = Modifier.fillMaxWidth()
                        .background( MaterialTheme.colorScheme.surfaceContainer )
                        .wrapContentHeight(Alignment.CenterVertically)
                        .clickable {filterScreenViewModel.onIngredientRemove(ingredients[ingredients.size-1])}
                    ,
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        fontSize = 18.sp,
                        text=ingredients[ingredients.size-1],
                        modifier=Modifier.weight(1f).padding(start = 10.dp),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    IconButton(onClick = {filterScreenViewModel.onIngredientRemove(ingredients[ingredients.size-1])}
                    ) {
                        Icon(imageVector = Icons.Filled.Delete, contentDescription = "delete from list",
                            tint = Color.Red,
                        )
                    }
                }
                if(ingredients.size != 1) {
                    Button(onClick = {filterScreenViewModel.showIngredients()},
                        shape = AbsoluteCutCornerShape(0.dp)
                        , modifier = Modifier.fillMaxWidth()
                            .background(Color.Transparent)
                    ) {
                        Text(
                            modifier=Modifier.fillMaxWidth()
                            ,
                            text = "⬇ unroll extra " + "${ingredients.size-1} ingredients ⬇",
                            textAlign = TextAlign.Center,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            } else if (!rollIngredients && ingredients.size != 0) {
                for(ingredient in ingredients) {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceContainer)
                            .wrapContentHeight(Alignment.CenterVertically)
                            .clickable { filterScreenViewModel.onIngredientRemove(ingredient) },
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            fontSize = 18.sp,
                            text = ingredient,
                            modifier = Modifier.weight(1f).padding(start = 10.dp),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        IconButton(onClick = { filterScreenViewModel.onIngredientRemove(ingredient) }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "delete from list",
                                tint = Color.Red,
                            )
                        }
                    }
                }
                if (ingredients.size != 1) {
                    Button(onClick = { filterScreenViewModel.hideIngredients() },
                        modifier = Modifier.fillMaxWidth()
                            .background(Color.Transparent),
                        shape = AbsoluteCutCornerShape(0.dp)
                    ) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "⬆ roll ⬆",
                            textAlign = TextAlign.Center,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }
    }
}