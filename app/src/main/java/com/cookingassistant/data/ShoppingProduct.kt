package com.cookingassistant.data

import android.content.Context
import android.util.Log
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import java.io.FileNotFoundException

@Serializable
data class ShoppingProduct (
    val id : UInt,
    val text : String,
    val isChecked : Boolean,
) {
    fun isEqual(other: String): Boolean
    {
        return text == other
    }
}

object ShoppingProducts {
    private var _products : MutableList<ShoppingProduct> = mutableListOf()
    private var _lastId = _products.size
    fun addProduct(text : String) {
        _products.add( ShoppingProduct( (_lastId+1).toUInt(), text,false ) )
    }
    fun getProducts() : List<ShoppingProduct> {
        _products.sortBy { product -> product.isChecked }
        return _products
    }
    fun checkProduct(product: ShoppingProduct, isChecked: Boolean) {
        val old = _products[_products.indexOf(product)]
        _products[_products.indexOf(product)] = ShoppingProduct(old.id,old.text,isChecked)
    }
    fun removeProduct(product: ShoppingProduct) {
        _products.remove(product)
    }
    fun loadProducts(context: Context) {
        try {
            _products = Json.decodeFromStream<MutableList<ShoppingProduct>>(
                context.openFileInput("shopping_products.json")
            )
        } catch (e : FileNotFoundException) {
            Log.e("ShoppingProducts", "file doesn't exist")
        }
    }
    fun saveProducts(context: Context) {
        val jsonString = Json.encodeToString(_products)
        val filename = "shopping_products.json"
        context.openFileOutput(filename, Context.MODE_PRIVATE).use {
            it.write(jsonString.toByteArray())
        }
    }
    fun isInList(productText : String): Boolean {
        for(product in _products) {
            if(product.isEqual(productText)) {
                return true
            }
        }
        return false
    }
    fun addNotPresent(productText: String) {
        if(!isInList(productText))
            addProduct(productText)
    }
    fun removePresent(productText: String) {
        for(product in _products) {
            if(product.isEqual(productText)) {
                removeProduct(product)
                break
            }
        }
    }
    fun changePresense(productText: String) {
        var needAdd = true
        for(product in _products) {
            if(product.isEqual(productText)) {
                removeProduct(product)
                needAdd = false
                break
            }
        }
        if(needAdd)
            addProduct(productText)
    }
}