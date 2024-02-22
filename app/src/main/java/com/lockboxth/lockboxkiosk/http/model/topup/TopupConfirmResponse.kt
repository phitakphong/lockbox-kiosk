package com.lockboxth.lockboxkiosk.http.model.topup

data class TopupConfirmResponse(
    val amount: Float,
    val payment_access: ArrayList<TopupMethod>
)

data class TopupMethod(
    val name: String,
    val value: String,
    val extra_amount: String,
    val extra_unit: String,
    val img_dat: String,
)
