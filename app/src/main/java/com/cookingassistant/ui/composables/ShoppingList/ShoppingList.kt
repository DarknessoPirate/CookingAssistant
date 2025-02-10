package com.cookingassistant.ui.composables.ShoppingList

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.CheckBoxOutlineBlank
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cookingassistant.data.ShoppingProduct
import com.cookingassistant.data.ShoppingProducts


@Composable
fun ShoppingList(shoppingListViewModel: ShoppingListViewModel = viewModel()) {
    val inputText by shoppingListViewModel.inputText.collectAsState()
    val products by shoppingListViewModel.productList.collectAsState()
    val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surfaceContainer).padding(bottom = 40.dp)
        ,
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        Row(
            modifier = Modifier.padding(top= 60.dp, bottom = 20.dp)

        ) {
            TextField(
                value = inputText,
                onValueChange = {shoppingListViewModel.onInputTextChanged(it)},
                maxLines = 1,
                modifier = Modifier.fillMaxWidth(0.8f),
                label = { Text("Write product here")},
                textStyle = TextStyle.Default.copy(fontSize = 18.sp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    shoppingListViewModel.onProductListAdd(inputText)
                    ShoppingProducts.saveProducts(context)
                }
                )

            )
            IconButton(
                onClick = {shoppingListViewModel.onProductListAdd(inputText)
                    ShoppingProducts.saveProducts(context)
                          },
            ) {
                Icon(Icons.Filled.AddCircle, tint = Color.Green, contentDescription = "add product",
                    modifier = Modifier.fillMaxSize())
            }
        }


        LazyColumn (
            modifier = Modifier.weight(1f)
                .padding(all = 10.dp)
                .wrapContentSize(Alignment.TopCenter)
            ,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(products) { product ->
                Row (
                    modifier = Modifier.fillMaxWidth()
                        .background( if(product.isChecked) Color.Green.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surfaceContainer )
                        .wrapContentHeight(Alignment.CenterVertically)
                        .clickable { shoppingListViewModel.onProductChecked(product)
                            ShoppingProducts.saveProducts(context)
                                   }
                    ,
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if(!product.isChecked) {
                        IconButton(onClick = {shoppingListViewModel.onProductChecked(product)
                            ShoppingProducts.saveProducts(context)
                        }) {
                            Icon(imageVector = Icons.Outlined.CheckBoxOutlineBlank, contentDescription = "mark as done",
                                tint = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        Text(
                            fontSize = 22.sp,
                            text=product.text,
                            modifier=Modifier.weight(1f),
                            color = MaterialTheme.colorScheme.onBackground
                        )

                    } else {
                        IconButton(onClick = {shoppingListViewModel.onProductChecked(product)
                            ShoppingProducts.saveProducts(context)
                        }) {
                            Icon(imageVector = Icons.Filled.CheckBox, contentDescription = "unmark as done",
                                tint = Color.Yellow,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        Text(
                            fontSize = 22.sp,
                            textDecoration = TextDecoration.LineThrough,
                            text=product.text,
                            modifier=Modifier.weight(1f),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        IconButton(onClick = {shoppingListViewModel.onProductListRemove(product)
                            ShoppingProducts.saveProducts(context)
                        }) {
                            Icon(imageVector = Icons.Filled.Delete, contentDescription = "delete from list",
                                tint = Color.Red,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
        }
    }

}