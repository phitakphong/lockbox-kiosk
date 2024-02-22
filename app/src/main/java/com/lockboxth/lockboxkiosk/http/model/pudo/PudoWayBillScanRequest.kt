package com.lockboxth.lockboxkiosk.http.model.pudo

data class PudoWayBillScanRequest(
    val generalprofile_id: Int,
    val txn: String,
    val qrcode: String,
    val screenshot: String? = null,
)

