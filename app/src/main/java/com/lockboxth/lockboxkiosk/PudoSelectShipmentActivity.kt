package com.lockboxth.lockboxkiosk

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.reflect.TypeToken
import com.lockboxth.lockboxkiosk.adapter.PudoShipmentRecyclerAdapter
import com.lockboxth.lockboxkiosk.helpers.BaseActivity
import com.lockboxth.lockboxkiosk.helpers.Gsoner
import com.lockboxth.lockboxkiosk.helpers.TransactionType
import com.lockboxth.lockboxkiosk.helpers.Util
import com.lockboxth.lockboxkiosk.http.model.pudo.PudoCourierSelectRequest
import com.lockboxth.lockboxkiosk.http.model.pudo.PudoShipmentItem
import com.lockboxth.lockboxkiosk.http.model.pudo.PudoShipmentRequest
import com.lockboxth.lockboxkiosk.http.model.pudo.PudoWalkInPickupConfirmRequest
import com.lockboxth.lockboxkiosk.http.repository.PudoRepository
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.CameraView
import com.otaliastudios.cameraview.PictureResult
import kotlinx.android.synthetic.main.activity_locker_password.camera
import kotlinx.android.synthetic.main.activity_pudo_select_shipment.*
import java.io.ByteArrayOutputStream

class PudoSelectShipmentActivity : BaseActivity() {

    private var adapter: PudoShipmentRecyclerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pudo_select_shipment)

        showProgressDialog()

        if (appPref.currentTransactionType == TransactionType.PUDO_SENDER_WALKIN) {
            tvTitle.text = getString(R.string.select_shiping)
            val type = intent.getStringExtra("type")!!
            val profileId = intent.getIntExtra("generalprofile_id", 0)
            PudoRepository.getInstance().walkinPickupConfirm(
                PudoWalkInPickupConfirmRequest(
                    appPref.kioskInfo!!.generalprofile_id, appPref.currentTransactionId!!, type, if (profileId != 0) {
                        profileId
                    } else {
                        null
                    }
                ),
                onSuccess = { resp ->
                    recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                    adapter = PudoShipmentRecyclerAdapter(this, ArrayList(resp.shipments), appPref.currentTransactionType!!)
                    recyclerView.adapter = adapter
                    hideProgressDialog()
                },
                onFailure = { error ->
                    hideProgressDialog()
                    showMessage(error.message!!)
                    finish()
                }
            )
        } else {

            val camera = findViewById<CameraView>(R.id.camera)
            camera.setLifecycleOwner(this)
            camera.addCameraListener(object : CameraListener() {
                @SuppressLint("WrongThread")
                override fun onPictureTaken(result: PictureResult) {
                    super.onPictureTaken(result)
                    result.toBitmap { bmp ->
                        val b = Bitmap.createScaledBitmap(bmp!!, 200, 200, false)
                        val stream = ByteArrayOutputStream()
                        b.compress(Bitmap.CompressFormat.PNG, 90, stream)
                        val image = stream.toByteArray()
                        val base64 = Util.toBase64(image)
                        selectCourier(base64)
                    }
                }
            })

            tvTitle.text = getString(R.string.select_shiping2)
            PudoRepository.getInstance().shipments(
                PudoShipmentRequest(appPref.kioskInfo!!.generalprofile_id, appPref.currentLanguage),
                onSuccess = { resp ->
                    appPref.currentTransactionId = resp.txn
                    recyclerView.layoutManager = GridLayoutManager(this, 2)
                    adapter = PudoShipmentRecyclerAdapter(this, ArrayList(resp.shipments), appPref.currentTransactionType!!)
                    recyclerView.adapter = adapter
                    hideProgressDialog()
                },
                onFailure = { error ->
                    hideProgressDialog()
                    showMessage(error.message!!)
                    finish()
                }
            )
        }

        setTimeoutMinute(5, tvCountdown) {
            onCancel()
        }

        btnCancel.setOnClickListener {
            onCancel()
        }

        btnConfirm.setOnClickListener {
            if (adapter == null || adapter!!.selectedItem == null) {
                showMessage(getString(R.string.require_shiping))
                return@setOnClickListener
            }
            showProgressDialog()
            if (appPref.currentTransactionType == TransactionType.PUDO_SENDER_WALKIN) {
                hideProgressDialog()
                val intent = Intent(this@PudoSelectShipmentActivity, PudoWalkInAddressDetailActivity::class.java)
                intent.putExtra("shipment_id", adapter!!.selectedItem!!.shipment_id)
                startActivity(intent)
            } else {
                if (this.camera.isOpened) {
                    this.camera.takePicture()
                } else {
                    selectCourier()
                }
            }
        }

    }

    private fun selectCourier(base64: String = "") {
        PudoRepository.getInstance().selectCourier(
            PudoCourierSelectRequest(appPref.kioskInfo!!.generalprofile_id, appPref.currentTransactionId!!, adapter!!.selectedItem!!.shipment_id, base64),
            onSuccess = { resp ->
                hideProgressDialog()
                val intent = if (resp.pickup_method.lowercase() == "qr") {
                    Intent(this@PudoSelectShipmentActivity, QrCodeScannerActivity::class.java)
                } else {
                    Intent(this@PudoSelectShipmentActivity, LockerPasswordActivity::class.java)
                }
                startActivity(intent)
                finish()
            },
            onFailure = { error ->
                hideProgressDialog()
                showMessage(error.message!!)
            }
        )
    }
}