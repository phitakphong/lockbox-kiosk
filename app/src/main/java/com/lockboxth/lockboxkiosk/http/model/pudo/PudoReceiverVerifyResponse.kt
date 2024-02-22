package com.lockboxth.lockboxkiosk.http.model.pudo

import android.os.Parcel
import android.os.Parcelable

data class PudoReceiverVerifyResponse(
    val txn: String,
    val has_pdpa: Boolean,
    val order: PudoParcelAddressResponse
)

