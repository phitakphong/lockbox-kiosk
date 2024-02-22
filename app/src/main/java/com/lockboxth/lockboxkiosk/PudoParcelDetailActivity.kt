package com.lockboxth.lockboxkiosk

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.gson.reflect.TypeToken
import com.lockboxth.lockboxkiosk.helpers.BaseActivity
import com.lockboxth.lockboxkiosk.helpers.Gsoner
import com.lockboxth.lockboxkiosk.helpers.Util
import com.lockboxth.lockboxkiosk.http.model.pudo.PudoBankAccount
import com.lockboxth.lockboxkiosk.http.model.pudo.PudoKeyVal
import com.lockboxth.lockboxkiosk.http.model.pudo.PudoWalkInParcelConfirmRequest
import com.lockboxth.lockboxkiosk.http.repository.PudoRepository
import kotlinx.android.synthetic.main.activity_pudo_parcel_detail.*


class PudoParcelDetailActivity : BaseActivity() {

    private var selectedParcelType: PudoKeyVal? = null
    private var selectedParcelSize: PudoKeyVal? = null
    var timeouted = false
    var firstResume = true

    companion object {
        var parcelTypeItems: List<PudoKeyVal> = listOf()
        var parcelSizeItems: List<PudoKeyVal> = listOf()
        var bankItems: List<PudoBankAccount> = listOf()
        var selectedBankAccount: PudoBankAccount? = null
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pudo_parcel_detail)

        autoHideKeyboard = false
        attachKeyboardListeners()

        setSelectedParcelType(parcelTypeItems.first())
        selectParcelType.setOnClickListener {
            val intent = Intent(this@PudoParcelDetailActivity, SelectParcelTypeActivity::class.java)
            selectParcelTypeResultLauncher.launch(intent)
        }

        setSelectedParcelSize(parcelSizeItems.first())
        selectParcelSize.setOnClickListener {
            val intent = Intent(this@PudoParcelDetailActivity, SelectParcelSizeActivity::class.java)
            selectParcelSizeResultLauncher.launch(intent)
        }

        selectBank.setOnClickListener {
            val intent = Intent(this@PudoParcelDetailActivity, SelectBankAccountActivity::class.java)
            startActivity(intent)
        }

        layoutCod.visibility = View.GONE

        chkCod.setOnCheckedChangeListener { _, b ->
            layoutCod.visibility = if (b) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }

        btnConfirm.setOnClickListener {
            if (validate()) {

                showProgressDialog()
                val req = PudoWalkInParcelConfirmRequest(
                    appPref.kioskInfo!!.generalprofile_id,
                    appPref.currentTransactionId ?: "",
                    selectedParcelType!!.key,
                    selectedParcelSize!!.key,
                    etWeight.text.toString().toFloat(),
                    chkCod.isChecked
                )

                if (selectedParcelSize?.value!!.lowercase() == "กำหนดเอง") {
                    req.width = etW.text.toString().toFloat()
                    req.length = etD.text.toString().toFloat()
                    req.height = etH.text.toString().toFloat()
                }
                if (chkCod.isChecked) {
                    req.bankacc_id = selectedBankAccount!!.bankacc_id
                    req.cod = etCodAmount.text.toString().toFloat()
                }
                PudoRepository.getInstance().walkInParcelConfirm(
                    req,
                    onSuccess = { resp ->
                        hideProgressDialog()
                        val intent = Intent(this@PudoParcelDetailActivity, PickupLocationActivity::class.java)
                        intent.putExtra("data", resp)
                        startActivity(intent)
                    },
                    onFailure = { error ->
                        hideProgressDialog()
                        showMessage(error.message!!)
                    }
                )

            }
        }

        btnCancel.setOnClickListener {
            onCancel()
        }

        setTimeoutMinute(10, tvCountdown) {
            timeouted = true
            if (etWeight != null) {
                onCancel()
            }
        }

    }


    private fun toggleCustomParcelSize() {
        if (selectedParcelSize?.value!!.lowercase() == "กำหนดเอง") {
            layoutCustomParcelSize.visibility = View.VISIBLE
        } else {
            layoutCustomParcelSize.visibility = View.GONE
        }
    }

    private fun validate(): Boolean {
        var valid = true
        if (selectedParcelType == null) {
            valid = false
        }

        if (selectedParcelSize == null) {
            valid = false
        } else if (selectedParcelSize?.value == "กำหนดเอง" && (etW.text.isNullOrEmpty() || etD.text.isNullOrEmpty() || etH.text.isNullOrEmpty())) {
            valid = false
        }
        if (etWeight.text.isNullOrEmpty()) {
            valid = false
        }

        if (chkCod.isChecked && (selectedBankAccount == null || etCodAmount.text.isNullOrEmpty())) {
            valid = false
        }

        if (!valid) {
            showMessage("กรุณากรอกข้อมูลให้ครบถ้วน")
        }
        return valid
    }

    private fun setSelectedParcelType(parcelType: PudoKeyVal) {
        selectedParcelType = parcelType
        if (parcelType.key == 0) {
            tvParcelType.setTextColor(ContextCompat.getColor(this, R.color.white_3))
        } else {
            tvParcelType.setTextColor(ContextCompat.getColor(this, R.color.black))
        }
        tvParcelType.text = selectedParcelType!!.value
    }

    private fun setSelectedParcelSize(parcelSize: PudoKeyVal) {
        selectedParcelSize = parcelSize
        if (parcelSize.key == 0) {
            tvParcelSize.setTextColor(ContextCompat.getColor(this, R.color.white_3))
        } else {
            tvParcelSize.setTextColor(ContextCompat.getColor(this, R.color.black))
        }
        toggleCustomParcelSize()
        tvParcelSize.text = selectedParcelSize!!.value
    }

    private var selectParcelTypeResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent = result.data!!
            setSelectedParcelType(data.getParcelableExtra("selected")!!)
        }
    }

    private var selectParcelSizeResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent = result.data!!
            setSelectedParcelSize(data.getParcelableExtra("selected")!!)
        }
    }

    override fun onShowKeyboard(keyboardHeight: Int) {
        Util.fullscreen(this.window.decorView)
    }

    override fun onHideKeyboard() {
        Util.fullscreen(this.window.decorView)
    }

    @SuppressLint("SetTextI18n")
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
        if (selectedBankAccount != null) {
            imgBank.visibility = View.VISIBLE
            Util.loadImage(selectedBankAccount!!.bank_image, imgBank)
            tvBankAccount.text = "${selectedBankAccount!!.bank_name}\n${selectedBankAccount!!.censor_account_id}"
            tvBankAccount.setTextColor(ContextCompat.getColor(this, R.color.black))
        } else {
            imgBank.visibility = View.GONE
            tvBankAccount.setTextColor(ContextCompat.getColor(this, R.color.white_3))
        }

        Util.fullscreen(this.window.decorView)

    }

    override fun onPause() {
        super.onPause()
        Util.fullscreen(this.window.decorView)
    }


}