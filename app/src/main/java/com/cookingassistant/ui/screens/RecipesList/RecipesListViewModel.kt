package com.cookingassistant.ui.screens.RecipesList

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cookingassistant.data.DTO.RecipePageResponse
import com.cookingassistant.data.DTO.RecipeQuery
import com.cookingassistant.data.DTO.RecipeSimpleGetDTO
import com.cookingassistant.data.Models.Result
import com.cookingassistant.services.RecipeService
import com.cookingassistant.services.UserService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

enum class State{
    Search,
    Favourite,
    Own
}

class RecipesListViewModel(
    private val _service: RecipeService,
    private val userService: UserService,
    ): ViewModel() {

    private val _currentState : MutableStateFlow<State> = MutableStateFlow(State.Search)
    private val _currentPage : MutableStateFlow<Int> = MutableStateFlow(0)
    private val _totalPages : MutableStateFlow<Int> = MutableStateFlow(0)
    private val _recipes : MutableStateFlow<List<RecipeSimpleGetDTO>> = MutableStateFlow(listOf())
    private val _recipeQuery : MutableStateFlow<RecipeQuery> = MutableStateFlow(RecipeQuery())
    private val _response : MutableStateFlow<RecipePageResponse> = MutableStateFlow<RecipePageResponse>(
        RecipePageResponse(listOf(),0,0,0,0)
    )


    private val _foundResults : MutableStateFlow<Int> = MutableStateFlow(0) //0 - loading //1 - found // 2 - not found
    private val _inputPageNumber : MutableStateFlow<String> = MutableStateFlow("")
    private val _recipeImages = MutableStateFlow<MutableMap<Int,Bitmap?>>(mutableMapOf(0 to null))

    val currentState : StateFlow<State> = _currentState
    val recipeImages : StateFlow<MutableMap<Int,Bitmap?>> = _recipeImages
    val foundResults : StateFlow<Int> = _foundResults
    val currentPage : StateFlow<Int> = _currentPage
    val totalPages : StateFlow<Int> = _totalPages
    val recipes : StateFlow<List<RecipeSimpleGetDTO>> = _recipes
    val recipeQuery : StateFlow<RecipeQuery> = _recipeQuery
    val inputPageNumber : StateFlow<String> = _inputPageNumber
    val loadedImages = MutableStateFlow<Boolean>(false)
    val loadedRecipes : MutableStateFlow<MutableList<RecipeSimpleGetDTO>> = MutableStateFlow(
        mutableListOf()
    )

    private val _isLoading : MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun onInputPageNumberChange(number: String) {
        if(number.length < 10) {
            _inputPageNumber.value = number.filter { it.isDigit() }
        }
    }

    fun loadQuery(rq : RecipeQuery = RecipeQuery(), state: State = State.Search) {
        setState(state)
        _onLoadQuery(rq)
    }

    fun onPageNumberSubmit() {
        if(_inputPageNumber.value.isNotEmpty()) {
            _selectPage(_inputPageNumber.value.toInt())
        }
    }

    fun onPageButtonClicked(number: Int) {
        _selectPage(_currentPage.value+number)
    }

    private fun setState(state: State){
        _currentState.value = state
    }

    private fun _selectPage(pageNumber: Int)  {
        if(pageNumber > 0 && pageNumber <= _totalPages.value) {
            if(_currentState.value == State.Search)
            {
                _onLoadQuery(_recipeQuery.value.copy(PageNumber = pageNumber))
                loadedImages.value = false
            }
            else if(_currentState.value == State.Favourite)
            {
                _onLoadQuery(_recipeQuery.value.copy(PageNumber = pageNumber))
                loadedImages.value = false
            }
            else if(_currentState.value == State.Own)
            {
                _onLoadQuery(_recipeQuery.value.copy(PageNumber = pageNumber))
                loadedImages.value = false
            }
        }
    }

    fun deleteRecipe(recipeId : Int){
        viewModelScope.launch {
            var success = false
            try{
                val result = _service.deleteRecipe(recipeId)
                if(result is Result.Success){
                    success = true
                }
                else if(result is Result.Error){
                    Log.e("RecipeListViewModel", result.message)
                }
            }catch (e: Exception) {
                Log.e("RecipeListViewModel", e.message ?: "recipe couldnt be deleted")
            }
            finally {
                _isLoading.value = true
            }
        }
    }

    fun resetLoading(){
        _isLoading.value = false
    }

    private fun _onLoadQuery(rq : RecipeQuery) {
        _foundResults.value = 0
        _recipeQuery.value = rq
        viewModelScope.launch {
            var tag = "RecipeListViewModel"
            try {
                val result = when(_currentState.value){
                    State.Search -> _service.findAllMatchingRecipes(rq)
                    State.Favourite -> userService.getUserFavouriteRecipes(rq)
                    State.Own -> userService.getUserRecipes(rq)
                }
                Log.e("yes","${_currentState.value}")
                when(result) {
                    is Result.Success -> {
                        if(result.data != null) {
                            _recipeImages.value.apply { clear() }
                            _response.value = result.data
                            _totalPages.value = result.data.totalPages
                            _recipes.value = result.data?.items ?: listOf()
                            _currentPage.value = _recipeQuery.value.PageNumber
                            if(_recipes.value.size != 0) {
                                _foundResults.value = 1
                                loadedRecipes.value = recipes.value.toMutableList()
                            } else {
                                _foundResults.value = 2
                            }
                        }
                        else {
                            _foundResults.value = 2
                            Log.w(tag, "_service.findAllMatchingRecipes(rq).body is empty", )
                        }
                    }
                    is Result.Error -> {
                        _foundResults.value = 2
                        Log.e(tag, result.message)
                    }
                    else -> {
                        _foundResults.value = 2
                        Log.e(tag, "Unexpected error occurred ${tag}")
                    }
                }
            } catch (e: Exception) {
                _foundResults.value = 2
                Log.e(tag, e.message ?: "couldn't get recipes", )
            }

            if(_foundResults.value == 1) { //try get images
                tag = "RecipeListViewModelImages"
                for (r in recipes.value) {
                    var bitmap : Bitmap? = null
                    try {
                        val result = _service.getRecipeImageBitmap(r.id)
                        if(result is Result.Success && result.data != null)
                            bitmap=result.data
                        else if(result is Result.Error){
                            Log.e("_onLoadQuery", "Failed to get image: ${result.message}")
                            // TODO : ADD DEFAULT IMAGE IF FAILED
                            // bitmap = placeholder
                        }
                    }
                    catch (e: Exception) {
                        Log.e(tag, e.message ?: "recipe id ${r.id} image couldn't be loaded", )
                    }
                    val updatedImages = _recipeImages.value.toMutableMap()
                    updatedImages.put(r.id, bitmap)
                    _recipeImages.value = updatedImages
                }
                //loadedImages.value = true
            }
        }
    }
}