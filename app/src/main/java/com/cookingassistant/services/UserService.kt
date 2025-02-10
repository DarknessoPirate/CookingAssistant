package com.cookingassistant.services

import android.graphics.Bitmap
import android.util.Log
import com.cookingassistant.data.DTO.RecipePageResponse
import com.cookingassistant.data.DTO.RecipeQuery
import com.cookingassistant.data.DTO.UserDeleteRequest
import com.cookingassistant.data.DTO.UserPasswordChangeDTO
import com.cookingassistant.data.Models.Result
import com.cookingassistant.data.repositories.ApiRepository
import com.cookingassistant.util.ApiResponseParser
import com.cookingassistant.util.ImageConverter
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.InputStream

class UserService(private val _apiRepository: ApiRepository) {

    private val _apiResponseParser = ApiResponseParser()

    // TODO: ADD EXCEPTION HANDLING
    suspend fun addRecipeToUserFavourites(recipeId:Int):Result<Unit?>{
        return try {
            val response = _apiRepository.addRecipeToFavourites(recipeId)
            _apiResponseParser.wrapResponse(response)
        } catch (e: Exception) {
            Log.e("UserService", "Exception in addRecipeToUserFavorites: ${e.localizedMessage}", e)
            Result.Error(
                message = "An exception occurred while adding recipe to favourites: ${e.localizedMessage}",
                errorCode = INTERNAL_SERVICE_ERROR_CODE
            )

        }

    }

    suspend fun removeRecipeFromUserFavourites(recipeId: Int): Result<Unit?> {
        return try {
            val response = _apiRepository.removeRecipeFromFavourites(recipeId)
            _apiResponseParser.wrapResponse(response)
        } catch (e: Exception) {
            Log.e("UserService", "Exception in removeRecipeFromUserFavourites: ${e.localizedMessage}", e)
            Result.Error(
                message = "An exception occurred while removing recipe from favourites: ${e.localizedMessage}",
                errorCode = INTERNAL_SERVICE_ERROR_CODE
            )
        }
    }

    suspend fun getUserFavouriteRecipes(rq: RecipeQuery): Result<RecipePageResponse?> {
        return try {
            val response = _apiRepository.getFavouriteRecipes(
                SearchPhrase = rq.SearchPhrase,
                IngredientsSearch = rq.Ingredients,
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
            Log.e("UserService", "Exception in getUserFavouriteRecipes: ${e.localizedMessage}", e)
            Result.Error(
                message = "An exception occurred while fetching favourite recipes: ${e.localizedMessage}",
                errorCode = INTERNAL_SERVICE_ERROR_CODE
            )
        }
    }

    suspend fun getUserRecipes(rq: RecipeQuery): Result<RecipePageResponse?>{
        return try{
            val response = _apiRepository.getUserRecipes(
                SearchPhrase = rq.SearchPhrase,
                IngredientsSearch = rq.Ingredients,
                SortBy = rq.SortBy,
                SortDirection = rq.SortDirection,
                FilterByDifficulty = rq.Difficulty,
                FilterByCategoryName = rq.Category,
                FilterByOccasion = rq.Occasion,
                PageNumber = rq.PageNumber,
                PageSize = rq.PageSize
            )
            _apiResponseParser.wrapResponse(response)
        } catch (e: Exception){
            Log.e("UserService", "Exception in getUserRecipes: ${e.localizedMessage}", e)
            Result.Error(
                message = "An exception occurred while getting user recipes: ${e.localizedMessage}",
                errorCode = INTERNAL_SERVICE_ERROR_CODE
            )
        }

    }

    suspend fun checkIfRecipeInUserFavourites(recipeId: Int): Result<Boolean?> {
        return try {
            val response = _apiRepository.checkIfRecipeInFavourites(recipeId)
            _apiResponseParser.wrapResponse(response)
        } catch (e: Exception) {
            Log.e("UserService", "Exception in checkIfRecipeInUserFavourites: ${e.localizedMessage}", e)
            Result.Error(
                message = "An exception occurred while checking if recipe is in favourites: ${e.localizedMessage}",
                errorCode = INTERNAL_SERVICE_ERROR_CODE
            )
        }
    }

    suspend fun checkIfRecipeIsCreatedByUser(recipeId: Int): Result<Boolean?> {
        return try {
            val response = _apiRepository.checkIfRecipeIsCreatedByUser(recipeId)
            _apiResponseParser.wrapResponse(response)
        } catch (e: Exception) {
            Log.e("UserService", "Exception in checkIfRecipeIsCreatedByUser: ${e.localizedMessage}", e)
            Result.Error(
                message = "An exception occurred while checking if recipe is created by user: ${e.localizedMessage}",
                errorCode = INTERNAL_SERVICE_ERROR_CODE
            )
        }
    }

    suspend fun addUserProfilePicture(imageStream: InputStream, mimeType: String): Result<Unit?> {
        return try {
            val imageRequestBody = imageStream.readBytes()
                .toRequestBody("image/jpeg".toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData("imageData", "profile.${mimeType.substringAfter("/")}", imageRequestBody)

            val response = _apiRepository.addProfilePicture(imagePart)
            _apiResponseParser.wrapResponse(response)
        } catch (e: Exception) {
            Log.e("UserService", "Exception in addUserProfilePicture: ${e.localizedMessage}", e)
            Result.Error(
                message = "An exception occurred while uploading profile picture: ${e.localizedMessage}",
                errorCode = INTERNAL_SERVICE_ERROR_CODE
            )
        }
    }
    suspend fun getUserProfilePictureBitmap(): Result<Bitmap?> {
        return try {
            val response = _apiRepository.getUserProfilePicture()
            if (response.isSuccessful && response.body() != null) {
                val imageByteArray = response.body()!!.bytes()
                val bitmap = ImageConverter.byteArrayToBitmap(imageByteArray)
                Result.Success(bitmap)
            } else {
                Result.Error(
                    message = "Failed to fetch user image: ${response.message()}",
                    errorCode = response.code()
                )
            }
        } catch (e: Exception) {
            Log.e("UserService", "Exception in getUserProfilePictureBitmap: ${e.localizedMessage}", e)
            Result.Error(
                message = "An exception occurred while fetching profile picture: ${e.localizedMessage}",
                errorCode = INTERNAL_SERVICE_ERROR_CODE
            )
        }
    }

    suspend fun changeUserPassword(oldPassword: String, newPassword: String, newPasswordConfirm: String): Result<Unit?> {
        return try {
            val passwordChangeDTO = UserPasswordChangeDTO(
                oldPassword,
                newPassword,
                newPasswordConfirm
            )
            val response = _apiRepository.changePassword(passwordChangeDTO)
            _apiResponseParser.wrapResponse(response)
        } catch (e: Exception) {
            Log.e("UserService", "Exception in changeUserPassword: ${e.localizedMessage}", e)
            Result.Error(
                message = "An exception occurred while changing user password: ${e.localizedMessage}",
                errorCode = INTERNAL_SERVICE_ERROR_CODE
            )
        }
    }

    suspend fun deleteUserAccount(password: String): Result<Unit?> {
        return try {
            val userDeleteRequest = UserDeleteRequest(password)
            val response = _apiRepository.deleteUser(userDeleteRequest)
            _apiResponseParser.wrapResponse(response)
        } catch (e: Exception) {
            Log.e("UserService", "Exception in deleteUserAccount: ${e.localizedMessage}", e)
            Result.Error(
                message = "An exception occurred while deleting user account: ${e.localizedMessage}",
                errorCode = INTERNAL_SERVICE_ERROR_CODE
            )
        }
    }
}