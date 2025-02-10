package com.cookingassistant.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun ToolDescription(toolName: String, description: String, color: Color) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp)) {
        Text(
            color = color,
            fontSize = 20.sp,
            text=toolName,
        )
        Text(
            color = color,
            fontSize = 18.sp,
            text=description,
        )
    }
}

@Composable
fun HelpSection(header : String, description: String, color: Color, endingDivider: Boolean = true) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            "${header}",
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
            color = color
        )

        Text(
            description,
            fontWeight = FontWeight.Light,
            fontSize = 18.sp,
            textAlign = TextAlign.Justify,
            modifier = Modifier.fillMaxWidth(),
            color = color
        )
    }
    if(endingDivider) {
        HorizontalDivider(modifier = Modifier.padding(vertical = 20.dp).background(MaterialTheme.colorScheme.primary))
    }
}

@Composable
//@Preview
fun HelpWindow(navHostController: NavHostController) {
//fun HelpWindow() {
    Column(horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(horizontal = 20.dp, vertical = 50.dp).verticalScroll(rememberScrollState())
    ) {
        val textColor =  MaterialTheme.colorScheme.onBackground;
        var helpText by remember { mutableStateOf("") }
        val searchTextHelp = "You can use top bar to search recipes using keyboard input or using your voice by clicking microphone icon." +
                " You can select recipe from the list or click search button to search for recipes based on phrase that either " +
                "appears in recipe's title or description."

        when (navHostController.currentDestination?.route) {
            "home" -> helpText = "Home screen displays recipe of the day."
            "timer" -> helpText = "Timer is helpful while preparing a meal. If you allowed notifications, you will get notification when time runs out."
            "recipeReviews" -> helpText = "Recipe reviews shows all reviews - you can also edit or delete your review if wrote one."
            "recipeList" -> helpText = "Being in recipe list screen, use arrow buttons or swipe left / right to switch pages. You can also manually type page you want to go to."
            "recipeScreen" -> helpText = "In recipe instructions screen use arrow buttons or swipe left / right to follow recipe instructions. You can also comment or add recipe to favorites clicking icon buttons at the right top.\nTop-left button offers touch-less controls. In touch-less mode, lay your phone on flat surface and roll it around 30 degrees either left or right side to switch current page without touching your screen."
            "editor" -> helpText = "Editor screen allows to create and edit your own recipes. Fill all necessary fields to make it happen."
            "profile" -> helpText = "Profile screen gives you insights inside your account information, favorite recipes and recipes created by you."
        }
        HelpSection("About", "Shake your phone to quickly open this window. You can find basic app usage instructions in this page.", textColor)
        HelpSection("Current window help", helpText, textColor)
        HelpSection("Basic search", searchTextHelp, textColor)
        HelpSection("Tool drawer", "Left drawer displays available tools.", textColor, false)
        ToolDescription("Shopping list", "In shopping list you can quickly add, cross or remove product names stored in your device.", textColor)
        ToolDescription("Timer", "You will get notified when time runs out. Helpful while cooking.", textColor)
        ToolDescription("Advanced search", "You will get notified when time runs out. Helpful while cooking.", textColor)
        ToolDescription("Create recipe", "Create your own recipes and share them with the world.", textColor)
    }
}