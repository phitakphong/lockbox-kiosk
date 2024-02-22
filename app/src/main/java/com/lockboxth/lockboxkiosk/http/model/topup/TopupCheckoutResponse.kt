package com.lockboxth.lockboxkiosk.http.model.topup

data class TopupCheckoutResponse(
    val timer: Int?,
    val time_limit: Int?,
    val payment_method: String?,
    val payment_method_display: String?,
    val amount_pay: Float?,
    val payment_vendor: Map<String, Any>?,
    val action: String?,
    val locker_no: String?,
    val money_change: Int?,
    val has_slip: Boolean?
)
