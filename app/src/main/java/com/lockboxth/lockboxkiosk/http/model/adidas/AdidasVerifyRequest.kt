package com.lockboxth.lockboxkiosk.http.model.adidas

data class AdidasVerifyRequest(
    val generalprofile_id: Int,
    val pin: String,
    val screenshot: String,
    val lang: String,
)




