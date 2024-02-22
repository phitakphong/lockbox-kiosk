package com.lockboxth.lockboxkiosk.http.model.personal

import com.google.gson.JsonObject

data class VerifyBookingFinishResponse(
    val event_type: String,
    val action: String,
    val transaction_id: String,
    val payment_status: Boolean,
    val locker_no: String,
    val money_change: Int,
    val locker_commands: JsonObject

)

