package com.cookingassistant.data.DTO
import android.graphics.Bitmap

data class Recipe(
    val id: Int,
    val name: String?,
    val description: String?,
    val CreatedDate: String,
    val ModificationDate: String?,
    val authorName: String,
    val image: Bitmap?,
    val ratings: Double?,
    val VoteCount: Int,
    val timeInMinutes: Int?,
    val serves: Int,
    val difficulty: String?,
    val category: String,
    val categoryId: Int,
    val occasion:String,
    val ingredientNames: List<String?>,
    val ingredientQuantities: List<String?>,
    val ingredientUnits: List<String?>,
    val steps: List<String?>,
)

data class RecipeSimpleGetDTO(
    val id: Int,
    val name: String,

    val ratings: Float,
    val timeInMinutes: Int,
    val difficultyName: String,
    val voteCount: Int,
    val caloricity: Int,
    val occasionName: String,
    val categoryName: String
)

data class RecipePageResponse(
    val items: List<RecipeSimpleGetDTO>,
    val totalPages: Int,
    val itemsFrom: Int,
    val itemsTo: Int,
    val totalItemsCount: Int
)

data class RecipeNameDTO(
    val id: Int,
    val name: String
)

data class RecipePostDTO(
    val name: String,
    val description: String?,
    var imageData: ByteArray?,
    val serves: Int?,
    val difficultyId: Int,
    val timeInMinutes: Int?,
    val categoryId: Int,
    val occasionId: Int,
    val caloricity: Int,
    val ingredientNames: List<String?>,
    val ingredientQuantities: List<String?>,
    val ingredientUnits: List<String?>,
    val steps: List<String?>,
)

data class RecipeGetDTO(
    val id: Int,
    val name: String,
    val description: String,
    val authorName: String,
    val ratings: Float,
    val timeInMinutes: Int,
    val serves: Int,
    val difficultyName: String?,
    val voteCount: Int,
    val categoryName: String,
    val occasionName: String,
    val caloricity: Int,
    val ingredients: List<RecipeIngredientGetDTO>?,
    val steps: List<StepGetDTO>?
)

data class RecipeQuery(
    val SearchPhrase: String? = null ,
    val Ingredients: List<String>? = null,
    val SortBy: String? = null,
    val SortDirection: String? = null,
    val Difficulty: String? = null,
    val Category: String? = null,
    val Occasion: String? = null,
    val PageNumber: Int = 1,
    val PageSize: Int = 10
)

data class RecipeIngredientGetDTO(
    val ingredientName:String?,
    val quantity:String?,
    val unit:String?
)

data class StepGetDTO(
    val stepNumber:Int,
    val description:String?
)





