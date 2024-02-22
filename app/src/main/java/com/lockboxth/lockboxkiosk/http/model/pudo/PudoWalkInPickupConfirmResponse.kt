package com.lockboxth.lockboxkiosk.http.model.pudo

import android.os.Parcel
import android.os.Parcelable

data class PudoWalkInPickupConfirmResponse(
    val shipments: List<PudoShipmentItem>
)

