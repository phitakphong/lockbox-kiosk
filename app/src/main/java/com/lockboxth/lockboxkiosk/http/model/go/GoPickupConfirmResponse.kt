package com.lockboxth.lockboxkiosk.http.model.go

import com.google.gson.JsonObject
import com.lockboxth.lockboxkiosk.http.model.payment.PaymentMethodResponse

data class GoPickupConfirmResponse(
    val is_paid: Boolean?,
    val event_type: String?,
    val action: String?,
    val txn: String?,
    val payment_status: Boolean?,
    val locker_no: String?,
    val locker_commands: JsonObject?,

    val amount_pay: Float?,
    val payment_method: ArrayList<PaymentMethodResponse>?
)

