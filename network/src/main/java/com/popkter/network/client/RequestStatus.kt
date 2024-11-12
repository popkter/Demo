package com.popkter.network.client


sealed class RequestStatus {
    fun isSuccess(): Boolean {
        return this is SUCCESS
    }

    fun isError(): Boolean {
        return this is ERROR
    }

    fun isFinish(): Boolean {
        return this is FINISH
    }


    data object FINISH : RequestStatus()
    data object SUCCESS : RequestStatus()
    data object ERROR : RequestStatus()
}

