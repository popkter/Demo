package com.popkter.collector.entity

import com.google.gson.annotations.SerializedName

data class ChatCompletionResponse(
    @SerializedName("id") val id: String,
    @SerializedName("object") val objectType: String,
    @SerializedName("created") val created: Long,
    @SerializedName("model") val model: String,
    @SerializedName("choices") val choices: List<Choice>,
    @SerializedName("usage") val usage: Usage,
    @SerializedName("system_fingerprint") val systemFingerprint: String
)


data class ChatCompletionChunk(
    val id: String,
    val `object`: String,
    val created: Long,
    val model: String,
    val system_fingerprint: String,
    val choices: List<Choice>,
    val usage: Usage?
) {
    data class Choice(
        val index: Int,
        val delta: Delta,
        val logprobs: Any?, // 可根据实际数据结构修改
        val finish_reason: String?
    ) {
        data class Delta(
            val content: String?
        )
    }

    data class Usage(
        val prompt_tokens: Int,
        val completion_tokens: Int,
        val total_tokens: Int,
        val prompt_cache_hit_tokens: Int,
        val prompt_cache_miss_tokens: Int
    )
}

data class Delta(
    @SerializedName("role") val role: String?,
    @SerializedName("content") val content: String?
)


data class Choice(
    @SerializedName("index") val index: Int,
    @SerializedName("message") val message: ChatMessage,
    @SerializedName("logprobs") val logprobs: Any?,
    @SerializedName("finish_reason") val finishReason: String
)

data class ChatMessage(
    @SerializedName("role") val role: String,
    @SerializedName("content") val content: String
)

data class Usage(
    @SerializedName("prompt_tokens") val promptTokens: Int,
    @SerializedName("completion_tokens") val completionTokens: Int,
    @SerializedName("total_tokens") val totalTokens: Int,
    @SerializedName("prompt_cache_hit_tokens") val promptCacheHitTokens: Int,
    @SerializedName("prompt_cache_miss_tokens") val promptCacheMissTokens: Int
)