package com.lockboxth.lockboxkiosk.http.model.cp

import com.google.gson.JsonObject

data class CpParcelResponse(
    val txn: String,
    val locker_no: String,
    val locker_commands: JsonObject,
    val event_type: String
)

