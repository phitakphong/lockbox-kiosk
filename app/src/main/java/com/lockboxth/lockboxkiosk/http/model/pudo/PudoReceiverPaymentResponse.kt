package com.lockboxth.lockboxkiosk.http.model.pudo

import android.os.Parcel
import android.os.Parcelable
import com.lockboxth.lockboxkiosk.http.model.payment.PaymentMethodResponse

data class PudoReceiverPaymentResponse(
    val amount_pay: Float,
    val payment_method: ArrayList<PaymentMethodResponse>
)

