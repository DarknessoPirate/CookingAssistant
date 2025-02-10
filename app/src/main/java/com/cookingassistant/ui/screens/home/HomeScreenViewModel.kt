package com.cookingassistant.ui.screens.home
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.cookingassistant.data.DTO.RecipeGetDTO
import com.cookingassistant.services.RecipeService
import com.cookingassistant.services.UserService
import com.cookingassistant.data.Models.Result
import com.cookingassistant.ui.screens.recipescreen.RecipeScreenViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeScreenViewModel(private val _recipeService: RecipeService,
                          private val _recipeScreenViewModel: RecipeScreenViewModel,
                          private val _navController : NavHostController
):ViewModel() {
    val recipePlaceHolder = RecipeGetDTO(1,"","","",4f,
        2,2,"",2,"","",
        1, listOf(), listOf()
    )

    private val _recipeImage = MutableStateFlow<Bitmap?>(null)
    val recipeImage: StateFlow<Bitmap?> get() = _recipeImage

    private val _recipe = MutableStateFlow<RecipeGetDTO>(recipePlaceHolder)
    val recipe : StateFlow<RecipeGetDTO> = _recipe

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading


    fun fetchRecipeImage(recipeId:Int){
        viewModelScope.launch {
            val result: Result<Bitmap?> = _recipeService.getRecipeImageBitmap(recipeId)

            if (result is Result.Success && result.data != null) {
                val bitmap: Bitmap = result.data
                _recipeImage.value = bitmap
            } else if (result is Result.Error) {
                _recipeImage.value = null
            }
        }
    }

    fun fetchDailyRecipe() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = _recipeService.getDailyRecipe()

            if (result is Result.Success && result.data != null) {
                val recipe: RecipeGetDTO = result.data
                Log.d("recipe", recipe.id.toString())
                _recipe.value = recipe
                fetchRecipeImage(_recipe.value.id)
            } else if (result is Result.Error) {
                Log.e("recipe fetch", result.message)
            }
            _isLoading.value = false
        }
    }



    fun onRecipeClick(recipeId : Int) {
        _recipeScreenViewModel.loadRecipe(recipeId)
        _navController.navigate("recipeScreen")
    }

}