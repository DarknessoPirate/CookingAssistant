package com.cookingassistant.ui.screens.FilterScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.AbsoluteCutCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cookingassistant.ui.screens.FilterScreen.composables.DifficultyFilter
import com.cookingassistant.ui.screens.FilterScreen.composables.DropdownCombo
import com.cookingassistant.ui.screens.FilterScreen.composables.ExtendableList
import com.cookingassistant.ui.screens.FilterScreen.composables.IngredientsFilter

@Preview
@Composable
fun FilterScreen(
    filterScreenViewModel: FilterScreenViewModel = viewModel(),
) {
    val searchText by filterScreenViewModel.searchQuery.collectAsState()
    val categories by filterScreenViewModel.allCategories.collectAsState()
    val difficulties by filterScreenViewModel.allDifficulties.collectAsState()
    val occasions by filterScreenViewModel.allOccasions.collectAsState()

    Column(Modifier.fillMaxSize()
        .background(MaterialTheme.colorScheme.surfaceContainer)
        .padding(vertical = 10.dp)
        .verticalScroll(rememberScrollState())

    ) {
        Spacer(modifier = Modifier.fillMaxWidth().padding(top = 40.dp))
        Column(modifier = Modifier.fillMaxWidth()
            .padding(vertical = 10.dp, horizontal = 20.dp)
        ,
            ) {
            Text(
                text = "Looking for something particular?",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 5.dp, end = 10.dp, bottom = 3.dp)
            )
            TextField(
                modifier = Modifier.fillMaxWidth(1.0f),
                value = searchText ?: "",
                label = { Text(text = "Search phrase") },
                singleLine = true,
                onValueChange = { filterScreenViewModel.onSearchQueryChange(it) },
            )
        }
        Column(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp,horizontal = 20.dp)
            ,
                horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Which ingredients would You like to use?",
                color =  MaterialTheme.colorScheme.primary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 5.dp, end = 10.dp, bottom = 3.dp)
            )
            IngredientsFilter(filterScreenViewModel, Modifier.fillMaxWidth(1f))
        }
        ExtendableList(
            title = "Filter by occasion",
            occasions,
            filterScreenViewModel,
            filterType = "Occasion",
            limit = 4
        )
        ExtendableList(
            title="Filter by category",
            categories,
            filterScreenViewModel,
            filterType = "Category",
            limit = 6
        )
        Column(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp,horizontal = 10.dp)
        ) {
            Text(
                text = "Filter by difficulty",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 5.dp, start = 10.dp, end = 10.dp, bottom = 3.dp)
            )
            DifficultyFilter(filterScreenViewModel, difficulties, 1f, color = Color.Transparent)
        }

        Column(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp, horizontal = 20.dp)
            ,
        ) {
            Text(
                text = "Sort by",
                color =  MaterialTheme.colorScheme.primary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 5.dp, bottom = 3.dp)
            )
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                DropdownCombo(modifier = Modifier.fillMaxWidth(),
                    filterScreenViewModel = filterScreenViewModel,
                    filterType = "SortBy"
                )
            }
        }
        Column(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp, horizontal = 20.dp)
            ,
        ) {
            Text(
                text = "Sort direction",
                color =  MaterialTheme.colorScheme.primary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 5.dp, bottom = 3.dp)
            )
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                DropdownCombo(modifier = Modifier.fillMaxWidth(1f),
                    filterScreenViewModel = filterScreenViewModel,
                    items = arrayOf("Default", "Ascending", "Descending"),
                    filterType = "SortDirection"
                )
            }
        }
        Column(
            Modifier.fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = {filterScreenViewModel.onSubmitSearch()},
                modifier = Modifier.fillMaxWidth()
                    .padding(top = 30.dp)
                    .height(100.dp)
                ,

                shape = AbsoluteCutCornerShape(10.dp)
            ) {
                Text(
                    text = "SEARCH",
                    fontSize = 24.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.ExtraBold,
                )
            }
        }

        Spacer(modifier = Modifier.fillMaxWidth().padding(top = 50.dp))
    }
}

