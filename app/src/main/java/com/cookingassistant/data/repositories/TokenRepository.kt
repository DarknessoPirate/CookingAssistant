package com.cookingassistant.data.repositories

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class TokenRepository(context: Context)
{
    // Create or retrieve the master key for encryption
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM) // Use AES256-GCM encryption
        .build()

    // Create the encrypted shared preferences instance
    private val sharedPreferences =
        EncryptedSharedPreferences.create(
        context,
        "secure_prefs", // filename for the shared preferences
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    // Function to save the token
    fun saveToken(token: String?) {
        sharedPreferences.edit()
            .putString("auth_token", token)
            .apply() // Use apply() to save in the background
        // println("Saved token: ${sharedPreferences.getString("auth_token", null)}")
    }

    // Function to retrieve the token
    fun getToken(): String? {
        return sharedPreferences.getString("auth_token", null)
    }

    // Function to clear the token (e.g., on logout)
    fun clearToken() {
        sharedPreferences.edit()
            .remove("auth_token")
            .apply()
    }
}