package com.senseauto.basiclibrary.entity

data class WeatherResponse(
    val queryCost: Int,
    val latitude: Double,
    val longitude: Double,
    val resolvedAddress: String,
    val address: String,
    val timezone: String,
    val tzoffset: Int,
    val days: List<Day>,
    val stations: Map<String, Station>
)

data class Day(
    val datetime: String,
    val datetimeEpoch: Long,
    val tempmax: Double,
    val tempmin: Double,
    val temp: Double,
    val feelslikemax: Double,
    val feelslikemin: Double,
    val feelslike: Double,
    val dew: Double,
    val humidity: Double,
    val precip: Double,
    val precipprob: Double,
    val precipcover: Double,
    val preciptype: Any?,
    val snow: Double,
    val snowdepth: Double,
    val windgust: Double,
    val windspeed: Double,
    val winddir: Double,
    val pressure: Double,
    val cloudcover: Double,
    val visibility: Double,
    val solarradiation: Double,
    val solarenergy: Double,
    val uvindex: Int,
    val severerisk: Int,
    val sunrise: String,
    val sunriseEpoch: Long,
    val sunset: String,
    val sunsetEpoch: Long,
    val moonphase: Double,
    val conditions: String,
    val description: String,
    val icon: String,
    val stations: List<String>,
    val source: String
)

data class Station(
    val distance: Int,
    val latitude: Double,
    val longitude: Double,
    val useCount: Int,
    val id: String,
    val name: String,
    val quality: Int,
    val contribution: Double
)