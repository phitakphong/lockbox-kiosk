package com.lockboxth.lockboxkiosk

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.View.OnKeyListener
import com.google.gson.Gson
import com.lockboxth.lockboxkiosk.customdialog.PaymentTimeoutDialog
import com.lockboxth.lockboxkiosk.helpers.BaseActivity
import com.lockboxth.lockboxkiosk.helpers.Util
import com.lockboxth.lockboxkiosk.http.model.locker.LockerCheckoutRequest
import com.lockboxth.lockboxkiosk.http.repository.LockerRepository
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.CameraView
import com.otaliastudios.cameraview.PictureResult
import kotlinx.android.synthetic.main.activity_qr_code_payment.*
import kotlinx.android.synthetic.main.activity_rfid_payment.*
import kotlinx.android.synthetic.main.activity_rfid_payment.btnBack
import kotlinx.android.synthetic.main.activity_rfid_payment.btnCancel
import kotlinx.android.synthetic.main.activity_rfid_payment.camera
import kotlinx.android.synthetic.main.activity_rfid_payment.tvChannel
import kotlinx.android.synthetic.main.activity_rfid_payment.tvCountdown
import kotlinx.android.synthetic.main.activity_rfid_payment.tvTotal
import kotlinx.android.synthetic.main.activity_total_payment_summary.*
import java.io.ByteArrayOutputStream

class RfidPaymentActivity : BaseActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rfid_payment)

        btnBack.setOnClickListener {
            onBackPressed()
        }

        btnCancel.setOnClickListener {
            onCancel()
        }

        val paymentMethodDisplay = intent.getStringExtra("payment_method_display")
        val amountPay = intent.getStringExtra("amount_pay")

        if (paymentMethodDisplay != null) {
            tvChannel.text = paymentMethodDisplay
        }
        if (amountPay != null) {
            tvTotal.text = "$amountPay บาท"
        }

        setTimeoutMinute(5, tvCountdown, timeoutCallback = {
            PaymentTimeoutDialog.newInstance().apply {
                onOkClickListener = {
                    onBack(appPref.currentTransactionType!!, "timeout") {
                        onBackPressed()
                    }
                }
                timeoutCallback = {
                    onCancel()
                }
            }.run {
                show(supportFragmentManager, "")
            }
        })

        val camera = findViewById<CameraView>(R.id.camera)
        camera.setLifecycleOwner(this)
        camera.addCameraListener(object : CameraListener() {
            override fun onPictureTaken(result: PictureResult) {
                super.onPictureTaken(result)
                submit(result)
            }
        })

        etRfId.setOnKeyListener(object : OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {
                if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    Log.d("KB", etRfId.text.toString())
                    if (!etRfId.text.isNullOrEmpty()) {
                        showProgressDialog()
                        if (camera.isOpened) {
                            this@RfidPaymentActivity.camera.takePicture()
                        } else {
                            submit()
                        }
                    }
                    return true
                }
                return false
            }
        })
    }

    @SuppressLint("WrongThread")
    private fun submit(result: PictureResult) {
        result.toBitmap { bmp ->
            val b = Bitmap.createScaledBitmap(bmp!!, 200, 200, false)
            val stream = ByteArrayOutputStream()
            b.compress(Bitmap.CompressFormat.PNG, 90, stream)
            val image = stream.toByteArray()
            val base64 = Util.toBase64(image)
            submit(base64)
        }
    }

    private fun submit(base64: String = "") {
        val req = LockerCheckoutRequest(appPref.kioskInfo!!.generalprofile_id, appPref.currentTransactionId!!, base64)
        LockerRepository.getInstance().checkout(
            appPref.currentTransactionType!!,
            req,
            onSuccess = { resp ->
                hideProgressDialog()
                val intent = Intent(this@RfidPaymentActivity, PaymentSuccessActivity::class.java)
                intent.putExtra("locker_no", resp.locker_no)
                if (resp.locker_commands != null) {
                    intent.putExtra("locker_commands", Gson().toJson(resp.locker_commands))
                }
                intent.putExtra("event_type", resp.event_type)
                startActivity(intent)
                finish()
            },
            onFailure = { error ->
                hideProgressDialog()
                showMessage("ไม่สามารถชำระเงินผ่านช่องทางนี้ได้ให้เลือกช่องทางอื่น")
            }
        )
    }
}