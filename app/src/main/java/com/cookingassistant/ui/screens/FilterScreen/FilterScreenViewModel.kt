package com.cookingassistant.ui.screens.FilterScreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.cookingassistant.data.DTO.RecipeQuery
import com.cookingassistant.data.DTO.idNameClassType
import com.cookingassistant.data.Models.Result
import com.cookingassistant.data.objects.SearchEngine
import com.cookingassistant.services.RecipeService
import com.cookingassistant.ui.composables.topappbar.TopAppBarViewModel
import com.cookingassistant.ui.screens.RecipesList.RecipesListViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FilterScreenViewModel(
    private val _service : RecipeService,
    private val _navController : NavHostController,
    private val _recipeListViewModel : RecipesListViewModel,
    private val _topAppBarViewModel : TopAppBarViewModel
) : ViewModel() {
    private val _filterByOccasion: MutableStateFlow<String?> = MutableStateFlow(null)
    private val _filterByCategory: MutableStateFlow<String?> = MutableStateFlow(null)
    private val _filterByDifficulty: MutableStateFlow<String?> = MutableStateFlow(null)
    private val _searchQuery : MutableStateFlow<String?> = MutableStateFlow(null)
    private val _suggestedIngredient : MutableStateFlow<String> = MutableStateFlow("")
    private val _addIngredientText : MutableStateFlow<String> = MutableStateFlow("")
    private val _selectedIngredients : MutableStateFlow<MutableList<String>> = MutableStateFlow(mutableListOf())
    private val _rollIngredients : MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val _sortBy : MutableStateFlow<String?> = MutableStateFlow(null)
    private val _sortDirection : MutableStateFlow<String?> = MutableStateFlow(null)
    private val _allOccasions : MutableStateFlow<List<String>> = MutableStateFlow(listOf())
    private val _allCategories : MutableStateFlow<List<String>> = MutableStateFlow(listOf())
    private val _allDifficulties : MutableStateFlow<List<String>> = MutableStateFlow(listOf())

    val allOccasions : StateFlow<List<String>> = _allOccasions
    val allCategories : StateFlow<List<String>> = _allCategories
    val allDifficulties : StateFlow<List<String>> = _allDifficulties
    val unrollIngredients : StateFlow<Boolean> = _rollIngredients
    val suggestedIngredient : StateFlow<String> = _suggestedIngredient
    val searchQuery: StateFlow<String?> = _searchQuery
    val addIngredientText : StateFlow<String> = _addIngredientText
    val selectedIngredients : StateFlow<MutableList<String>> = _selectedIngredients
    val filterByDifficulty : StateFlow<String?> = _filterByDifficulty

    private fun _toStringList(list : List<idNameClassType>) : List<String> {
        val result = mutableListOf<String>()
        for(item in list) {
            result.add(item.name)
        }
        return result
    }

    init {
        viewModelScope.launch {
            val tag = "FilterScreenViewModel"
            try {
                val result = _service.getAllOccasionsList()
                when (result) {
                    is Result.Success -> {
                        if (result.data != null)
                             _allOccasions.value = _toStringList(result.data)
                        else {
                            Log.w(tag, "_service.getAllOccasionsList().body is empty",)
                        }
                    }

                    is Result.Error -> {
                        Log.e(tag, result.message)
                    }

                    else -> {
                        Log.e(tag, "Unexpected error occurred ${tag}")
                    }
                }
            } catch (e: Exception) {
                Log.e(tag, e.message ?: "couldn't get occasions list",)
            }

            try {
                val result = _service.getAllCategoriesList()
                when (result) {
                    is Result.Success -> {
                        if (result.data != null)
                            _allCategories.value = _toStringList(result.data)
                        else {
                            Log.w(tag, "_service.getAllCategoriesList().body is empty",)
                        }
                    }

                    is Result.Error -> {
                        Log.e(tag, result.message)
                    }

                    else -> {
                        Log.e(tag, "Unexpected error occurred ${tag}")
                    }
                }
            } catch (e: Exception) {
                Log.e(tag, e.message ?: "couldn't get categories list",)
            }

            try {
                val result = _service.getAllDifficultiesList()
                when (result) {
                    is Result.Success -> {
                        if (result.data != null)
                            _allDifficulties.value = _toStringList(result.data)
                        else {
                            Log.w(tag, "_service.getAllOccasionsList().body is empty",)
                        }
                    }

                    is Result.Error -> {
                        Log.e(tag, result.message)
                    }

                    else -> {
                        Log.e(tag, "Unexpected error occurred ${tag}")
                    }
                }
            } catch (e: Exception) {
                Log.e(tag, e.message ?: "couldn't get difficulties list",)
            }
        }
    }

    fun onSubmitSearch() {
        val query = RecipeQuery (
            searchQuery.value,
            if(selectedIngredients.value.size == 0) null else selectedIngredients.value,
            _sortBy.value,
            _sortDirection.value,
            _filterByDifficulty.value,
            _filterByCategory.value,
            _filterByOccasion.value
        )
        _recipeListViewModel.loadQuery(query)
        if(_navController.currentDestination?.route != "recipeList") {
            _navController.navigate("recipeList")
        }
        _topAppBarViewModel.onDeselctTool()
    }

    private fun _onSortByChange(sort : String) {
        if(sort == "Default") {
            _sortBy.value = null
        } else {
            _sortBy.value = sort
        }
    }

    private fun _onSortDirectionChange(direction : String) {
        if(direction == "Default") {
            _sortDirection.value = null
        } else {
            _sortDirection.value = direction
        }
    }

    fun showIngredients() {
        _rollIngredients.value = false
    }

    fun hideIngredients() {
        _rollIngredients.value = true
    }

    fun onIngredientAdd(ingredient : String) {
        if(_selectedIngredients.value.contains(ingredient))
            return
        _selectedIngredients.value = _selectedIngredients.value.toMutableList().apply {
            add(ingredient)
        }
        _addIngredientText.value = ""
    }

    fun onIngredientRemove(ingredient: String) {
        _selectedIngredients.value = _selectedIngredients.value.toMutableList().apply {
            remove(ingredient)
        }
    }

    fun onAddIngredientTextChange(query : String) {
        if(query != "" && query.length >= (_addIngredientText.value.length)) {
            viewModelScope.launch(Dispatchers.IO) {
                _suggestedIngredient.value = SearchEngine.suggetIngredient(query)
            }
        }
        _addIngredientText.value = query
    }

    fun onSearchQueryChange(query: String) {
        if(query == "") {
            _searchQuery.value = null
        } else {
            _searchQuery.value = query
        }
    }

    private fun _onFilterByOccasionChange(occasion : String) {
        if(occasion == _filterByOccasion.value) {
            _filterByOccasion.value = null
        } else {
            _filterByOccasion.value = occasion
        }
    }

    private fun _onFilterByCategoryChange(category : String) {
        if(category == _filterByCategory.value) {
            _filterByCategory.value = null
        } else {
            _filterByCategory.value = category
        }
    }

    fun onFilterByDifficultyChange(difficulty : String) {
        if(difficulty == _filterByDifficulty.value) {
            _filterByDifficulty.value = null
        } else {
            _filterByDifficulty.value = difficulty
        }
    }

    fun onFilterChange(filterQuery : String, value: String) {
        when(filterQuery) {
            "Occasion" -> {_onFilterByOccasionChange(value)}
            "Category" -> {_onFilterByCategoryChange(value)}
            "SortBy" -> {_onSortByChange(value)}
            "SortDirection" -> {_onSortDirectionChange(value)}
            else -> {}
        }
    }

    fun getFilterValue(filterQuery : String) : String {
        when(filterQuery) {
            "Occasion" -> {return _filterByOccasion.value ?: ""}
            "Category" -> {return _filterByCategory.value ?: ""}
            else -> return ""
        }
    }

}