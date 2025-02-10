package com.cookingassistant.data.DTO

data class ReviewPostDTO(
    val value: Int,
    val description: String?
)

data class ReviewGetDTO(
    val id: Int,
    var value: Int,
    var description: String?,
    val reviewAuthor: String?,
    val dateCreated: String,
    var dateModified: String?
)