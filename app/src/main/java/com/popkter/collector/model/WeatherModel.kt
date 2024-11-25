package com.popkter.collector.model

import com.google.gson.Gson
import com.popkter.collector.constant.WEATHER_NEXT_7_DAYS
import com.popkter.network.client.HttpRequestExt
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow

class WeatherModel {

    companion object{
        const val TAG = "WeatherModel"
        val instance by lazy { WeatherModel() }
    }

    private val gson = Gson()

    suspend fun load7daysWeather(location: Pair<Double, Double> = 0.0 to 0.0): Pair<List<DaysWeather>, String> {
        val result = HttpRequestExt.doGetRequest<WeatherResponse>(
            WEATHER_NEXT_7_DAYS
        )

        val data = result?.days?.let { list ->
            list.map {
                mapOf(
                    "temp" to it.temp,
                    "windSpeed" to it.windspeed,
                    "humidity" to it.humidity,
                    "conditions" to it.conditions
                )
            }
        }

        val tts = DeepSeekModel.INSTANCE.summary7DaysWeather(gson.toJson(data))
        return (result?.days ?: emptyList() )to tts
    }

    suspend fun load7daysWeatherChunk(
        location: Pair<Double, Double> = 0.0 to 0.0,
        onChunkUpdate: suspend (String) -> Unit = {}
    ): List<DaysWeather> {
        val result = HttpRequestExt.doGetRequest<WeatherResponse>(
            WEATHER_NEXT_7_DAYS
        )

        val data = result?.days?.let { list ->
            list.map {
                mapOf(
                    "temp" to it.temp,
                    "windSpeed" to it.windspeed,
                    "humidity" to it.humidity,
                    "conditions" to it.conditions
                )
            }
        }
        DeepSeekModel.INSTANCE.summary7DaysWeather(gson.toJson(data), onChunkUpdate)
        return result?.days ?: emptyList()
    }

}

data class WeatherResponse(
    val queryCost: Int,
    val latitude: Double,
    val longitude: Double,
    val resolvedAddress: String,
    val address: String,
    val timezone: String,
    val tzoffset: Int,
    val days: List<DaysWeather>,
    val stations: Map<String, Station>
)

data class DaysWeather(
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