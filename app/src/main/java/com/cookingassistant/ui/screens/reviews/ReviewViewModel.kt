package com.cookingassistant.ui.screens.reviews

import android.app.Application
import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import com.cookingassistant.data.DTO.ReviewGetDTO
import com.cookingassistant.services.ReviewService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cookingassistant.data.DTO.Recipe
import com.cookingassistant.data.DTO.ReviewPostDTO
import com.cookingassistant.data.Models.Result
import com.cookingassistant.data.objects.ScreenControlManager
import com.cookingassistant.data.objects.SearchEngine
import kotlinx.coroutines.delay
import java.time.LocalDate

class ReviewViewModel(private val _reviewService: ReviewService) : ViewModel(){

    private val _reviews = MutableStateFlow<List<ReviewGetDTO>?>(emptyList())
    val reviews: StateFlow<List<ReviewGetDTO>?> = _reviews

    private val _reviewImages = MutableStateFlow<MutableMap<Int, Bitmap?>>(mutableMapOf(0 to null))
    val reviewImages : StateFlow<MutableMap<Int, Bitmap?>> = _reviewImages

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _loadingResult = MutableStateFlow<String>("")
    val loadingResult: StateFlow<String> = _loadingResult

    private val _userRating = MutableStateFlow(0)
    val userRating : StateFlow<Int> = _userRating

    private val _userComment = MutableStateFlow("")
    val userComment : StateFlow<String> = _userComment

    private val _isDialogVisible = MutableStateFlow(false)
    val isDialogVisible: StateFlow<Boolean> = _isDialogVisible

    private val _recipeId = MutableStateFlow<Int>(0)
    val recipeId : StateFlow<Int> = _recipeId

    private val _showDialog = MutableStateFlow(false)
    val showDialog : StateFlow<Boolean> = _showDialog

    fun callDialog() {
        viewModelScope.launch {
            _showDialog.value = true
            delay(3000)
            _showDialog.value = false
        }
    }

    private val _currentUserReview = MutableStateFlow<ReviewGetDTO?>(ReviewGetDTO(
        id = -1,
        reviewAuthor = "Anonymous",
        value = 1,
        description = "No review available",
        dateCreated = "1970-01-01T00:00:00",
        dateModified = "1970-01-01T00:00:00"
    ))
    val currentUserReview: StateFlow<ReviewGetDTO?> = _currentUserReview

    fun loadReviews(recipeId: Int) {
        _recipeId.value = recipeId
        viewModelScope.launch {
            val result = _reviewService.getAllReviews(recipeId)
            if (result is Result.Success) {
                _reviews.value = result.data
            }
            LoadCurrentUserReview(recipeId)
        }
    }

    fun LoadReviewsImage(reviews: List<ReviewGetDTO>?) {
        viewModelScope.launch {
            val newImagesMap = mutableMapOf<Int, Bitmap?>()
            reviews?.forEach { review ->
                val result = _reviewService.getReviewImageBitmap(review.id)
                if (result is Result.Success) {
                    newImagesMap[review.id] = result.data
                } else {
                    newImagesMap[review.id] = null // Set null if image loading fails
                }
            }
            _reviewImages.value = newImagesMap
        }
    }

    fun LoadCurrentUserReview(recipeId: Int) {
        viewModelScope.launch {
            val result = _reviewService.getMyReview(recipeId)
            if (result is Result.Success) {
                _currentUserReview.value = result.data
            }
            else {
                Log.e("User not found", "User not found for review")
            }
        }
    }

    fun DeleteReview(recipeId: Int) {
        viewModelScope.launch {
            try {
                _isLoading.value = true // Start loading
                val result = _reviewService.deleteReview(recipeId)
                if (result is Result.Success) {
                    _loadingResult.value = "Review deleted!"
                    _reviews.value = _reviews.value?.filter { it.id != _currentUserReview.value!!.id }
                    _isLoading.value = false
                }
                else if (result is Result.Error ) {
                    _isLoading.value = false
                    _loadingResult.value = "Can't delete review"
                    result.detailedErrors?.forEach { field, messages ->
                        messages.forEach { message ->
                            Log.d("review delete", "$field: $message")
                        }
                    }
                }
            } catch (e: Exception) {
                _loadingResult.value = "Delete failed: no access to server"
                _isLoading.value = false;
            }
            callDialog()
        }
    }

    fun ModifyReview(recipeId: Int, userRating: Int, userComment: String) {
        if(userRating == 0 || userComment == "") {
            return
        }
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val result = _reviewService.modifyReview(recipeId, ReviewPostDTO(_userRating.value, if(_userComment.value=="") null else _userComment.value))
                val result2 = _reviewService.getMyReview(recipeId)
                if(result is Result.Success && result2 is Result.Success) {
                    _loadingResult.value = "Rating successfully submitted"
                    _reviews.value?.forEach { review ->
                        if (review.id == _currentUserReview.value?.id) {
                            review.value = _userRating.value
                            review.description = _userComment.value
                            review.dateModified = result2.data?.dateModified
                        }
                    }
                    _isLoading.value = false
                    _userComment.value = ""
                    _userRating.value = 0
                }
                else if (result is Result.Error) {
                    _loadingResult.value = "Can't submit review"
                    _isLoading.value = false
                    Log.e("Modify Review","result is error: ${result.message}")
                }
            }
            catch (e: Exception) {
                _loadingResult.value = "Modify failed: no access to server"
                _isLoading.value = false;
            }
            callDialog()
        }
    }

    fun onUserCommentChanged(newComment: String) {
        _userComment.value = newComment
    }

    fun onUserRatingChanged(newRating : Int) {
        _userRating.value = newRating
    }

    fun showResultDialog() {
        _isDialogVisible.value = true
    }

    fun hideResultDialog() {
        _isDialogVisible.value = false
    }

    fun clearLoadingResult() {
        _loadingResult.value = "" // Assuming `_loadingResult` is a MutableState holding `loadingResult`
    }


}