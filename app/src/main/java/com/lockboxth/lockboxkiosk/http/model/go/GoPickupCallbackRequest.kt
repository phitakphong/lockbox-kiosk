package com.lockboxth.lockboxkiosk.http.model.go

import com.google.gson.JsonObject

data class GoPickupCallbackRequest(
    val generalprofile_id: Int,
    val txn: String,
    val event_type: String = "go_pickup",
    val locker_results: JsonObject
)
