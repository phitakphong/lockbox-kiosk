package com.lockboxth.lockboxkiosk.http.model.adidas

import com.google.gson.JsonObject

data class AdidasVerifyResponse(
    val txn: String,
    val locker_no: String,
    val locker_commands: JsonObject,
    val event_type: String
)

