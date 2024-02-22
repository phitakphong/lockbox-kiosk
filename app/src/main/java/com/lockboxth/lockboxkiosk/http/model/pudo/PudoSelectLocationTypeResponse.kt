package com.lockboxth.lockboxkiosk.http.model.pudo

import android.os.Parcel
import android.os.Parcelable

data class PudoSelectLocationTypeResponse(
    val pickup_service: ArrayList<PudoPickupService>
)


data class PudoPickupService(
    val type: String,
    val details: ArrayList<String>,
    val location: PudoPickupLocation
)


data class PudoPickupLocation(
    val station_list: ArrayList<PudoPickupStation>?,
    val address_select: PudoPickupAddress?
)

data class PudoPickupStation(
    val generalprofile_id: Int,
    val name: String
)

data class PudoPickupAddress(
    val full_name: String,
    val phone: String,
    val full_address: String
)