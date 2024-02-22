package com.lockboxth.lockboxkiosk.http

data class HttpResponse<T>(
    val status: Boolean,
    val code: String?,
    val message: String?,
    val info: T,
    val warning_message: String? = null
)
