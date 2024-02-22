package com.lockboxth.lockboxkiosk

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.lockboxth.lockboxkiosk.helpers.BaseActivity
import com.lockboxth.lockboxkiosk.helpers.TransactionType
import kotlinx.android.synthetic.main.activity_pudo_sender_select_type.*

class PudoSenderSelectTypeActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pudo_sender_select_type)

        btnNow.setOnClickListener {
            appPref.currentTransactionType = TransactionType.PUDO_SENDER
            val intent = Intent(this@PudoSenderSelectTypeActivity, PudoVerifyTypeActivity::class.java)
            startActivity(intent)
        }

        btnWalkin.setOnClickListener {
            appPref.currentTransactionType = TransactionType.PUDO_SENDER_WALKIN
            val intent = Intent(this@PudoSenderSelectTypeActivity, RegisterActivity::class.java)
            startActivity(intent)
        }

        btnBack.setOnClickListener {
            onBackPressed()
        }

        setTimeoutSecond(15, null) {
            onCancel()
        }
    }
}