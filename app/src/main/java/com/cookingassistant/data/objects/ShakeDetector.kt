package com.cookingassistant.data.objects

import androidx.compose.runtime.mutableStateOf

object ShakeDetector {
    var detectedShake = mutableStateOf<Boolean>(false)
}