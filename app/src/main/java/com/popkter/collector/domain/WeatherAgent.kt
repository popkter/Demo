package com.popkter.collector.domain

import android.os.Parcelable
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.popkter.network.client.HttpRequestExt
import kotlinx.parcelize.Parcelize

object WeatherAgent {

    private const val TAG = "WeatherAgent"

    private val gson = Gson()

    suspend fun loadWeatherData(
        query: String,
        onDaysData: (List<WeatherDaysInfo>) -> Unit = {},
        onHoursData: (List<WeatherHoursInfo>) -> Unit = {},
        onSummaryUpdate: (String) -> Unit = {}
    ) {
        HttpRequestExt.doPostStreamRequest(
            url = "http://124.221.124.238:10011/query_weather",
            requestBody =
            """
            {
                "user_query":"$query",
                "token":"1008611"
            }
             """.trimIndent(),
            contentType = "application/json"
        ) { _, line ->
            if (line.isNotBlank()) {
                val data = gson.fromJson(line, WeatherResponse::class.java)
                if (data.type == "weather_data_days") {
                    val type = object : TypeToken<List<WeatherDaysInfo>>() {}.type
                    val result: List<WeatherDaysInfo> = gson.fromJson(data.data, type)
                    onDaysData(result)
                }
                if (data.type == "weather_data_hours") {
                    val type = object : TypeToken<List<WeatherHoursInfo>>() {}.type
                    val result: List<WeatherHoursInfo> = gson.fromJson(data.data, type)
                    onHoursData(result)
                }
                if (data.type == "summary") {
                    onSummaryUpdate(decodeUnicode(data.data))
                }
            }
        }
    }

    fun decodeUnicode(input: String): String {
        return Regex("""\\u([0-9a-fA-F]{4})""").replace(input) { matchResult ->
            val codePoint = matchResult.groupValues[1].toInt(16)
            codePoint.toChar().toString()
        }
    }


    data class WeatherResponse(val type: String, val data: String)


    @Parcelize
    data class WeatherHoursInfo(
        val datetime: String,  // 时间
        val temp: Double,      // 温度
        val feelslike: Double, // 体感温度
        val windspeed: Double, // 风速
        val humidity: Double,  // 湿度
        val conditions: String, // 天气状况
        val uvindex: Double,   // 紫外线指数
        val visibility: Double // 能见度
    ) : Parcelable

    @Parcelize
    data class WeatherDaysInfo(
        val date: String,
        val temperature: Temperature,
        val windspeed: Double,
        val conditions: String,
        val humidity: Double
    ) : Parcelable

    @Parcelize
    data class Temperature(
        val max: Double,
        val min: Double,
        val avg: Double
    ) : Parcelable
}