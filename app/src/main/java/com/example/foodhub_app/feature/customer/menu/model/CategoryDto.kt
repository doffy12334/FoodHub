package com.example.foodhub_app.feature.customer.menu.model

import kotlinx.serialization.Serializable

//Nhan json danh muc tu server
@Serializable
data class CategoryDto(
    val id : String,
    val name : String,
    val image : String? = null,
    val sortOrder: Int
){

}