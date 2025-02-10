package com.cookingassistant.services
import android.graphics.Bitmap
import android.util.Log
import com.cookingassistant.data.DTO.CategoriesGetDTO
import com.cookingassistant.data.DTO.DifficultiesGetDTO
import com.cookingassistant.data.DTO.OccasionsGetDTO
import com.cookingassistant.data.DTO.RecipeGetDTO
import com.cookingassistant.data.DTO.RecipeNameDTO
import com.cookingassistant.data.DTO.RecipePageResponse
import com.cookingassistant.data.DTO.RecipePostDTO
import com.cookingassistant.data.DTO.RecipeQuery
import com.cookingassistant.data.Models.Result
import com.cookingassistant.data.repositories.ApiRepository
import com.cookingassistant.util.ApiResponseParser
import com.cookingassistant.util.ImageConverter
import com.cookingassistant.util.RequestBodyFormatter
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody

public const val INTERNAL_SERVICE_ERROR_CODE = -1

class RecipeService(private val _apiRepository: ApiRepository) {

    private val _apiResponseParser = ApiResponseParser()
    private val _formatter = RequestBodyFormatter()


    suspend fun getAllIngredientsList(): Result<List<String>?> {
        return try {
            val response = _apiRepository.getAllIngredientsList()
            _apiResponseParser.wrapResponse(response)

        } catch (e: Exception) {
            Log.e("RecipeService", "Exception in getAllIngredientsList: ${e.localizedMessage}", e)
            Result.Error(
                message = "An exception occurred while fetching ingredients: ${e.localizedMessage}",
                errorCode = INTERNAL_SERVICE_ERROR_CODE
            )
        }
    }

    suspend fun getAllUnitsList(): Result<List<String>?> {
        return try {
            val response =  _apiRepository.getAllUnitList()
            _apiResponseParser.wrapResponse(response)

        } catch(e: Exception) {
            Log.e("RecipeService", "Exception in getAllUnitsList: ${e.localizedMessage}", e)
            Result.Error(
                message = "An exception occurred while fetching units: ${e.localizedMessage}",
                errorCode = INTERNAL_SERVICE_ERROR_CODE
            )
        }
    }

    suspend fun getAllOccasionsList(): Result<List<OccasionsGetDTO>?>{
        return try {
            val response = _apiRepository.getAllOccasionsList()
            _apiResponseParser.wrapResponse(response)

        } catch (e: Exception) {
            Log.e("RecipeService", "Exception in getAllOccasionsList: ${e.localizedMessage}", e)
            Result.Error(
                message = "An exception occurred while fetching occasions list: ${e.localizedMessage}",
                errorCode = INTERNAL_SERVICE_ERROR_CODE
            )
        }
    }

    suspend fun getAllDifficultiesList(): Result<List<DifficultiesGetDTO>?>{
        return try {
            val response = _apiRepository.getAllDifficultiesList()
            _apiResponseParser.wrapResponse(response)

        } catch (e: Exception) {
            Log.e("RecipeService", "Exception in getAllDifficultiesList: ${e.localizedMessage}", e)
            Result.Error(
                message = "An exception occurred while fetching difficulties: ${e.localizedMessage}",
                errorCode = INTERNAL_SERVICE_ERROR_CODE
            )
        }
    }

    suspend fun getAllCategoriesList(): Result<List<CategoriesGetDTO>?> {
        return try {
            val response = _apiRepository.getAllCategoriesList()
            _apiResponseParser.wrapResponse(response)

        } catch (e: Exception) {
            Log.e("RecipeService", "Exception in getAllCategoriesList: ${e.localizedMessage}", e)
            Result.Error(
                message = "An exception occurred while fetching categories: ${e.localizedMessage}",
                errorCode = INTERNAL_SERVICE_ERROR_CODE
            )
        }
    }

    suspend fun findAllMatchingRecipes(rq: RecipeQuery): Result<RecipePageResponse?>{
        return try {
            val response = _apiRepository.getAllRecipes(
                SearchPhrase = rq.SearchPhrase,
                IngredientsSearch = rq.Ingredients?.joinToString(separator = ","),
                SortBy = rq.SortBy,
                SortDirection = rq.SortDirection,
                FilterByDifficulty = rq.Difficulty,
                FilterByCategoryName = rq.Category,
                FilterByOccasion = rq.Occasion,
                PageNumber = rq.PageNumber,
                PageSize = rq.PageSize
            )

            _apiResponseParser.wrapResponse(response)

        } catch (e: Exception) {
            Log.e("RecipeService", "Exception in findAllMatchingRecipes: ${e.localizedMessage}", e)
            Result.Error(
                message = "An exception occurred while fetching matching recipes: ${e.localizedMessage}",
                errorCode = INTERNAL_SERVICE_ERROR_CODE
            )
        }
    }

    suspend fun getRecipeDetails(recipeId: Int):Result<RecipeGetDTO?>{
        return try {
            val response = _apiRepository.getRecipeDetails(recipeId)
            _apiResponseParser.wrapResponse(response)

        } catch (e: Exception) {
            Log.e("RecipeService", "Exception in getRecipeDetails: ${e.localizedMessage}", e)
            Result.Error(
                message = "An exception occurred while getting recipe details recipes: ${e.localizedMessage}",
                errorCode = INTERNAL_SERVICE_ERROR_CODE
            )
        }
    }

    suspend fun downloadRecipePdf(recipeId: Int): Result<ResponseBody?> {
        return try {
            val response = _apiRepository.downloadRecipePdf(recipeId)
            _apiResponseParser.wrapResponse(response)

        } catch (e: Exception) {
            Log.e("RecipeService", "Exception in downloadRecipePdf: ${e.localizedMessage}", e)
            Result.Error(
                message = "An exception occurred while downloading recipe PDF: ${e.localizedMessage}",
                errorCode = INTERNAL_SERVICE_ERROR_CODE
            )
        }
    }

    suspend fun getRecipeNames(): Result<List<RecipeNameDTO>?> {
        return try {
            val response =  _apiRepository.getAllRecipeNames()
            _apiResponseParser.wrapResponse(response)

        } catch (e: Exception) {
            Log.e("RecipeService", "Exception in getRecipeNames: ${e.localizedMessage}", e)
            Result.Error(
                message = "An exception occurred while fetching recipe names: ${e.localizedMessage}",
                errorCode = INTERNAL_SERVICE_ERROR_CODE
            )
        }
    }

    suspend fun getDailyRecipe(): Result<RecipeGetDTO?>{
        return try{
            val response = _apiRepository.getDailyRecipe()
            _apiResponseParser.wrapResponse(response)

        } catch (e: Exception) {
            Log.e("RecipeService", "Exception in getDailyRecipe: ${e.localizedMessage}", e)
            Result.Error(
                message = "An exception occurred while fetching daily recipe: ${e.localizedMessage}",
                errorCode = INTERNAL_SERVICE_ERROR_CODE
            )
        }
    }


    suspend fun getRecipeImageBitmap(recipeId: Int): Result<Bitmap?>{
        return try {
            val response = _apiRepository.getRecipeImage(recipeId)

            if (response.isSuccessful && response.body() != null) {
                val imageByteArray = response.body()!!.bytes()
                val bitmap = ImageConverter.byteArrayToBitmap(imageByteArray)
                Result.Success(bitmap)

            } else {
                Result.Error(
                    message = "Failed to fetch recipe image: ${response.message()}",
                    errorCode = response.code()
                )
            }

        } catch (e: Exception) {
            Log.e("RecipeService", "Exception in getRecipeImageBitmap: ${e.localizedMessage}", e)
            Result.Error(
                message = "An exception occurred while fetching recipe image: ${e.localizedMessage}",
                errorCode = INTERNAL_SERVICE_ERROR_CODE
            )
        }
    }

    // TODO: ADD EXCEPTION HANDLING
    suspend fun addRecipe(recipe: RecipePostDTO): Result<Unit?> {
        return try {
            // Use RequestBodyFormatter for each part
            val namePart = _formatter.fromString(recipe.name)
            val descriptionPart = _formatter.fromString(recipe.description)
            val servesPart = _formatter.fromInt(recipe.serves)
            val difficultyIdPart = _formatter.fromInt(recipe.difficultyId)
            val timeInMinutesPart = _formatter.fromInt(recipe.timeInMinutes)
            val categoryIdPart = _formatter.fromInt(recipe.categoryId)
            val occasionIdPart = _formatter.fromInt(recipe.occasionId)
            val caloricityPart = _formatter.fromInt(recipe.caloricity)

            // Convert lists using helper
            val ingredientNames = _formatter.fromList("IngredientNames", recipe.ingredientNames)
            val ingredientQuantities = _formatter.fromList("IngredientQuantities", recipe.ingredientQuantities)
            val ingredientUnits = _formatter.fromList("IngredientUnits", recipe.ingredientUnits)
            val steps = _formatter.fromList("Steps", recipe.steps)

            // Convert image data if available
            val imagePart = recipe.imageData.let {
                _formatter.fromByteArray(it, "recipe_image.jpg")
            } ?: MultipartBody.Part.createFormData("ImageData", "", "".toRequestBody("image/jpeg".toMediaTypeOrNull()))

            // Prepare all parts for the API call
            val response = _apiRepository.postRecipe(
                namePart, descriptionPart, servesPart, difficultyIdPart, timeInMinutesPart,
                categoryIdPart, occasionIdPart, caloricityPart,
                ingredientNames, ingredientQuantities, ingredientUnits, steps,
                imagePart
            )

            _apiResponseParser.wrapResponse(response)

        } catch (e: Exception) {
            Log.e("RecipeService", "Exception in addRecipe: ${e.localizedMessage}", e)
            Result.Error(
                message = "An exception occurred while adding recipe: ${e.localizedMessage}",
                errorCode = INTERNAL_SERVICE_ERROR_CODE
            )
        }
    }

    // TODO: TEST
    // TODO: ADD EXCEPTION HANDLING
    suspend fun modifyRecipe(recipeId: Int, recipe: RecipePostDTO):Result<Unit?>{

        return try {

            val namePart = _formatter.fromString(recipe.name)
            val descriptionPart = _formatter.fromString(recipe.description)
            val servesPart = _formatter.fromInt(recipe.serves)
            val difficultyIdPart = _formatter.fromInt(recipe.difficultyId)
            val timeInMinutesPart = _formatter.fromInt(recipe.timeInMinutes)
            val categoryIdPart = _formatter.fromInt(recipe.categoryId)
            val occasionIdPart = _formatter.fromInt(recipe.occasionId)
            val caloricityPart = _formatter.fromInt(recipe.caloricity)
            val ingredientNames = _formatter.fromList("IngredientNames", recipe.ingredientNames)
            val ingredientQuantities =
                _formatter.fromList("IngredientQuantities", recipe.ingredientQuantities)
            val ingredientUnits = _formatter.fromList("IngredientUnits", recipe.ingredientUnits)
            val steps = _formatter.fromList("Steps", recipe.steps)

            // Convert image data if available
            val imagePart = recipe.imageData.let {
                _formatter.fromByteArray(it, "recipe_image.jpg")
            } ?: MultipartBody.Part.createFormData(
                "ImageData",
                "",
                "".toRequestBody("image/jpeg".toMediaTypeOrNull())
            )

            val response = _apiRepository.modifyRecipe(
                recipeId,
                namePart, descriptionPart, servesPart, difficultyIdPart, timeInMinutesPart,
                categoryIdPart, occasionIdPart, caloricityPart,
                ingredientNames, ingredientQuantities, ingredientUnits, steps,
                imagePart
            )

            _apiResponseParser.wrapResponse(response)

        } catch (e: Exception) {
            Log.e("RecipeService", "Exception in modifyRecipe: ${e.localizedMessage}", e)
            Result.Error(
                message = "An exception occurred while modifying recipe: ${e.localizedMessage}",
                errorCode = INTERNAL_SERVICE_ERROR_CODE
            )
        }

    }
    // TODO: TEST
    // TODO: ADD EXCEPTION HANDLING
    suspend fun deleteRecipe(recipeId:Int):Result<Unit?>{
        return try {
            val response = _apiRepository.deleteRecipe(recipeId)
            _apiResponseParser.wrapResponse(response)

        } catch (e: Exception) {
            Log.e("RecipeService", "Exception in deleteRecipe: ${e.localizedMessage}", e)
            Result.Error(
                message = "An exception occurred while deleting recipe: ${e.localizedMessage}",
                errorCode = INTERNAL_SERVICE_ERROR_CODE
            )
        }

    }





}