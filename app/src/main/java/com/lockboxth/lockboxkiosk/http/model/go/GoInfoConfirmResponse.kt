package com.lockboxth.lockboxkiosk.http.model.go

import com.google.gson.JsonObject

data class GoInfoConfirmResponse(
    val is_paid: Boolean?,
    val txn: String?,
    val action: String?,
    val payment_status: Boolean?,
    val locker_no: String,
    val locker_commands: JsonObject?,
    val event_type: String?
)

