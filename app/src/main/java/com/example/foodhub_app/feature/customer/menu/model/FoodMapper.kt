package com.example.foodhub_app.feature.customer.menu.model

// Chuyển đổi FoodDto sang MenuFood
fun FoodDto.toMenuFood(): MenuFood {
    val priceString = salePrice ?: basePrice
    val priceLong = priceString.toDoubleOrNull()?.toLong() ?: 0L

    return MenuFood(
        id = id,
        name = name,
        price = priceLong,
        imageUrl = image,
        description = description ?: "",
        rating = avgRating,
        isFeatured = isFeatured,
        totalOrder = totalOrder,
        available = isAvailable
    )
}

// Chuyển đổi CategoryDto sang MenuCategory
fun CategoryDto.toMenuCategory(items: List<MenuFood> = emptyList()): MenuCategory {
    return MenuCategory(
        id = id,
        name = name,
        items = items
    )
}

// Chuyển đổi MenuCategoryDto sang MenuCategory
fun MenuCategoryDto.toMenuCategory(): MenuCategory {
    return MenuCategory(
        id = id,
        name = name,
        items = items.map { it.toMenuFood() }
    )
}
