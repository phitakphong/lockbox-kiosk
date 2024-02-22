package com.lockboxth.lockboxkiosk.http.model.go

import com.google.gson.JsonObject

data class GoDropCallbackRequest(
    val generalprofile_id: Int,
    val txn: String,
    val event_type: String = "go_drop",
    val locker_results: JsonObject
)
