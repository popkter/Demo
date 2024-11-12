package com.senseauto.basiclibrary.entity


data class Poi(
    val cover: String,
    val name: String,
    val type: String,
    val score: Int,
    val distance: Int,
    val address: String,
    val latitude: Double,
    val longitude: Double
)
