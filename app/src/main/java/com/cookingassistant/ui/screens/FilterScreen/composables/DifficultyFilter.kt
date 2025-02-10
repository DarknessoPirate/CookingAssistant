package com.cookingassistant.ui.screens.FilterScreen.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.RadioButtonChecked
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cookingassistant.ui.screens.FilterScreen.FilterScreenViewModel

@Composable
fun DifficultyFilter(
    filterScreenViewModel: FilterScreenViewModel,
    items : List<String>,
    width: Float,
    color : Color = Color.Transparent
) {
    val selectedDifficulty by filterScreenViewModel.filterByDifficulty.collectAsState()
    var refresh by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier.fillMaxWidth(width)
            .background(color)
            .padding()
    ) {
        if (refresh) {
            for (difficulty in items) {
                Row(
                    modifier = Modifier
                        .padding(end = 10.dp, top = 2.dp, bottom = 2.dp, start = 10.dp)
                        .fillMaxWidth()
                        .background(color)
                        .wrapContentSize(Alignment.Center)
                        .clickable {
                            filterScreenViewModel.onFilterByDifficultyChange(difficulty)
                            refresh = false
                        },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = if (difficulty == selectedDifficulty) Icons.Outlined.RadioButtonChecked else Icons.Outlined.RadioButtonUnchecked,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.clickable {
                            filterScreenViewModel.onFilterByDifficultyChange(difficulty)
                            refresh = false
                        },
                    )
                    Text(
                        fontSize = 18.sp,
                        text = difficulty,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.weight(1f).padding(start = 5.dp)
                    )
                }
            }
        } else {
            for (difficulty in items) {
                Row(
                    modifier = Modifier
                        .padding(end = 10.dp, top = 2.dp, bottom = 2.dp, start = 10.dp)
                        .fillMaxWidth()
                        .background(color)
                        .wrapContentSize(Alignment.Center)
                        .clickable {
                            filterScreenViewModel.onFilterByDifficultyChange(difficulty)
                            refresh = true
                        },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = if (difficulty == selectedDifficulty) Icons.Outlined.RadioButtonChecked else Icons.Outlined.RadioButtonUnchecked,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.clickable {
                            filterScreenViewModel.onFilterByDifficultyChange(difficulty)
                            refresh = true
                        },
                    )
                    Text(
                        fontSize = 18.sp,
                        text = difficulty,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.weight(1f).padding(start = 5.dp)
                    )
                }
            }
        }
    }
}
