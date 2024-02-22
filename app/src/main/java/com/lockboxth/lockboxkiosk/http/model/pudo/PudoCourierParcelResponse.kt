package com.lockboxth.lockboxkiosk.http.model.pudo

import com.google.gson.JsonObject

data class PudoCourierParcelResponse(
    val txn: String,
    val parcel_detail: PudoParcelAddressResponse,
    val locker_commands: JsonObject,
    val event_type: String
)




