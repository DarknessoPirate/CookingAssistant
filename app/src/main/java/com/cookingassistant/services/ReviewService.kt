package com.cookingassistant.services

import android.graphics.Bitmap
import android.util.Log
import com.cookingassistant.data.DTO.ReviewGetDTO
import com.cookingassistant.data.DTO.ReviewPostDTO
import com.cookingassistant.data.Models.Result
import com.cookingassistant.data.repositories.ApiRepository
import com.cookingassistant.util.ApiResponseParser
import com.cookingassistant.util.ImageConverter

class ReviewService(private val _apiRepository: ApiRepository) {

    private val _apiResponseParser = ApiResponseParser()

    suspend fun addReview(recipeId: Int, reviewPostDTO: ReviewPostDTO):Result<Unit?>{
        return try {
            val response = _apiRepository.postReview(recipeId, reviewPostDTO)
            _apiResponseParser.wrapResponse(response)

        } catch (e: Exception) {
            Log.e("ReviewService", "Exception in addReview: ${e.localizedMessage}", e)
            Result.Error(
                message = "An exception occurred while adding review: ${e.localizedMessage}",
                errorCode = INTERNAL_SERVICE_ERROR_CODE
            )
        }
    }

    suspend fun modifyReview(recipeId: Int, reviewPostDTO: ReviewPostDTO):Result<Unit?>{
        return try {
            val response = _apiRepository.modifyReview(recipeId, reviewPostDTO)
            _apiResponseParser.wrapResponse(response)

        } catch (e: Exception){
            Log.e("ReviewService", "Exception in modifyReview: ${e.localizedMessage}", e)
            Result.Error(
                message = "An exception occurred while modifying review: ${e.localizedMessage}",
                errorCode = INTERNAL_SERVICE_ERROR_CODE
            )
        }
    }

    suspend fun deleteReview(recipeId: Int):Result<Unit?>{
        return try {
            val response = _apiRepository.deleteReview(recipeId)
            _apiResponseParser.wrapResponse(response)
        } catch (e: Exception){
            Log.e("ReviewService", "Exception in deleteReview: ${e.localizedMessage}", e)
            Result.Error(
                message = "An exception occurred while deleting review: ${e.localizedMessage}",
                errorCode = INTERNAL_SERVICE_ERROR_CODE
            )
        }
    }

    suspend fun getMyReview(recipeId: Int):Result<ReviewGetDTO?>{
        return try {
            val response = _apiRepository.getMyReview(recipeId)
            _apiResponseParser.wrapResponse(response)
        } catch (e: Exception){
            Log.e("ReviewService", "Exception in getMyReview: ${e.localizedMessage}", e)
            Result.Error(
                message = "An exception occurred while getting user review: ${e.localizedMessage}",
                errorCode = INTERNAL_SERVICE_ERROR_CODE
            )
        }
    }

    suspend fun getAllReviews(recipeId: Int):Result<List<ReviewGetDTO>?> {
        return try{
            val response = _apiRepository.getAllReviews(recipeId)
            _apiResponseParser.wrapResponse(response)

        } catch (e: Exception){
            Log.e("ReviewService", "Exception in getAllReview: ${e.localizedMessage}", e)
            Result.Error(
                message = "An exception occurred while getting all reviews: ${e.localizedMessage}",
                errorCode = INTERNAL_SERVICE_ERROR_CODE
            )
        }
    }

    // TODO: ADD EXCEPTION HANDLING
    suspend fun getReviewImageBitmap(reviewId: Int): Result<Bitmap?>{
        return try {
            val response = _apiRepository.getReviewImage(reviewId)
            if(response.isSuccessful && response.body() != null) {
                val imageByteArray = response.body()!!.bytes()
                val bitmap = ImageConverter.byteArrayToBitmap(imageByteArray)

                return Result.Success(bitmap)

            }
            else {
                return Result.Error("Failed to fetch user image:${response.message()}", errorCode = response.code())
            }

        } catch (e: Exception) {
            Log.e("ReviewService", "Exception in getReviewImageBitmap: ${e.localizedMessage}", e)
            Result.Error(
                message = "An exception occurred while getting review image: ${e.localizedMessage}",
                errorCode = INTERNAL_SERVICE_ERROR_CODE
            )
        }
    }
}