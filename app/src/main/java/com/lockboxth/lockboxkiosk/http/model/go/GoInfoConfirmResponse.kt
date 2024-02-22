package com.lockboxth.lockboxkiosk.http.model.go

import com.google.gson.JsonObject

data class GoInfoConfirmResponse(
    val txn: String,
    val locker_no: String,
    val locker_commands: JsonObject,
    val event_type: String
)

