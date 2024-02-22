package com.lockboxth.lockboxkiosk

import android.content.Intent
import android.os.Bundle
import com.lockboxth.lockboxkiosk.helpers.BaseActivity
import com.lockboxth.lockboxkiosk.helpers.TransactionType
import com.lockboxth.lockboxkiosk.http.repository.PersonalRepository
import kotlinx.android.synthetic.main.activity_consent.*

class ConsentActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_consent)

        val lang = "th"

        btnCancel.setOnClickListener {
            onCancel()
        }

        setTimeoutSecond(30, null) {
            onCancel()
        }

        btnConfirm.setOnClickListener {
            when (appPref.currentTransactionType) {
                TransactionType.PUDO_SENDER_WALKIN -> {
                    val intent = Intent(this@ConsentActivity, PudoCardIdentifyActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                TransactionType.PUDO_SENDER,
                TransactionType.PUDO_RECEIVER -> {
                    val intent = Intent(this@ConsentActivity, PudoSenderOrderDetailActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                else -> {
                    val intent = if (appPref.currentVerifyType == "rfid") {
                        Intent(this@ConsentActivity, RfidVerifyActivity::class.java)
                    } else {
                        Intent(this@ConsentActivity, LockerPasswordActivity::class.java)
                    }
                    startActivity(intent)
                    finish()
                }
            }
        }

        showProgressDialog()
        PersonalRepository.getInstance().contentPDPA(
            lang,
            onSuccess = { resp ->

                tvTitle1.text = resp.title1
                tvTitle2.text = resp.title2

                val contents = arrayListOf<String>()
                contents.add(resp.sub_title1)
                contents.add(resp.sub_title2)
                contents.add(resp.content1)
                contents.add(resp.content2)
                contents.add(resp.content3)
                contents.add(resp.content4)
                contents.add(resp.content5)
                contents.add(resp.content6)
                contents.add(resp.content7)
                contents.add(resp.content8)
                tvContent.text = contents.joinToString("\r\n")

                hideProgressDialog()

            },
            onFailure = { error ->
                hideProgressDialog()
                showMessage(error)
            }
        )
    }
}