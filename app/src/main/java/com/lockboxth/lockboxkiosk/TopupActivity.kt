package com.lockboxth.lockboxkiosk

import android.content.Intent
import android.os.Bundle
import com.lockboxth.lockboxkiosk.helpers.BaseActivity
import com.lockboxth.lockboxkiosk.helpers.NumberTextWatcherForThousand
import com.lockboxth.lockboxkiosk.http.model.topup.TopupVerifyMember
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_topup.*
import kotlinx.android.synthetic.main.activity_topup.btnCancel
import kotlinx.android.synthetic.main.activity_topup.btnConfirm
import kotlinx.android.synthetic.main.activity_topup.tvCountdown

class TopupActivity : BaseActivity() {

    private var totalTopup = 0

    private var isResume = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_topup)

        autoHideKeyboard = false

        val member = intent.getParcelableExtra<TopupVerifyMember>("member")!!
        if (!member.name.isNullOrEmpty()) {
            tvName.text = member.name
        }
        if (!member.email.isNullOrEmpty()) {
            tvEmail.text = member.email
        }
        if (!member.phone.isNullOrEmpty()) {
            tvPhoneNumber.text = member.phone
        }

        displayText()

        btn100.setOnClickListener {
            add(100)
        }
        btn500.setOnClickListener {
            add(500)
        }
        btn700.setOnClickListener {
            add(700)
        }
        btn1000.setOnClickListener {
            add(1000)
        }
        btn1200.setOnClickListener {
            add(1200)
        }
        btn1500.setOnClickListener {
            add(1500)
        }

        etTotal.addTextChangedListener(NumberTextWatcherForThousand(etTotal))

        btnClear.setOnClickListener {
            totalTopup = 0
            displayText()
        }

        btnCancel.setOnClickListener {
            onCancel()
        }

        btnConfirm.setOnClickListener {
            if(etTotal.text.isNotEmpty()){
                totalTopup = NumberTextWatcherForThousand.trimCommaOfString(etTotal.text.toString()).toInt()
            }
            if (totalTopup < 100) {
                showMessage(getString(R.string.minimum_topup))
                return@setOnClickListener
            }
            val intent = Intent(this@TopupActivity, TopupMethodActivity::class.java)
            intent.putExtra("total", totalTopup)
            startActivity(intent)
        }

    }

    private fun add(total: Int) {
        if(etTotal.text.toString().isNotEmpty()){
            totalTopup = NumberTextWatcherForThousand.trimCommaOfString(etTotal.text.toString()).toInt()
        }
        totalTopup += total
        displayText()
    }

    private fun displayText() {
        etTotal.setText(totalTopup.toString())
    }

    override fun onResume() {
        super.onResume()
        setTimeoutMinute(5, tvCountdown) {
            onCancel()
        }
    }

}