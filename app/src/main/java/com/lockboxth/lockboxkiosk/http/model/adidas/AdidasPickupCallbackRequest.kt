package com.lockboxth.lockboxkiosk.http.model.adidas

import com.google.gson.JsonObject

data class AdidasPickupCallbackRequest(
    val generalprofile_id: Int,
    val txn: String,
    val event_type: String = "br_pickup",
    val locker_results: JsonObject
)




