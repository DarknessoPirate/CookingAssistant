package com.cookingassistant.util
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException


class ImageConverter() {
    companion object{
        fun imageToByteArray(imagePath: String):ByteArray?{
            try
            {
                val file = File(imagePath)

                if (file.exists()) {
                    println("Image found: ${file.absolutePath}")
                    val inputStream = FileInputStream(file)
                    return inputStream.readBytes() // reads the file into a ByteArray
                } else {
                    println("Image not found: $imagePath")
                    return null
                }
            } catch (e: IOException)
            {
                e.printStackTrace()
                return null
            }
        }

        fun byteArrayToBitmap(byteArray: ByteArray): Bitmap?{
            return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        }

        fun uriToBitmap(context: Context, uri: Uri): Bitmap? {
            return if (Build.VERSION.SDK_INT >= 28) {
                val source = ImageDecoder.createSource(context.contentResolver, uri)
                ImageDecoder.decodeBitmap(source)
            } else {
                @Suppress("DEPRECATION")
                MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            }
        }

        fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            return stream.toByteArray()
        }
    }
}