package com.cookingassistant.ui.screens.editor


import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.cookingassistant.ui.screens.editor.composables.DetailsPage
import com.cookingassistant.ui.screens.editor.composables.FrontPage
import com.cookingassistant.ui.screens.editor.composables.StepsPage


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun EditorScreen(navController: NavController, viewModel: EditorScreenViewModel = viewModel()) {
    val currentScreen by viewModel.currentScreen.collectAsState()

    var doItOnce by remember { mutableStateOf(true) }

    if(doItOnce) {
        viewModel.loadOccasions()
        viewModel.loadCategories()
        viewModel.loadDifficulties()
        viewModel.loadIngredients()
        viewModel.loadUnits()
        doItOnce = false
    }

    AnimatedContent(
        targetState = currentScreen,
        transitionSpec = {
            if (targetState == "front" && initialState != "front") {
                fadeIn() with fadeOut()
            } else if (targetState == "details" && initialState != "details") {
                fadeIn() with fadeOut()
            } else {
                fadeIn() with fadeOut()
            }
        }
    ) { screen ->
        when(screen) {
            "front" -> {
                FrontPage(viewModel)
            }
            "details" -> {
                DetailsPage(viewModel)
            }
            "steps" -> {
                StepsPage(navController, viewModel)
            }
        }
    }

}


