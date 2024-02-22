package com.lockboxth.lockboxkiosk

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.lockboxth.lockboxkiosk.helpers.BaseActivity
import com.lockboxth.lockboxkiosk.http.model.pudo.PudoPersonal
import com.lockboxth.lockboxkiosk.http.model.pudo.PudoVerifyIdCardRequest
import com.lockboxth.lockboxkiosk.http.model.pudo.PudoWalkInConsentRequest
import com.lockboxth.lockboxkiosk.http.repository.PudoRepository
import kotlinx.android.synthetic.main.activity_pudo_warning.*

class PudoWarningActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pudo_warning)

        btnCancel.setOnClickListener {
            onCancel()
        }

        val warningMessage = intent.getStringExtra("warning_message") ?: "-"
        tvWarningMessage.text = warningMessage


        btnConfirm.setOnClickListener {
            showProgressDialog()
            val req = PudoWalkInConsentRequest(appPref.kioskInfo!!.generalprofile_id, appPref.currentTransactionId!!)
            PudoRepository.getInstance().walkInConsent(
                req,
                onSuccess = { resp ->
                    hideProgressDialog()
                    val intent = Intent(this@PudoWarningActivity, PudoParcelDetailActivity::class.java)
                    PudoParcelDetailActivity.parcelTypeItems = resp.parcel_category
                    PudoParcelDetailActivity.parcelSizeItems = resp.parcel_size
                    PudoParcelDetailActivity.bankItems = resp.bank_account
                    PudoParcelDetailActivity.selectedBankAccount = resp.bank_account_default
                    startActivity(intent)

                },
                onFailure = { error ->
                    hideProgressDialog()
                    showMessage(error.message!!)
                }
            )
        }

        setTimeoutMinute(5, tvCountdown) {
            onCancel()
        }
    }
}