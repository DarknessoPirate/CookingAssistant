package com.cookingassistant.ui.screens.profile

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cookingassistant.data.Models.Result
import com.cookingassistant.data.repositories.TokenRepository
import com.cookingassistant.services.UserService
import kotlinx.coroutines.launch
import java.io.InputStream

class ProfileScreenViewModel(private val userService : UserService, private val tokenRepository: TokenRepository) : ViewModel() {
    private var _userProfilePicture by mutableStateOf<Bitmap?>(null)
    var userProfilePicture by mutableStateOf<Bitmap?>(null)

    private var _success by mutableStateOf(false)
    private var _operationFinished by mutableStateOf(false)
    val success: Boolean
        get() = _success
    val operationFinished: Boolean
        get() = _operationFinished


    fun getUserProfilePicture() {
        viewModelScope.launch {
            var success = false
            try {
                val result = userService.getUserProfilePictureBitmap()
                if (result is Result.Success) {
                    _userProfilePicture = result.data
                    success = true
                } else if (result is Result.Error) {
                    Log.e("ProfileScreenViewModel", result.message)
                }
            } catch (e: Exception) {
                Log.e("ProfileScreenViewModel", e.message ?: "picture couldnt be loaded")
            }
            if(success && _userProfilePicture != null) {
                userProfilePicture = _userProfilePicture
            }
        }
    }

    fun addUserProfilePicture(imageStream: InputStream, mimeType: String){
        viewModelScope.launch {
            var success = false
            try {
                val result = userService.addUserProfilePicture(imageStream,mimeType)
                if (result is Result.Success) {
                    success = true
                } else if (result is Result.Error) {
                    Log.e("ProfileScreenViewModel", result.message)
                }
            } catch (e: Exception) {
                Log.e("ProfileScreenViewModel", e.message ?: "picture couldnt be loaded")
            }
        }
    }

    fun deleteAccount(password : String){
        viewModelScope.launch {
            try {
                val result = userService.deleteUserAccount(password)
                if (result is Result.Success) {
                    _success = true
                } else if (result is Result.Error) {
                    Log.e("ProfileScreenViewModel", result.message)
                    _success = false
                }
            } catch (e: Exception) {
                Log.e("ProfileScreenViewModel", e.message ?: "picture couldnt be loaded")
                _success = false
            }
            finally {
                _operationFinished = true
            }
        }
    }

    fun resetToken()
    {
        tokenRepository.clearToken()
    }

    fun resetState() {
        _operationFinished = false
    }
}