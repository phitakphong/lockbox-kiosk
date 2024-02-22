package com.lockboxth.lockboxkiosk

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.lockboxth.lockboxkiosk.helpers.BaseActivity
import com.lockboxth.lockboxkiosk.helpers.Util
import com.lockboxth.lockboxkiosk.helpers.Util.Companion.firstCharUpper
import com.lockboxth.lockboxkiosk.http.model.pudo.PudoWalkInAddressDetailResponse
import com.lockboxth.lockboxkiosk.http.model.pudo.PudoWalkInShipmentSelectRequest
import com.lockboxth.lockboxkiosk.http.repository.PudoRepository
import kotlinx.android.synthetic.main.activity_pudo_walk_in_address_detail.*
import kotlinx.android.synthetic.main.activity_pudo_walk_in_address_detail.btnCancel
import kotlinx.android.synthetic.main.activity_pudo_walk_in_address_detail.imgShipmentLogo
import kotlinx.android.synthetic.main.activity_pudo_walk_in_address_detail.layoutCod
import kotlinx.android.synthetic.main.activity_pudo_walk_in_address_detail.tvCodAmt
import kotlinx.android.synthetic.main.activity_pudo_walk_in_address_detail.tvCountdown
import kotlinx.android.synthetic.main.activity_pudo_walk_in_address_detail.tvDropOff
import kotlinx.android.synthetic.main.activity_pudo_walk_in_address_detail.tvParcelCategory
import kotlinx.android.synthetic.main.activity_pudo_walk_in_address_detail.tvParcelSize
import kotlinx.android.synthetic.main.activity_pudo_walk_in_address_detail.tvParcelWeight
import kotlinx.android.synthetic.main.activity_pudo_walk_in_address_detail.tvPickup

class PudoWalkInAddressDetailActivity : BaseActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pudo_walk_in_address_detail)


        showProgressDialog()
        val shipmentId = intent.getIntExtra("shipment_id", 0)
        PudoRepository.getInstance().walkinShipmentSelect(
            PudoWalkInShipmentSelectRequest(appPref.kioskInfo!!.generalprofile_id, appPref.currentTransactionId!!, shipmentId),
            onSuccess = { resp ->
                bindUI(shipmentId, resp)
                hideProgressDialog()
            },
            onFailure = { error ->
                hideProgressDialog()
                showMessage(error.message!!)
                finish()
            }
        )

        setTimeoutMinute(5, tvCountdown) {
            onCancel()
        }

        btnCancel.setOnClickListener {
            onCancel()
        }

        btnConfirm.setOnClickListener {
            val intent = Intent(this@PudoWalkInAddressDetailActivity, PudoWalkinWaybillActivity::class.java)
            startActivity(intent)
            timerResume()
            finish()
        }

        setTimeoutMinute(5, tvCountdown) {
            try {
                onCancel()
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

    }

    @SuppressLint("SetTextI18n")
    private fun bindUI(shipmentId: Int, address: PudoWalkInAddressDetailResponse) {
        if (address.parcel_data.has_cod) {
            layoutCod.visibility = View.VISIBLE
            tvCodAmt.text = Util.formatMoney(address.parcel_data.cod_amt!!)
        } else {
            layoutShiping.visibility = View.VISIBLE
            tvShipingAmt.text = Util.formatMoney(address.fee_data.sum_total)
        }
        tvDropOff.text = "${address.drop_data.type.firstCharUpper()}\n${address.drop_data.station_name}"
        if (address.pickup_data.type == "anywhere") {
            imgPickup.setImageResource(R.drawable.ic_marker)
            tvPickup.text = address.pickup_data.type.firstCharUpper()
        } else {
            tvPickup.text = "${address.pickup_data.type.firstCharUpper()}\n${address.pickup_data.station_name}"
        }
        tvParcelCategory.text = address.parcel_data.parcel_category_name
        tvParcelSize.text = address.parcel_data.parcel_size_full_name
        tvParcelWeight.text = "${Util.formatMoney(address.parcel_data.parcel_weight)} ${getString(R.string.kg)}"

        Util.loadImage(address.shipment_data.image_url, imgShipmentLogo)

        tvSenderName.text = address.drop_data.address_data.full_name
        tvSenderTelNo.text = address.drop_data.address_data.phone
        var senderAddress = ""
        if (!address.drop_data.address_data.idcard.isNullOrEmpty()) {
            senderAddress = address.drop_data.address_data.idcard
            senderAddress += "\r\n"
        }
        senderAddress += address.drop_data.address_data.full_address
        tvSenderAddress.text = senderAddress

        tvReceiverName.text = address.pickup_data.address_data.full_name
        tvReceiverTelNo.text = address.pickup_data.address_data.phone
        tvReceiverAddress.text = address.pickup_data.address_data.full_address
    }


}