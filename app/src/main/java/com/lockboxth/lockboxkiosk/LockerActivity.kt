package com.lockboxth.lockboxkiosk

import android.content.Intent
import android.os.Bundle
import com.lockboxth.lockboxkiosk.helpers.BaseActivity
import com.lockboxth.lockboxkiosk.helpers.TransactionType
import kotlinx.android.synthetic.main.activity_locker.*

class LockerActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_locker)

        btnBack.setOnClickListener {
            onBackPressed()
        }

        val isAp = intent.getBooleanExtra("is_ap", false)
        if (isAp) {
            tvTitle.text = getString(R.string.ap_locker)
        }

        lockerIn.setOnClickListener {
            if (isAp) {
                appPref.currentTransactionType = TransactionType.GO_IN
                val intent = Intent(this@LockerActivity, RegisterActivity::class.java)
                startActivity(intent)
            } else {
                appPref.currentTransactionType = TransactionType.IN
                val intent = Intent(this@LockerActivity, SelectLockBoxActivity::class.java)
                startActivity(intent)
            }
            finish()


        }

        lockerOut.setOnClickListener {
            if (isAp) {
                appPref.currentTransactionType = TransactionType.GO_OUT
                val intent = Intent(this@LockerActivity, LockerPasswordActivity::class.java)
                startActivity(intent)
            } else {
                appPref.currentTransactionType = TransactionType.OUT
                val intent = Intent(this@LockerActivity, RegisterActivity::class.java)
                startActivity(intent)
            }

            finish()

        }

        setTimeoutSecond(15, null) {
            onBackPressed()
        }
    }
}