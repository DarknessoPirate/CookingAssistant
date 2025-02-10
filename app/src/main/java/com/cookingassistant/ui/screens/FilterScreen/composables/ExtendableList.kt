package com.cookingassistant.ui.screens.FilterScreen.composables

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.RadioButtonChecked
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cookingassistant.ui.screens.FilterScreen.FilterScreenViewModel

@Composable
fun ExtendableList(
    title: String,
    items: List<String>,
    filterScreenViewModel: FilterScreenViewModel,
    filterType : String, //Occasion, Category
    fontColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onBackground,
    limit: Int = 2,
    color: Color = Color.Transparent
) {
    var isExtended by remember{ mutableStateOf(false) }
    var refresh by remember { mutableStateOf(false) }
    Column(modifier = Modifier.fillMaxWidth().background(color)) {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.primary,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 5.dp, start = 20.dp, end = 10.dp)
        )
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth().animateContentSize()
                .padding(start = 20.dp, end = 20.dp, bottom = 5.dp),
        ) {
            if(refresh) {
                for (i in 0..<items.size step 2) {
                    if (i > limit - 1 && !isExtended) { //
                        break
                    }

                    Row(modifier = Modifier.fillMaxWidth()
                        ,
                        verticalAlignment = Alignment.CenterVertically
                    )
                    {
                        Column (
                            modifier = Modifier
                                .padding(end = 10.dp, top = 2.dp, bottom = 2.dp)
                                .fillMaxWidth(0.5f)
                                .wrapContentSize(Alignment.Center)
                                .clickable {
                                    filterScreenViewModel.onFilterChange(
                                        filterType,
                                        items[i]
                                    )
                                    refresh = false
                                },
                        ) {
                            Row( verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (items[i] == filterScreenViewModel.getFilterValue(
                                            filterType
                                        )
                                    ) Icons.Outlined.RadioButtonChecked else Icons.Outlined.RadioButtonUnchecked,
                                    contentDescription = null,
                                    tint = fontColor,
                                    modifier = Modifier.clickable {
                                        filterScreenViewModel.onFilterChange(
                                            filterType,
                                            items[i]
                                        )
                                        refresh = false
                                    }
                                        .size(30.dp)
                                    ,
                                )
                                Text(
                                    fontSize = 18.sp,
                                    text = items[i],
                                    color = fontColor,
                                    modifier = Modifier.padding(start = 5.dp)
                                        .weight(1f)
                                )
                            }
                        }
                        if(i + 1 != items.size) {
                            Column (
                                modifier = Modifier
                                    .padding(end = 10.dp, top = 2.dp, bottom = 2.dp)
                                    .fillMaxWidth()
                                    .wrapContentSize(Alignment.Center)
                                    .clickable {
                                        filterScreenViewModel.onFilterChange(
                                            filterType,
                                            items[i+1]
                                        )
                                        refresh = false
                                    },
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (items[i+1] == filterScreenViewModel.getFilterValue(
                                                filterType
                                            )
                                        ) Icons.Outlined.RadioButtonChecked else Icons.Outlined.RadioButtonUnchecked,
                                        contentDescription = null,
                                        tint = fontColor,
                                        modifier = Modifier.clickable {
                                            filterScreenViewModel.onFilterChange(
                                                filterType,
                                                items[i+1]
                                            )
                                            refresh = false
                                        } .size(30.dp),
                                    )
                                    Text(
                                        fontSize = 18.sp,
                                        text = items[i+1],
                                        color = fontColor,
                                        modifier = Modifier.weight(1f).padding(start = 5.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                for (i in 0..<items.size step 2) {
                    if (i > limit - 1 && !isExtended) { //
                        break
                    }

                    Row( verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()

                    ) {
                        Column (
                            modifier = Modifier
                                .padding(end = 10.dp, top = 2.dp, bottom = 2.dp)
                                .fillMaxWidth(0.5f)
                                .wrapContentSize(Alignment.Center)
                                .clickable {
                                    filterScreenViewModel.onFilterChange(
                                        filterType,
                                        items[i]
                                    )
                                    refresh = true
                                },
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (items[i] == filterScreenViewModel.getFilterValue(
                                            filterType
                                        )
                                    ) Icons.Outlined.RadioButtonChecked else Icons.Outlined.RadioButtonUnchecked,
                                    contentDescription = null,
                                    tint = fontColor,
                                    modifier = Modifier.clickable {
                                        filterScreenViewModel.onFilterChange(
                                            filterType,
                                            items[i]
                                        )
                                        refresh = true
                                    } .size(30.dp),
                                )
                                Text(
                                    fontSize = 18.sp,
                                    text = items[i],
                                    color = fontColor,
                                    modifier = Modifier.weight(1f).padding(start = 5.dp)
                                )
                            }
                        }

                        if(i + 1 != items.size) {
                            Column (
                                modifier = Modifier
                                    .padding(end = 10.dp, top = 2.dp, bottom = 2.dp)
                                    .fillMaxWidth()
                                    .wrapContentSize(Alignment.Center)
                                    .clickable {
                                        filterScreenViewModel.onFilterChange(
                                            filterType,
                                            items[i+1]
                                        )
                                        refresh = true
                                    },
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (items[i+1] == filterScreenViewModel.getFilterValue(
                                                filterType
                                            )
                                        ) Icons.Outlined.RadioButtonChecked else Icons.Outlined.RadioButtonUnchecked,
                                        contentDescription = null,
                                        tint = fontColor,
                                        modifier = Modifier.clickable {
                                            filterScreenViewModel.onFilterChange(
                                                filterType,
                                                items[i+1]
                                            )
                                            refresh = true
                                        } .size(30.dp),
                                    )
                                    Text(
                                        fontSize = 18.sp,
                                        text = items[i+1],
                                        color = fontColor,
                                        modifier = Modifier.weight(1f).padding(start = 5.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

        }
        ClickableText(
            text = if(!isExtended) AnnotatedString("Show more") else AnnotatedString("Show less"),
            onClick = {
                isExtended = !isExtended
            },
            modifier = Modifier.padding(start = 20.dp, bottom = 10.dp),
            style = TextStyle.Default.copy(color = MaterialTheme.colorScheme.primary, fontSize = 16.sp)
        )
    }
}