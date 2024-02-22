package com.lockboxth.lockboxkiosk

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.lockboxth.lockboxkiosk.helpers.BaseActivity
import com.lockboxth.lockboxkiosk.helpers.PudoLocationType
import com.lockboxth.lockboxkiosk.helpers.Util
import com.lockboxth.lockboxkiosk.http.model.pudo.PudoKeyVal
import com.lockboxth.lockboxkiosk.http.model.pudo.PudoSubmitRecieverRequest
import com.lockboxth.lockboxkiosk.http.repository.PudoRepository
import kotlinx.android.synthetic.main.activity_pudo_add_new_receiver.*
import kotlinx.android.synthetic.main.activity_pudo_add_new_receiver.btnCancel
import kotlinx.android.synthetic.main.activity_pudo_add_new_receiver.btnConfirm
import kotlinx.android.synthetic.main.activity_pudo_add_new_receiver.imgCountryFlag
import kotlinx.android.synthetic.main.activity_pudo_add_new_receiver.tvDialCode

class PudoAddReceiverActivity : BaseActivity() {

    var dialCode: String = "+66"
    var countryCode: String = "th"

    private var selectedProvince: PudoKeyVal? = null
    private var selectedSubDistrict: PudoKeyVal? = null
    private var selectedDistrict: PudoKeyVal? = null

    private var currentSelectedLocationType = PudoLocationType.PROVINCE

    override fun onCreate(savedInstanceState: Bundle?) {
        autoHideKeyboard = false
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pudo_add_new_receiver)
        imgCountryFlag.setOnClickListener {
            val intent = Intent(this@PudoAddReceiverActivity, SearchCountryCodeActivity::class.java)
            intent.putExtra("dial_code", this.dialCode)
            phoneNumberResultLauncher.launch(intent)
        }

        selectProvince.setOnClickListener {
            currentSelectedLocationType = PudoLocationType.PROVINCE
            val intent = Intent(this@PudoAddReceiverActivity, KeyValSelectorActivity::class.java)
            intent.putExtra("location_type", PudoLocationType.PROVINCE)
            keyValResultLauncher.launch(intent)
        }

        selectDistrict.setOnClickListener {
            currentSelectedLocationType = PudoLocationType.DISTRICT
            if (selectedProvince == null) {
                showMessage(getString(R.string.select_province))
                return@setOnClickListener
            }
            val intent = Intent(this@PudoAddReceiverActivity, KeyValSelectorActivity::class.java)
            intent.putExtra("location_type", PudoLocationType.DISTRICT)
            intent.putExtra("parent_id", selectedProvince!!.key)
            keyValResultLauncher.launch(intent)
        }

        selectSubDistrict.setOnClickListener {
            currentSelectedLocationType = PudoLocationType.SUB_DISTRICT
            if (selectedDistrict == null) {
                showMessage(getString(R.string.select_dist))
                return@setOnClickListener
            }
            val intent = Intent(this@PudoAddReceiverActivity, KeyValSelectorActivity::class.java)
            intent.putExtra("location_type", PudoLocationType.SUB_DISTRICT)
            intent.putExtra("parent_id", selectedDistrict!!.key)
            keyValResultLauncher.launch(intent)
        }

        btnCancel.setOnClickListener {
            onBackPressed()
        }

        btnConfirm.setOnClickListener {
            if (validate()) {
                submit()
            } else {
                showMessage(getString(R.string.require_fill_in2))
            }
        }

    }

    private fun validate(): Boolean {
        if (etName.text.isNullOrEmpty()) {
            return false
        }
        if (etSurname.text.isNullOrEmpty()) {
            return false
        }
        if (etPhoneNo.text.isNullOrEmpty()) {
            return false
        }
        if (etAddress.text.isNullOrEmpty()) {
            return false
        }
        if (selectedProvince == null) {
            return false
        }
        if (selectedSubDistrict == null) {
            return false
        }
        if (selectedDistrict == null) {
            return false
        }
        return true
    }

    private fun submit() {
        showProgressDialog()
        val req = PudoSubmitRecieverRequest(
            appPref.kioskInfo!!.generalprofile_id,
            appPref.currentTransactionId!!,
            etName.text.toString(),
            etSurname.text.toString(),
            "${dialCode}${etPhoneNo.text}",
            etAddress.text.toString(),
            selectedProvince!!.key,
            selectedDistrict!!.key,
            selectedSubDistrict!!.key,
        )
        PudoRepository.getInstance().addReceiver(
            req,
            onSuccess = { resp ->
                hideProgressDialog()
                intent.putExtra("data", resp)
                setResult(RESULT_OK, intent)
                finish()
            },
            onFailure = { error ->
                hideProgressDialog()
                showMessage(error.message!!)
            }
        )
    }

    private var phoneNumberResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent = result.data!!
            dialCode = data.getStringExtra("dial_code")!!
            countryCode = data.getStringExtra("code")!!
            Util.loadCountryFlag(countryCode, imgCountryFlag)
            tvDialCode.text = dialCode
        }
    }

    private var keyValResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent = result.data!!
            val item = data.getParcelableExtra<PudoKeyVal>("item")!!
            when (currentSelectedLocationType) {
                PudoLocationType.PROVINCE -> setSelectedProvince(item)
                PudoLocationType.SUB_DISTRICT -> setSelectedSubDistrict(item)
                else -> setSelectedDistrict(item)
            }
        }
    }

    private fun setSelectedProvince(item: PudoKeyVal?) {
        selectedProvince = item
        if (item == null || item.key == 0) {
            tvProovince.setTextColor(ContextCompat.getColor(this, R.color.white_3))
            tvProovince.text = getString(R.string.province)
        } else {
            tvProovince.setTextColor(ContextCompat.getColor(this, R.color.black))
            tvProovince.text = selectedProvince!!.value
        }
        setSelectedSubDistrict(null)
        setSelectedDistrict(null)
    }

    private fun setSelectedSubDistrict(item: PudoKeyVal?) {
        selectedSubDistrict = item
        if (item == null || item.key == 0) {
            tvSubDistrict.setTextColor(ContextCompat.getColor(this, R.color.white_3))
            tvSubDistrict.text = getString(R.string.sub_dist)
        } else {
            tvSubDistrict.setTextColor(ContextCompat.getColor(this, R.color.black))
            tvSubDistrict.text = selectedSubDistrict!!.value
            etPostCde.setText(selectedSubDistrict!!.zip_code!!)
        }
    }

    private fun setSelectedDistrict(item: PudoKeyVal?) {
        selectedDistrict = item
        if (item == null || item.key == 0) {
            tvDistrict.setTextColor(ContextCompat.getColor(this, R.color.white_3))
            tvDistrict.text = getString(R.string.dist)
        } else {
            tvDistrict.setTextColor(ContextCompat.getColor(this, R.color.black))
            tvDistrict.text = selectedDistrict!!.value
        }
        setSelectedSubDistrict(null)
    }

    override fun onShowKeyboard(keyboardHeight: Int) {
        Util.fullscreen(this.window.decorView)
    }

    override fun onHideKeyboard() {
        Util.fullscreen(this.window.decorView)
    }
}