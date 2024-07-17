package com.lockboxth.lockboxkiosk

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.lockboxth.lockboxkiosk.customdialog.CustomDialog
import com.lockboxth.lockboxkiosk.customdialog.InvalidQrDialog
import com.lockboxth.lockboxkiosk.helpers.BaseActivity
import com.lockboxth.lockboxkiosk.helpers.TransactionType
import com.lockboxth.lockboxkiosk.helpers.Util
import com.lockboxth.lockboxkiosk.http.model.cp.CpParcelRequest
import com.lockboxth.lockboxkiosk.http.model.personal.VerifyBookingRequest
import com.lockboxth.lockboxkiosk.http.model.pudo.PudoCourierParcelRequest
import com.lockboxth.lockboxkiosk.http.model.pudo.PudoReceiverVerifyRequest
import com.lockboxth.lockboxkiosk.http.model.pudo.PudoSenderVerifyRequest
import com.lockboxth.lockboxkiosk.http.model.pudo.PudoVerifyCourierRequest
import com.lockboxth.lockboxkiosk.http.repository.CpRepository
import com.lockboxth.lockboxkiosk.http.repository.PersonalRepository
import com.lockboxth.lockboxkiosk.http.repository.PudoRepository
import com.lockboxth.lockboxkiosk.service.hardware.HardwareService
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.CameraView
import com.otaliastudios.cameraview.PictureResult
import kotlinx.android.synthetic.main.activity_qr_code_scanner.*
import kotlinx.android.synthetic.main.recycler_locker_item.lockerNo
import java.io.ByteArrayOutputStream

class QrCodeScannerActivity : BaseActivity() {

    private var hardwareService: HardwareService? = null

    companion object {
        var currentQrCode = ""
        var using = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_code_scanner)

        btnCancel.setOnClickListener {
            onCancel()
        }

        setTimeoutMinute(5, tvCountdown) {
            onCancel()
        }

        btnCpPin.visibility = if (appPref.currentTransactionType == TransactionType.PUDO_CP_DROP) {
            View.VISIBLE
        } else {
            View.GONE
        }

        btnCpPin.setOnClickListener {
            val intent = Intent(this@QrCodeScannerActivity, LockerPasswordActivity::class.java)
            startActivity(intent)
            finish()
        }


        if (appPref.currentTransactionType == TransactionType.PUDO_COURIER_SENDER || appPref.currentTransactionType == TransactionType.PUDO_CP_DROP) {
            val camera = findViewById<CameraView>(R.id.camera)
            camera.setLifecycleOwner(this)
            camera.addCameraListener(object : CameraListener() {
                override fun onPictureTaken(result: PictureResult) {
                    super.onPictureTaken(result)
                    checkPudoCourierSender(result)
                }
            })
        }

        using = true

        runOnUiThread {
            hardwareService = HardwareService.getInstance()
            hardwareService?.setOnDataReceived { s ->
                if (s.length >= 5 && using) {
                    using = false
                    currentQrCode = s
                    when (appPref.currentTransactionType) {
                        TransactionType.BOOKING -> {
                            checkBookingQrCode()
                        }

                        TransactionType.PUDO_SENDER -> {
                            checkPudoSendQrCode()
                        }

                        TransactionType.PUDO_RECEIVER -> {
                            checkReceiver()
                        }

                        TransactionType.PUDO_COURIER_SENDER -> {
                            if (this.camera.isOpened) {
                                this.camera.takePicture()
                            } else {
                                checkPudoCourierSender()
                            }
                        }

                        TransactionType.PUDO_COURIER_PICKUP -> {
                            checkPudoCourierQr()
                        }

                        TransactionType.PUDO_CP_DROP -> {
                            if (this.camera.isOpened) {
                                this.camera.takePicture()
                            } else {
                                checkPudoPartnerSender()
                            }
                        }

                        else -> {
                            showMessage("INVALID TRANSACTION")
                        }

                    }
                }
            }
        }

    }

    private fun checkReceiver() {
        showProgressDialog()
        val req = PudoReceiverVerifyRequest(
            appPref.kioskInfo!!.generalprofile_id,
            "qrcode",
            currentQrCode,
            appPref.currentLanguage
        )
        PudoRepository.getInstance().receiverVerify(
            req,
            onSuccess = { resp ->
                hideProgressDialog()
                appPref.currentTransactionId = resp.txn
                PudoSenderOrderDetailActivity.address = resp.order
                val intent = if (!resp.has_pdpa) {
                    Intent(this@QrCodeScannerActivity, ConsentActivity::class.java)
                } else {
                    Intent(this@QrCodeScannerActivity, PudoSenderOrderDetailActivity::class.java)
                }
                startActivity(intent)
                finish()
            },
            onFailure = { error ->
                hideProgressDialog()
                CustomDialog.newInstance(R.layout.dialog_invalid_pin).apply {
                    message = error.message!!
                    timeoutCallback = {
                        onCancel()
                    }
                    onOkClickListener = {
                        using = true
                    }
                }.run {
                    show(supportFragmentManager, "")
                }
            }
        )
    }

    @SuppressLint("WrongThread")
    private fun checkPudoCourierSender(result: PictureResult) {
        showProgressDialog()
        result.toBitmap { bmp ->
            val b = Bitmap.createScaledBitmap(bmp!!, 200, 200, false)
            val stream = ByteArrayOutputStream()
            b.compress(Bitmap.CompressFormat.PNG, 90, stream)
            val image = stream.toByteArray()
            val base64 = Util.toBase64(image)

            when (appPref.currentTransactionType) {
                TransactionType.PUDO_COURIER_SENDER -> {
                    checkPudoCourierSender(base64)
                }

                TransactionType.PUDO_CP_DROP -> {
                    checkPudoPartnerSender(base64)
                }

                else -> {

                }
            }
        }
    }

    private fun checkPudoCourierSender(base64: String = "") {
        val req = PudoCourierParcelRequest(
            appPref.kioskInfo!!.generalprofile_id,
            currentQrCode,
            base64
        )
        PudoRepository.getInstance().courierParcel(
            req,
            onSuccess = { resp ->
                hideProgressDialog()
                appPref.currentTransactionId = resp.txn
                PudoSenderOrderDetailActivity.cmd = resp.locker_commands
                val intent = Intent(this@QrCodeScannerActivity, PudoSenderOrderDetailActivity::class.java)
                intent.putExtra("detail", resp.parcel_detail)
                startActivity(intent)
                finish()
            },
            onFailure = { error ->
                hideProgressDialog()
                showMessage(error.message!!) {
                    using = true
                }
            }
        )
    }

    private fun checkPudoPartnerSender(base64: String = "") {
        val req = CpParcelRequest(
            appPref.kioskInfo!!.generalprofile_id,
            currentQrCode,
            base64,
            appPref.currentLanguage
        )
        CpRepository.getInstance().cpParcel(
            req,
            onSuccess = { resp ->
                hideProgressDialog()
                appPref.currentTransactionId = resp.txn
                val cmd = Gson().fromJson(resp.locker_commands, JsonObject::class.java)
                PudoSenderOrderConfirmActivity.currentLockerName = resp.locker_no
                PudoSenderOrderConfirmActivity.currentLockerNo = cmd.keySet().first().toInt()
                val jsonObj = cmd[PudoSenderOrderConfirmActivity.currentLockerNo.toString()].asJsonObject
                PudoSenderOrderConfirmActivity.currentOpenCommand = jsonObj["open"].toString()
                PudoSenderOrderConfirmActivity.currentInQuireCommand = jsonObj["inquire"].toString()
                val intent = Intent(this@QrCodeScannerActivity, PudoSenderOrderConfirmActivity::class.java)
                startActivity(intent)
                finish()
            },
            onFailure = { error ->
                hideProgressDialog()
                showMessage(error, timer = false) {
                    using = true
                }
            }
        )
    }

    private fun checkPudoSendQrCode() {
        showProgressDialog()
        val req = PudoSenderVerifyRequest(
            appPref.kioskInfo!!.generalprofile_id,
            "qrcode",
            currentQrCode,
            appPref.currentLanguage
        )
        PudoRepository.getInstance().senderVerify(
            req,
            onSuccess = { resp ->
                appPref.currentBookingId = resp.booking_id
                appPref.currentTransactionId = resp.txn
                hideProgressDialog()

                val intent = if (resp.has_pdpa) {
                    Intent(this@QrCodeScannerActivity, PudoCardIdentifyActivity::class.java)
                } else {
                    Intent(this@QrCodeScannerActivity, ConsentActivity::class.java)
                }
                startActivity(intent)
                finish()

            },
            onFailure = { error ->
                hideProgressDialog()
                InvalidQrDialog.newInstance(error.message!!).apply {
                    timeoutCallback = {
                        onCancel()
                    }
                    onOkClickListener = {
                        using = true
                    }
                }.run {
                    show(supportFragmentManager, "")
                }
            }
        )
    }

    private fun checkPudoCourierQr() {
        val req = PudoVerifyCourierRequest(
            appPref.kioskInfo!!.generalprofile_id,
            appPref.currentTransactionId!!,
            currentQrCode
        )
        PudoRepository.getInstance().verifyCourier(
            req,
            onSuccess = { r ->
                hideProgressDialog()
                val resp = r.parcel_list.first()
                val intent = Intent(this@QrCodeScannerActivity, CourierQrSummaryActivity::class.java)
                intent.putExtra("locker_no", resp.locker_no)
                intent.putExtra("tracking_no", resp.tracking_number)
                intent.putExtra("locker_commands", Gson().toJson(resp.locker_commands))
                startActivity(intent)
                finish()
            },
            onFailure = { error ->
                hideProgressDialog()
                showMessage(error.message ?: "", timer = false) {
                    using = true
                }
            }
        )
    }

    private fun checkBookingQrCode() {
        showProgressDialog()
        val req = VerifyBookingRequest(
            appPref.kioskInfo!!.generalprofile_id,
            "qrcode",
            currentQrCode
        )
        PersonalRepository.getInstance().verifyBooking(
            req,
            onSuccess = { resp ->
                appPref.currentBookingId = resp.booking_id
                appPref.currentTransactionId = resp.txn
                hideProgressDialog()

                val intent = Intent(this@QrCodeScannerActivity, RegisterActivity::class.java)
                startActivity(intent)
                finish()

            },
            onFailure = { error ->
                hideProgressDialog()
                if (error == "BOKKING_NOT_FOUND" || error == "REQUEST_INVALID") {
                    InvalidQrDialog.newInstance().apply {
                        timeoutCallback = {
                            onCancel()
                        }
                        onOkClickListener = {
                            using = true
                        }
                    }.run {
                        show(supportFragmentManager, "")
                    }
                } else {
                    showMessage(error)
                }
            }
        )
    }

    override fun onDestroy() {
        hardwareService = null
        using = false
        super.onDestroy()
    }
}