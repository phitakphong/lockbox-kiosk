package com.lockboxth.lockboxkiosk.http.model.locker

import com.google.gson.JsonObject

data class LockerCheckoutResponse(
    val timer: Int?,
    val time_limit: Int?,
    val payment_method: String?,
    val payment_method_display: String?,
    val amount_pay: Float?,
    val payment_vendor: Map<String, Any>?,
    val action: String?,
    val locker_no: String?,
    val money_change: Int?,
    val has_slip: Boolean?,
    val locker_commands: JsonObject?,
    val event_type: String?,
)
