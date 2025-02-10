package com.cookingassistant.ui.screens.editor.authorization

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cookingassistant.data.Models.Result
import com.cookingassistant.services.UserService
import kotlinx.coroutines.launch

class AuthorizationScreenViewModel(private val userService : UserService) : ViewModel(){
    private var _success by mutableStateOf(false)
    private var _operationFinished by mutableStateOf(false)
    val success: Boolean
        get() = _success
    val operationFinished: Boolean
        get() = _operationFinished


    fun changePassword(currentPassword : String, newPassword : String, confirmPassword : String){
        viewModelScope.launch{
            try{
                val result = userService.changeUserPassword(currentPassword,newPassword,confirmPassword)
                if(result is Result.Success){
                    _success = true
                }
                else if(result is Result.Error){
                    Log.e("AuthorizationScreenViewModel", result.message)
                    _success = false
                }
            }catch (e: Exception) {
                Log.e("AuthorizationScreenViewModel", e.message ?: "password couldnt be changed")
                _success = false
            }
            finally{
                _operationFinished = true
            }
        }
    }

    fun resetState() {
        _operationFinished = false
    }
}