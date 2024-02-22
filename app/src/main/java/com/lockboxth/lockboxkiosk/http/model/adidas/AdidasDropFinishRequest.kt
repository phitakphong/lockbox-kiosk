package com.lockboxth.lockboxkiosk.http.model.adidas

import com.google.gson.JsonObject

data class AdidasDropFinishRequest(
    val generalprofile_id: Int,
    val txn: String,
    val submit_type: String
)




