package com.lockboxth.lockboxkiosk

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.lockboxth.lockboxkiosk.helpers.BaseActivity
import com.lockboxth.lockboxkiosk.helpers.PudoLocationPickupType
import com.lockboxth.lockboxkiosk.helpers.PudoLocationType
import com.lockboxth.lockboxkiosk.helpers.Util
import com.lockboxth.lockboxkiosk.http.model.pudo.PudoKeyVal
import com.lockboxth.lockboxkiosk.http.model.pudo.PudoSelectLocationTypeRequest
import com.lockboxth.lockboxkiosk.http.model.pudo.PudoSelectLocationTypeResponse
import com.lockboxth.lockboxkiosk.http.repository.PudoRepository
import kotlinx.android.synthetic.main.activity_pudo_pickup_point.*

class PudoPickupPointActivity : BaseActivity() {

    private var selectedLocationType: PudoLocationPickupType? = null
    private var addressData: PudoSelectLocationTypeResponse? = null
    private var selectedLockerLocation: PudoKeyVal? = null

    private var timeouted = false
    private var firstResume = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pudo_pickup_point)

        val addressId = intent.getIntExtra("address_id", 0)

        btnLocker.setOnClickListener {
            selectedLocationType = PudoLocationPickupType.LOCKER
            toggleType()
        }

        btnAnywhere.setOnClickListener {
            selectedLocationType = PudoLocationPickupType.ANYWHERE
            toggleType()
        }

        showProgressDialog()

        val req = PudoSelectLocationTypeRequest(
            appPref.kioskInfo!!.generalprofile_id,
            appPref.currentTransactionId!!,
            addressId
        )
        PudoRepository.getInstance().walkinReceiverSelect(
            req,
            onSuccess = { resp ->
                hideProgressDialog()
                addressData = resp
                bindUI()
            },
            onFailure = { error ->
                hideProgressDialog()
                showMessage(error.message!!)
            }
        )

        btnConfirm.setOnClickListener {
            if (selectedLocationType == null) {
                showMessage(getString(R.string.select_dest))
                return@setOnClickListener
            }
            if(selectedLocationType == PudoLocationPickupType.LOCKER && selectedLockerLocation == null){
                showMessage(getString(R.string.select_location))
                return@setOnClickListener
            }
            val intent = Intent(this@PudoPickupPointActivity, PudoSelectShipmentActivity::class.java)
            if (selectedLocationType == PudoLocationPickupType.LOCKER) {
                intent.putExtra("generalprofile_id", selectedLockerLocation!!.key)
            }
            intent.putExtra("type", selectedLocationType!!.toString().lowercase())
            startActivity(intent)
        }

        btnCancel.setOnClickListener {
            onCancel()
        }

        setTimeoutMinute(5, tvCountdown) {
            timeouted = true
            if (btnCancel != null) {
                onCancel()
            }
        }

    }

    @SuppressLint("SetTextI18n")
    private fun bindUI() {
        val lockerItem = addressData!!.pickup_service.find { d -> d.type == "locker" }
        val anywhereItem = addressData!!.pickup_service.find { d -> d.type == "anywhere" }
        if (lockerItem != null) {
            tvLockerDetail.text = TextUtils.join("\n", lockerItem.details.map { d ->
                "• $d"
            })
        }

        if (lockerItem != null) {
            val selectorItems = lockerItem.location.station_list!!.map { s -> PudoKeyVal(s.generalprofile_id, s.name, null) }
            lockerLocation.setOnClickListener {
                val intent = Intent(this@PudoPickupPointActivity, KeyValSelectorActivity::class.java)
                intent.putExtra("location_type", PudoLocationType.LOCKER_LOCATION)
                intent.putExtra("locker_location", ArrayList(selectorItems))
                selectLockerLocationResultLauncher.launch(intent)
            }
        }

        if (anywhereItem != null) {
            tvAnywhereDetail.text = TextUtils.join("\n", anywhereItem.details.map { d ->
                "• $d"
            })
            val address = anywhereItem.location.address_select!!
            tvAnywhere.text = "${address.full_name}, ${address.phone}, ${address.full_address}"
        }
        toggleType()
    }

    private var selectLockerLocationResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent = result.data!!
            selectedLockerLocation = data.getParcelableExtra("item")!!
            if (selectedLockerLocation == null || selectedLockerLocation?.key == 0) {
                tvLocker.setTextColor(ContextCompat.getColor(this, R.color.white_3))
                tvLocker.text = "เลือกสถานที่"
            } else {
                tvLocker.setTextColor(ContextCompat.getColor(this, R.color.black))
                tvLocker.text = selectedLockerLocation!!.value
            }
        }
    }

    private fun toggleType() {
        lockerLocation.visibility = View.GONE
        anywhereLocation.visibility = View.GONE
        btnLocker.background = ContextCompat.getDrawable(this@PudoPickupPointActivity, R.drawable.card_item_white_border_gray)
        btnAnywhere.background = ContextCompat.getDrawable(this@PudoPickupPointActivity, R.drawable.card_item_white_border_gray)
        when (selectedLocationType) {
            PudoLocationPickupType.LOCKER -> {
                btnLocker.background = ContextCompat.getDrawable(this@PudoPickupPointActivity, R.drawable.card_item_white_border_raduis_8)
                lockerLocation.visibility = View.VISIBLE
            }
            PudoLocationPickupType.ANYWHERE -> {
                btnAnywhere.background = ContextCompat.getDrawable(this@PudoPickupPointActivity, R.drawable.card_item_white_border_raduis_8)
                anywhereLocation.visibility = View.VISIBLE
            }
            else -> {}
        }
    }

    override fun onResume() {
        super.onResume()
        if (!firstResume) {
            timerResume()
        }
        firstResume = false
        if (timeouted) {
            onCancel()
            return
        }
        Util.fullscreen(this.window.decorView)
    }
}