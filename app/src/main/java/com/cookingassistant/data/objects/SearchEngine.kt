package com.cookingassistant.data.objects

import com.cookingassistant.data.DTO.RecipeNameDTO
import com.frosch2010.fuzzywuzzy_kotlin.FuzzySearch
import com.frosch2010.fuzzywuzzy_kotlin.model.ExtractedResult


object SearchEngine {
    private var knownRecipeNames : MutableList<String> = mutableListOf()
    private var knownIngredients : MutableList<String> = mutableListOf()
    private var ids : MutableList<Int> = mutableListOf()

    var loadedApiResources = 0 // number of loaded api successes

    fun getIndex(id : Int) : Int {
        return ids[id]
    }

    fun updateRecipesList(idNames : List<RecipeNameDTO>) {
        knownRecipeNames.clear()
        ids.clear()
        for (recipe in idNames) {
            knownRecipeNames.add(recipe.name)
            ids.add(recipe.id)
        }
        loadedApiResources += 1
    }

    fun updateIngredientsList(list: List<String>) {
        knownIngredients = list.toMutableList()
        loadedApiResources += 1
    }

    fun suggetIngredient(query: String) : String {

        return FuzzySearch.extractOne(query=query, choices = knownIngredients).string
    }

    fun suggestRecipeNames(query : String, limit : Int = 5) : List<ExtractedResult> {
        val found = FuzzySearch.extractTop(query = query, choices = knownRecipeNames, limit)
        var result = mutableListOf<ExtractedResult>()
        for (f in found) {
            result.add(f)
        }
        return result
    }
}