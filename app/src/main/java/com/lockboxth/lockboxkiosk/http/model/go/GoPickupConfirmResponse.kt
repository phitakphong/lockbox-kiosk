package com.lockboxth.lockboxkiosk.http.model.go

import com.google.gson.JsonObject

data class GoPickupConfirmResponse(
    val is_paid: Boolean,
    val event_type: String,
    val action: String,
    val txn: String,
    val payment_status: Boolean,
    val locker_no: String,
    val locker_commands: JsonObject,
)

