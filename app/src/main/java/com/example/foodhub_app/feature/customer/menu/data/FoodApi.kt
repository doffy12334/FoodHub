package com.example.foodhub_app.feature.customer.menu.data

import com.example.foodhub_app.core.network.ApiResponse
import com.example.foodhub_app.feature.customer.menu.model.CategoryDto
import com.example.foodhub_app.feature.customer.menu.model.FoodDto
import com.example.foodhub_app.feature.customer.menu.model.MenuCategoryDto
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path


//Khai bao Api
interface FoodApi {
    @GET("menu/all")
    suspend fun getMenu() : ApiResponse<List<MenuCategoryDto>>

    @GET("menu/categories")
    suspend fun getCategories() : ApiResponse<List<CategoryDto>>

    @GET("menu/item/{id}")
    suspend fun getFood(
        @Path("id") id: String
    ) : ApiResponse<FoodDto>
}