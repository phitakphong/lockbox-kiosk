package com.lockboxth.lockboxkiosk.http.model.cp

data class CpParcelRequest(
    val generalprofile_id: Int,
    val qrcode: String,
    val screenshot: String,
    val lang: String,
)




