package com.cookingassistant.data.Models

data class ApiErrorResponse(
    val type: String,
    val title: String,
    val status: Int,
    val errors: Map<String, List<String>>,
    val traceId: String
)