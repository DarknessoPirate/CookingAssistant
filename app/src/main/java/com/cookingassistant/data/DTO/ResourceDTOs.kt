package com.cookingassistant.data.DTO

abstract class idNameClassType {
    abstract val id:Int
    abstract val name: String
}

data class DifficultiesGetDTO(
    override val id: Int,
    override val name: String
) : idNameClassType()

data class CategoriesGetDTO(
    override val id: Int,
    override val name: String
) : idNameClassType()

data class OccasionsGetDTO(
    override val id: Int,
    override val name: String
): idNameClassType()