package com.lockboxth.lockboxkiosk


import android.content.Intent
import android.os.*
import android.util.Log
import android.view.View
import com.lockboxth.lockboxkiosk.cardreader.SmartCardDevice
import com.lockboxth.lockboxkiosk.cardreader.ThaiSmartCard
import com.lockboxth.lockboxkiosk.helpers.BaseActivity
import com.lockboxth.lockboxkiosk.helpers.TransactionType
import com.lockboxth.lockboxkiosk.http.model.pudo.PudoPersonal
import com.lockboxth.lockboxkiosk.http.model.pudo.PudoVerifyIdCardRequest
import com.lockboxth.lockboxkiosk.http.repository.PudoRepository
import kotlinx.android.synthetic.main.activity_pudo_card_identify.*

class PudoCardIdentifyActivity : BaseActivity() {

    private var completeTransaction = false

    private var allaowRead = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pudo_card_identify)

        layoutIdentify.visibility = View.VISIBLE
        layoutIdentifySuccess.visibility = View.GONE

        readIdCard()

//        if (BuildConfig.C_ENV == "LOCAL") {
//            verify(PudoPersonal("0000000000000", "NameTH", "Address"))
//        }

        btnCancel.setOnClickListener {
            onCancel()
        }

    }

    private fun readIdCard() {
        synchronized(this) {

            try {
                val device: SmartCardDevice? = SmartCardDevice.getSmartCardDevice(applicationContext, "Smartcard", object : SmartCardDevice.SmartCardDeviceEvent {
                    override fun OnReady(device: SmartCardDevice?) {
                        val thaiSmartCard = ThaiSmartCard(device)
                        if (thaiSmartCard.isInserted) {
                            val info = thaiSmartCard.personalInformation
                            if (info == null) {
                                Log.d("SmartCardDevice", "Read Smart Card information failed")
                                Handler(Looper.getMainLooper()).postDelayed({
                                    if (allaowRead) {
                                        readIdCard()
                                    }
                                }, 2000)

                            } else {
                                allaowRead = false
                                verify(PudoPersonal(info.PersonalID, info.NameTH, info.Address))
                            }
                        } else {
                            Handler(Looper.getMainLooper()).postDelayed({
                                if (allaowRead) {
                                    readIdCard()
                                }
                            }, 2000)
                        }
                    }

                    override fun OnDetached(device: SmartCardDevice?) {
                        allaowRead = false
                        Log.d("SmartCardDevice", "Smart Card is removed")
                        showMessage("SmartCardDevice not ready", false)
                    }
                })
                if (device == null) {
                    Handler(Looper.getMainLooper()).postDelayed({
                        if (allaowRead) {
                            readIdCard()
                        }
                    }, 2000)
                }
            } catch (ex: Exception) {
                showMessage("SmartCardDevice not ready", false)
            }
        }
    }

    private fun verify(info: PudoPersonal) {
        showProgressDialog()
        completeTransaction = true
        val req = PudoVerifyIdCardRequest(appPref.kioskInfo!!.generalprofile_id, appPref.currentTransactionId!!, info)
        PudoRepository.getInstance().walkInIdentify(
            req,
            onSuccess = { resp ->
                hideProgressDialog()
                layoutIdentify.visibility = View.GONE
                layoutIdentifySuccess.visibility = View.VISIBLE
                Handler(Looper.getMainLooper()).postDelayed({
                    val intent = Intent(this@PudoCardIdentifyActivity, PudoWarningActivity::class.java)
                    intent.putExtra("warning_message", resp)
                    startActivity(intent)
                }, 5000)

            },
            onFailure = { error ->
                hideProgressDialog()
                showMessage(error.message!!)
            }
        )

    }

    override fun onStart() {
        super.onStart()
        setTimeoutMinute(5, tvCountdown) {
            if (!completeTransaction) {
                onCancel()
            }
        }
    }
}