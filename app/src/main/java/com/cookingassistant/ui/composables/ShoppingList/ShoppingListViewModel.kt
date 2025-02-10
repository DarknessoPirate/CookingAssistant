package com.cookingassistant.ui.composables.ShoppingList

import androidx.lifecycle.ViewModel
import com.cookingassistant.data.ShoppingProduct
import com.cookingassistant.data.ShoppingProducts
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ShoppingListViewModel : ViewModel() {
    private val _inputText = MutableStateFlow<String>("")
    val inputText : StateFlow<String> = _inputText

    private val _productList = MutableStateFlow<MutableList<ShoppingProduct>>(mutableListOf())
    val productList : StateFlow<MutableList<ShoppingProduct>> = _productList

    init {
        _productList.value = ShoppingProducts.getProducts().toMutableList()
    }

    fun onInputTextChanged(text : String) {
        _inputText.value = text
    }

    fun onProductChecked(product: ShoppingProduct) { //Ready to be removed
        ShoppingProducts.checkProduct(product, !product.isChecked)
        _productList.value = ShoppingProducts.getProducts().toMutableList()
    }

    fun onProductListAdd(text: String) {
        ShoppingProducts.addProduct(" ${text}")
        _inputText.value = ""
        _productList.value = ShoppingProducts.getProducts().toMutableList()
    }

    fun onProductListRemove(product: ShoppingProduct) {
        ShoppingProducts.removeProduct(product)
        _productList.value = ShoppingProducts.getProducts().toMutableList()
    }
}