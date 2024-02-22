package com.lockboxth.lockboxkiosk.http.model.pudo

import com.google.gson.JsonObject

data class PudoCourierCallbackResponse(
    val event_type: String?,
    val locker_commands: JsonObject?,
    val locker_no: String?
)

