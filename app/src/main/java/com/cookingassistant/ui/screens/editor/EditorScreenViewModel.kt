package com.cookingassistant.ui.screens.editor


import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cookingassistant.data.DTO.CategoriesGetDTO
import com.cookingassistant.data.DTO.DifficultiesGetDTO
import com.cookingassistant.data.DTO.OccasionsGetDTO
import com.cookingassistant.data.DTO.RecipeIngredientGetDTO
import com.cookingassistant.data.DTO.RecipePostDTO
import com.cookingassistant.data.Models.Result
import com.cookingassistant.services.RecipeService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

enum class State{
    None,
    Create,
    Modify
}

class EditorScreenViewModel(private val recipeService: RecipeService) : ViewModel() {
    private val _currentScreen = MutableStateFlow("front")
    val currentScreen: MutableStateFlow<String> = _currentScreen

    var state by mutableStateOf<State>(State.None)

    var id by mutableStateOf<Int?>(null)
    var name by mutableStateOf("")
    var description by mutableStateOf("")
    var image by mutableStateOf<Bitmap?>(null)
    var serves by mutableStateOf<Int?>(null)
    var difficulty by mutableStateOf<DifficultiesGetDTO?>(null)
    var time by mutableStateOf<Int?>(null)
    var category by mutableStateOf<CategoriesGetDTO?>(null)
    var occasion by mutableStateOf<OccasionsGetDTO?>(null)
    var calories by mutableStateOf<Int?>(null)
    var ingredients by mutableStateOf(listOf<RecipeIngredientGetDTO>())
    var steps by mutableStateOf(listOf<String>())

    val difficultyOptions = MutableStateFlow<List<DifficultiesGetDTO>>(listOf())
    val categoryOptions = MutableStateFlow<List<CategoriesGetDTO>>(listOf())
    val occasionOptions = MutableStateFlow<List<OccasionsGetDTO>>(listOf())
    val ingredientsOptions = MutableStateFlow<List<String>>(listOf())
    val unitOptions = MutableStateFlow<List<String>>(listOf())

    fun navigateTo(screen: String) {
        _currentScreen.value = screen
    }

    init {
        loadOccasions()
        loadCategories()
        loadDifficulties()
        loadIngredients()
        loadUnits()
    }

    fun checkRecipe():Boolean{
        if(name.isNotBlank() && description.isNotBlank()
            && image != null && serves != null
            && difficulty != null && time != null
            && category != null && occasion != null
            && calories != null && ingredients.size > 0
            && steps.size > 0){
            return true
        }
        return false
    }

    fun emptyRecipe(){
        navigateTo("front")
        state = State.Create

        id = null
        name = ""
        description = ""
        image = null
        serves = null
        difficulty = null
        time = null
        category = null
        occasion = null
        calories = null
        ingredients = listOf()
        steps = listOf()
    }

    fun loadRecipe(recipeId : Int){
        navigateTo("front")
        state = State.Modify

        viewModelScope.launch {
            var success = false
            try{
                val result = recipeService.getRecipeDetails(recipeId)
                if(result is Result.Success){
                    success = true
                    id = recipeId
                    name = result.data?.name ?: ""
                    description = result.data?.description ?: ""
                    serves = result.data?.serves ?: 0
                    difficulty = difficultyOptions.value.find { it.name == result.data?.difficultyName }
                    time = result.data?.timeInMinutes
                    category = categoryOptions.value.find { it.name == result.data?.categoryName }
                    occasion = occasionOptions.value.find { it.name == result.data?.occasionName }
                    calories = result.data?.caloricity
                    ingredients = result.data?.ingredients ?: listOf()
                    val tempSteps = result.data?.steps ?: listOf()
                    val sortedSteps = tempSteps.sortedBy { it.stepNumber }
                    steps = sortedSteps.map { it.description ?: "" }
                }
                else if(result is Result.Error){
                    Log.e("EditorScreenViewModel", result.message)
                }
            }catch (e: Exception) {
                Log.e("EditorScreenViewModel", e.message ?: "recipe couldnt be loaded")
            }
            if(success)
            {
                try{
                    val result = recipeService.getRecipeImageBitmap(recipeId)
                    if(result is Result.Success){
                        image = result.data
                    }
                    else if(result is Result.Error){
                        Log.e("EditorScreenViewModel", result.message)
                    }
                }catch (e: Exception) {
                    Log.e("EditorScreenViewModel", e.message ?: "image couldnt be loaded")
                }
            }
        }
    }

    fun modifyRecipe(recipe: RecipePostDTO){
        if(id != null)
        {
            viewModelScope.launch {
                var success = false
                try{
                    val result = recipeService.modifyRecipe(id!!,recipe)
                    if(result is Result.Success){
                        success = true
                    }
                    else if(result is Result.Error){
                        Log.e("EditorScreenViewModel", result.message)
                    }
                }catch (e: Exception) {
                    Log.e("EditorScreenViewModel", e.message ?: "recipe couldnt be modified")
                }
            }
        }
    }

    fun createRecipe(recipe: RecipePostDTO){
        viewModelScope.launch {
            var success = false
            try{
                val result = recipeService.addRecipe(recipe)
                if(result is Result.Success){
                    success = true
                }
                else if(result is Result.Error){
                    Log.e("EditorScreenViewModel", result.message)
                }
            }catch (e: Exception) {
                Log.e("EditorScreenViewModel", e.message ?: "recipe couldnt be created")
            }
        }
    }

    fun loadIngredients(){
        viewModelScope.launch {
            var success = false
            try{
                val result = recipeService.getAllIngredientsList()
                if(result is Result.Success){
                    ingredientsOptions.value = result.data ?: ingredientsOptions.value
                    success = true
                }
                else if(result is Result.Error){
                    Log.e("EditorScreenViewModel", result.message)
                }
            }catch (e: Exception) {
                Log.e("EditorScreenViewModel", "ingredients couldnt be loaded")
            }
        }
    }

    fun loadUnits(){
        viewModelScope.launch {
            var success = false
            try{
                val result = recipeService.getAllUnitsList()
                if(result is Result.Success){
                    unitOptions.value = result.data ?: unitOptions.value
                    success = true
                }
                else if(result is Result.Error){
                    Log.e("EditorScreenViewModel", result.message)
                }
            }catch (e: Exception) {
                Log.e("EditorScreenViewModel", "units couldnt be loaded")
            }
        }
    }

    fun loadDifficulties(){
        viewModelScope.launch {
            var success = false
            try {
                val result = recipeService.getAllDifficultiesList()
                if(result is Result.Success){
                    difficultyOptions.value = result.data ?: difficultyOptions.value
                    success = true
                }
                else if(result is Result.Error){
                    Log.e("EditorScreenViewModel", result.message)
                }
            } catch (e: Exception) {
                Log.e("EditorScreenViewModel", "difficulties couldnt be loaded")
            }
        }
    }

    fun loadCategories(){
        viewModelScope.launch {
            var success = false
            try {
                val result = recipeService.getAllCategoriesList()
                if(result is Result.Success){
                    categoryOptions.value = result.data ?: categoryOptions.value
                    success = true
                }
                else if(result is Result.Error){
                    Log.e("EditorScreenViewModel", result.message)
                }
            } catch (e: Exception) {
                Log.e("EditorScreenViewModel", "categories couldnt be loaded")
            }
        }
    }

    fun loadOccasions(){
        viewModelScope.launch {
            var success = false
            try {
                val result = recipeService.getAllOccasionsList()
                if(result is Result.Success){
                    occasionOptions.value = result.data ?: occasionOptions.value
                    success = true
                }
                else if(result is Result.Error){
                    Log.e("EditorScreenViewModel", result.message)
                }
            } catch (e: Exception) {
                Log.e("EditorScreenViewModel", "occasions couldnt be loaded")
            }
        }
    }
}