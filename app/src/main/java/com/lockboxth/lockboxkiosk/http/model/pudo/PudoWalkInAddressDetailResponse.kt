package com.lockboxth.lockboxkiosk.http.model.pudo


data class PudoWalkInAddressDetailResponse(
    val parcel_data: ParcelData,
    val drop_data: DropData,
    val pickup_data: PickupData,
    val shipment_data: ShipmentData,
    val fee_data: FeeData
)

data class ParcelData(
    val parcel_category_name: String,
    val parcel_size_full_name: String,
    val parcel_weight: Float,
    val width: Float,
    val height: Float,
    val length: Float,
    val parcel_dimession: Float,
    val has_cod: Boolean,
    val cod_amt: Float?,
)

data class DropData(
    val type: String,
    val station_name: String,
    val locker_no: List<String>,
    val address_data: AddressData
)

data class AddressData(
    val full_name: String,
    val phone: String,
    val idcard: String,
    val full_address: String,
    val district_name: String,
    val amphur_name: String,
    val province_name: String,
    val zip_code: Long,
)


data class PickupData(
    val type: String,
    val station_name: String?,
    val address_data: AddressData,
)



data class ShipmentData(
    val name: String,
    val image_url: String,
    val fee: Float
)

data class FeeData(
    val sum_total: Float,
    val amount_pay: Float,
    val details: List<Detail>,
)

data class Detail(
    val detail: String,
    val fee: Float,
)
