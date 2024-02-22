package com.lockboxth.lockboxkiosk

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.lockboxth.lockboxkiosk.helpers.BaseActivity
import com.lockboxth.lockboxkiosk.helpers.Util
import com.lockboxth.lockboxkiosk.http.model.pudo.PudoAddBankRequest
import com.lockboxth.lockboxkiosk.http.model.pudo.PudoBankRequest
import com.lockboxth.lockboxkiosk.http.model.pudo.PudoKeyVal
import com.lockboxth.lockboxkiosk.http.repository.PudoRepository
import kotlinx.android.synthetic.main.activity_pudo_add_bank_account.*
import kotlinx.android.synthetic.main.activity_pudo_add_bank_account.btnCancel

class PudoAddBankAccountActivity : BaseActivity() {

    private var selectedBank: PudoKeyVal? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pudo_add_bank_account)

        autoHideKeyboard = false
        attachKeyboardListeners()

        selectBank.setOnClickListener {
            val intent = Intent(this@PudoAddBankAccountActivity, SelectedBankActivity::class.java)
            selectBankResultLauncher.launch(intent)
        }

        btnCancel.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }

        btnConfirm.setOnClickListener {
            if (validate()) {
                showProgressDialog()
                val req = PudoAddBankRequest(
                    appPref.kioskInfo!!.generalprofile_id,
                    appPref.currentTransactionId!!,
                    selectedBank!!.key,
                    etBankAccountCode.text.toString(),
                    etBankAccountName.text.toString()
                )
                PudoRepository.getInstance().addBank(
                    req,
                    onSuccess = { resp ->
                        PudoParcelDetailActivity.bankItems = resp.bank_account
                        PudoParcelDetailActivity.selectedBankAccount = resp.bank_account_default
                        hideProgressDialog()
                        finish()
                    },
                    onFailure = { error ->
                        hideProgressDialog()
                        showMessage(error.message!!)
                    }
                )
            } else {
                showMessage(getString(R.string.require_fill_in))
            }
        }

    }

    private fun validate(): Boolean {
        var valid = true
        if (selectedBank == null) {
            valid = false
        }
        if (etBankAccountCode.text.isNullOrEmpty()) {
            valid = false
        }
        if (etBankAccountName.text.isNullOrEmpty()) {
            valid = false
        }
        return valid
    }

    private fun setSelectedBank(parcelType: PudoKeyVal) {
        selectedBank = parcelType
        if (parcelType.key == 0) {
            tvBankName.setTextColor(ContextCompat.getColor(this, R.color.white_3))
        } else {
            tvBankName.setTextColor(ContextCompat.getColor(this, R.color.black))
        }
        tvBankName.text = parcelType.value
    }

    private var selectBankResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent = result.data!!
            setSelectedBank(data.getParcelableExtra("selected")!!)
        }
    }

    override fun onShowKeyboard(keyboardHeight: Int) {
        Util.fullscreen(this.window.decorView)
    }

    override fun onHideKeyboard() {
        Util.fullscreen(this.window.decorView)
    }


}