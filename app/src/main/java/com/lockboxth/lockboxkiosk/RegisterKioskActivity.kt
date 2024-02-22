package com.lockboxth.lockboxkiosk

import android.app.Activity
import android.content.Intent
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Bundle
import android.util.Log
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.messaging.FirebaseMessaging
import com.lockboxth.lockboxkiosk.customdialog.CustomDialog
import com.lockboxth.lockboxkiosk.helpers.BaseActivity
import com.lockboxth.lockboxkiosk.http.model.kiosk.RegisterKiosk
import com.lockboxth.lockboxkiosk.http.repository.KioskRepository
import kotlinx.android.synthetic.main.activity_register_kiosk.*

class RegisterKioskActivity : BaseActivity() {

    private var fcmToken: String? = null

    private val ACTION_USB_PERMISSION = "com.lockboxth.lockboxkiosk.usb.perm"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_kiosk)

//        etSerialNumber.setText("ADDEMO001")
//        etMasterKey.setText("WHgPYikp")

//        etSerialNumber.setText("LSB0054")
//        etMasterKey.setText("ciNbcvWj")

//        this.appPref.kioskInfo = null

        if (!isGooglePlayServicesAvailable(this)) {
            CustomDialog.newInstance(R.layout.dialog_gms_not_found).apply {
                onOkClickListener = {
                    finish()
                }
                timeoutCallback = {
                    finish()
                }
            }.run {
                show(supportFragmentManager, "")
            }

        } else {
            checkRegister()
//            val usbManager = this.getSystemService(USB_SERVICE) as UsbManager
//            val deviceList: HashMap<String, UsbDevice> = usbManager.deviceList
//            val deviceIterator: Iterator<UsbDevice> = deviceList.values.iterator()
//
//            while (deviceIterator.hasNext()) {
//                val device = deviceIterator.next()
//                Log.d("DEVICES", device.productId.toString() + " : " + device.vendorId.toString() + " : " + device.productName)
//            }
        }

        btnSubmit.setOnClickListener {
            if (validate()) {
                showProgressDialog()
                val req = RegisterKiosk(etSerialNumber.text.toString(), etMasterKey.text.toString(), fcmToken!!)
                KioskRepository.getInstance().register(
                    req,
                    onSuccess = { resp ->
                        appPref.kioskInfo = resp
                        goMainActivity()
                        hideProgressDialog()
                    },
                    onFailure = { error ->
                        showMessage(error, allowCancel = false)
                        hideProgressDialog()
                    }
                )
            }
        }

    }

    private fun isGooglePlayServicesAvailable(activity: Activity?): Boolean {
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val status = googleApiAvailability.isGooglePlayServicesAvailable(activity!!)
        if (status != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(status)) {
                googleApiAvailability.getErrorDialog(activity, status, 2404)!!.show()
            }
            return false
        }
        return true
    }

    private fun validate(): Boolean {
        etSerialNumber.error = null
        etMasterKey.error = null
        var valid = true
        if (etSerialNumber.text == null) {
            valid = false
            etSerialNumber.error = "กรุณากรอก Serial Number"
        }
        if (etMasterKey.text == null) {
            valid = false
            etMasterKey.error = "กรุณากรอก Master Key"
        }
        return valid
    }

    private fun checkRegister() {
        if (this.appPref.kioskInfo != null) {
            goMainActivity()
            return
        }
        showProgressDialog()
        FirebaseMessaging.getInstance().token.addOnSuccessListener { fcmToken ->
            hideProgressDialog()
            Log.d("fcmToken", fcmToken)
            this@RegisterKioskActivity.fcmToken = fcmToken
            Log.d("fcmToken", fcmToken)
        }
    }

    private fun goMainActivity() {
        val intent = Intent(this@RegisterKioskActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}