package com.example.foodhub_app.feature.customer.menu.data

import com.example.foodhub_app.feature.customer.menu.model.CategoryDto
import com.example.foodhub_app.feature.customer.menu.model.FoodDto
import com.example.foodhub_app.feature.customer.menu.model.MenuCategory
import com.example.foodhub_app.feature.customer.menu.model.MenuCategoryDto
import com.example.foodhub_app.feature.customer.menu.model.toMenuCategory
import javax.inject.Inject


//Repository lay data
class FoodRepository @Inject constructor(
    private val foodApi: FoodApi
) {
    suspend fun getMenu(): Result<List<MenuCategory>> {
        return runCatching {
            val response = foodApi.getMenu()
            val dto = response.data ?: emptyList()
            dto.map { it.toMenuCategory() }
        }
    }

    suspend fun getCategories(): Result<List<CategoryDto>> {
        return runCatching {
            val response = foodApi.getCategories()
            response.data ?: emptyList()
        }
    }

    suspend fun getFood(id: String): Result<FoodDto?> {
        return runCatching {
            val response = foodApi.getFood(id)
            response.data
        }
    }

}