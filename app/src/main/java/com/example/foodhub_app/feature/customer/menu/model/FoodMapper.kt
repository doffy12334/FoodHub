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
        available = isAvailable,
        basePrice = basePrice.toDoubleOrNull()?.toLong() ?: priceLong,
        salePercent = salePercent,
        isBestSeller = isBestSeller,
    )
}

// Chuyển đổi FoodDto sang FoodDetail
fun FoodDto.toFoodDetail(): FoodDetail {
    val basePriceLong = basePrice.toDoubleOrNull()?.toLong() ?: 0L
    val salePriceLong = salePrice?.toDoubleOrNull()?.toLong()

    return FoodDetail(
        id = id,
        name = name,
        description = description ?: "",
        basePrice = basePriceLong,
        salePrice = salePriceLong,
        rating = avgRating,
        available = isAvailable,
        imageUrl = image,
        groups = variantGroups.map { group ->
            FoodOptionGroup(
                id = group.id,
                name = group.name,
                multiple = group.type == "MULTIPLE",
                required = group.isRequired,
                options = group.options.filter { it.isActive }.map { option ->
                    FoodOption(
                        id = option.id,
                        name = option.name,
                        priceAdd = option.priceAdd.toBigDecimal().longValueExact()
                    )
                }
            )
        },
        isBestSeller = isBestSeller,
        totalOrder = totalOrder
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
