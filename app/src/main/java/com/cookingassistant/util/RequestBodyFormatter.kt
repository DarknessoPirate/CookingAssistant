package com.cookingassistant.util
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody


class RequestBodyFormatter {
    // Convert a single string to RequestBody
    fun fromString(value: String?): RequestBody {
        return value.orEmpty().toRequestBody("text/plain".toMediaTypeOrNull())
    }

    // Convert integer values to RequestBody
    fun fromInt(value: Int?): RequestBody {
        return (value ?: 0).toString().toRequestBody("text/plain".toMediaTypeOrNull())
    }

    // Convert byte array to RequestBody for images
    fun fromByteArray(data: ByteArray?, fileName: String): MultipartBody.Part? {
        return data?.let {
            val requestBody = it.toRequestBody("image/jpeg".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("ImageData", fileName, requestBody)
        }
    }

    // Convert list to Multipart parts
    fun fromList(key: String, items: List<String?>): List<MultipartBody.Part> {
        return items.mapIndexed { index, value ->
            MultipartBody.Part.createFormData("$key[$index]", value.orEmpty())
        }
    }

}