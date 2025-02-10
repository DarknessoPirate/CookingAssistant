package com.cookingassistant.data.objects

import com.cookingassistant.ui.composables.topappbar.TopAppBarViewModel

object ScreenControlManager {
    //lateinit var navController:NavController //Memory leak 💀
    var hasLoggedIn = false
    var activeTool = ""
    lateinit var topAppBarViewModel: TopAppBarViewModel
    /*
    fun navigate(destination:String) {
        navController.navigate(destination)
        activeTool = ""
    }
    */
}