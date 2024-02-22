package com.lockboxth.lockboxkiosk

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.View.OnKeyListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.lockboxth.lockboxkiosk.customdialog.CustomDialog
import com.lockboxth.lockboxkiosk.helpers.BaseActivity
import com.lockboxth.lockboxkiosk.helpers.TransactionType
import com.lockboxth.lockboxkiosk.helpers.Util
import com.lockboxth.lockboxkiosk.http.model.locker.LockerCheckoutRequest
import com.lockboxth.lockboxkiosk.http.model.personal.VerifyBookingFinishRequest
import com.lockboxth.lockboxkiosk.http.model.personal.VerifyFinishRequest
import com.lockboxth.lockboxkiosk.http.repository.PersonalRepository
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.CameraView
import com.otaliastudios.cameraview.PictureResult
import kotlinx.android.synthetic.main.activity_rfid_verify.*
import kotlinx.android.synthetic.main.activity_rfid_verify.btnCancel
import kotlinx.android.synthetic.main.activity_rfid_verify.camera
import kotlinx.android.synthetic.main.activity_rfid_verify.tvCountdown
import kotlinx.android.synthetic.main.activity_total_payment_summary.*
import java.io.ByteArrayOutputStream


class RfidVerifyActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rfid_verify)

        etRfId.setOnKeyListener(object : OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {
                if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    Log.d("KB", etRfId.text.toString())
                    if (!etRfId.text.isNullOrEmpty()) {
                        when (appPref.currentTransactionType) {
                            TransactionType.BOOKING -> {
                                showProgressDialog()
                                if (camera.isOpened) {
                                    camera.takePicture()
                                } else {
                                    submit()
                                }
                            }
                            else -> {
                                varify(etRfId.text.toString())
                            }
                        }
                    }
                    return true
                }
                return false
            }
        })

        setTimeoutMinute(5, tvCountdown) {
            onCancel()
        }

        btnCancel.setOnClickListener {
            onCancel()
        }

        if (appPref.currentTransactionType == TransactionType.BOOKING) {
            val camera = findViewById<CameraView>(R.id.camera)
            camera.setLifecycleOwner(this)
            camera.addCameraListener(object : CameraListener() {
                override fun onPictureTaken(result: PictureResult) {
                    super.onPictureTaken(result)
                    submit(result)
                }
            })
        }

    }


    private fun varify(rfid: String) {
        showProgressDialog()
        when (appPref.currentTransactionType) {
            TransactionType.IN -> {
                val req = VerifyFinishRequest(appPref.kioskInfo!!.generalprofile_id, appPref.currentTransactionId!!, rfid)
                PersonalRepository.getInstance().verifyFinish(
                    req,
                    onSuccess = {
                        hideProgressDialog()
                        val intent = Intent(this@RfidVerifyActivity, ServiceFeeSummaryActivity::class.java)
                        startActivity(intent)
                        finish()
                    },
                    onFailure = { error ->
                        hideProgressDialog()
                        showMessage(error)
                    }
                )
            }
            TransactionType.OUT -> {
                val req = VerifyFinishRequest(appPref.kioskInfo!!.generalprofile_id, appPref.currentTransactionId!!, rfid)
                PersonalRepository.getInstance().verifyOut(
                    req,
                    onSuccess = {
                        hideProgressDialog()
                        val intent = Intent(this@RfidVerifyActivity, SelectLockBoxActivity::class.java)
                        startActivity(intent)
                    },
                    onFailure = { error ->
                        handleError(error)
                        hideProgressDialog()
                    }
                )
            }
            else -> {}
        }

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
        val req = VerifyBookingFinishRequest(appPref.kioskInfo!!.generalprofile_id, appPref.currentTransactionId!!, etRfId.text.toString(), base64)
        PersonalRepository.getInstance().bookingFinish(
            req,
            onSuccess = { resp ->
                hideProgressDialog()
                openLocker(resp.locker_no, resp.event_type, Gson().toJson(resp.locker_commands))
            },
            onFailure = { error ->
                hideProgressDialog()
                handleError(error)
            }
        )
    }

    private fun handleError(error: String) {
        when (error) {
            "BOKKING_NOT_FOUND",
            "BOOKING_NOT_FOUND",
            "REQUEST_INVALID",
            "PIN_FAIL" -> {
                CustomDialog.newInstance(R.layout.dialog_invalid_pin).apply {
                    timeoutCallback = {
                        onCancel()
                    }
                }.run {
                    show(supportFragmentManager, "")
                }
            }
            else -> {
                showMessage(error)
            }
        }
    }
}

