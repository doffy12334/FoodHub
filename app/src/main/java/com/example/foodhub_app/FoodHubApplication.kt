package com.example.foodhub_app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp //khởi tạo hệ thống dependency của Hilt cho ứng dụng
class FoodHubApplication : Application() {
}