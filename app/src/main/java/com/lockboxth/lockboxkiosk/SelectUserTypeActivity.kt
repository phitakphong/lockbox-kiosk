package com.lockboxth.lockboxkiosk

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.lockboxth.lockboxkiosk.helpers.BaseActivity
import com.lockboxth.lockboxkiosk.helpers.ParcelType
import com.lockboxth.lockboxkiosk.helpers.TransactionType
import kotlinx.android.synthetic.main.activity_select_user_type.*

class SelectUserTypeActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_user_type)


        val menu = appPref.currentConfig!!
        if (menu.find { m -> m.id == 2 } == null) {
            btnUser.visibility = View.GONE
            btnOfficer.visibility = View.GONE
        }
        if (menu.find { m -> m.id == 4 } == null) {
            btnPartner.visibility = View.GONE
        }
        if (menu.find { m -> m.id == 7 } == null) {
            btnAdidas.visibility = View.GONE
        }
        if (appPref.currentParcelType == ParcelType.SENDER) {

            tvTitle.text = getString(R.string.pudo_page_send)
            tvAdidas.text = getString(R.string.return_adidas)

            btnUser.setOnClickListener {
                val intent = Intent(this@SelectUserTypeActivity, PudoSenderSelectTypeActivity::class.java)
                startActivity(intent)
            }

            btnOfficer.setOnClickListener {
                appPref.currentTransactionType = TransactionType.PUDO_COURIER_SENDER
                val intent = Intent(this@SelectUserTypeActivity, QrCodeScannerActivity::class.java)
                startActivity(intent)
            }

            btnPartner.setOnClickListener {
                appPref.currentTransactionType = TransactionType.PUDO_CP_DROP
                val intent = Intent(this@SelectUserTypeActivity, QrCodeScannerActivity::class.java)
                startActivity(intent)
            }

            btnAdidas.setOnClickListener {
                appPref.currentTransactionType = TransactionType.ADIDAS_DROP
                val intent = Intent(this@SelectUserTypeActivity, LockerPasswordActivity::class.java)
                startActivity(intent)
            }

        } else if (appPref.currentParcelType == ParcelType.RECEIVER) {

            tvLazada.text = getString(R.string.online_shop)
            tvTitle.text = getString(R.string.pudo_page_recieve)
            tvAdidas.text = getString(R.string.get_adidas)

            btnUser.setOnClickListener {
                appPref.currentTransactionType = TransactionType.PUDO_RECEIVER
                val intent = Intent(this@SelectUserTypeActivity, PudoVerifyTypeActivity::class.java)
                startActivity(intent)
            }

            btnOfficer.setOnClickListener {
                appPref.currentTransactionType = TransactionType.PUDO_COURIER_PICKUP
                val intent = Intent(this@SelectUserTypeActivity, PudoSelectShipmentActivity::class.java)
                startActivity(intent)
            }

            btnPartner.setOnClickListener {
                appPref.currentTransactionType = TransactionType.PUDO_CP_PICKUP
                val intent = Intent(this@SelectUserTypeActivity, LockerPasswordActivity::class.java)
                startActivity(intent)
            }

            btnAdidas.setOnClickListener {
                appPref.currentTransactionType = TransactionType.ADIDAS_PICKUP
                val intent = Intent(this@SelectUserTypeActivity, LockerPasswordActivity::class.java)
                startActivity(intent)
            }

        }

        btnBack.setOnClickListener {
            onBackPressed()
        }

        setTimeoutSecond(15, null) {
            onCancel()
        }

    }
}