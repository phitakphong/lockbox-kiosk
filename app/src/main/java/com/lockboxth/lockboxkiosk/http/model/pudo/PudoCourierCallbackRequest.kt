package com.lockboxth.lockboxkiosk.http.model.pudo

import com.google.gson.JsonObject

data class PudoCourierCallbackRequest(
    val generalprofile_id: Int,
    val txn: String,
    val event_type: String,
    val locker_results: JsonObject
)
