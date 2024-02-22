package com.lockboxth.lockboxkiosk.http.model.cp

import com.google.gson.JsonObject

data class CpVerifyResponse(
    val txn: String,
    val event_type: String,
    val locker_no: String,
    val locker_commands: JsonObject
)
