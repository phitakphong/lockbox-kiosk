package com.lockboxth.lockboxkiosk.print

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.hardware.usb.*
import android.os.Build
import android.os.Parcelable
import android.util.Base64
import android.util.Log
import com.dantsu.escposprinter.connection.usb.UsbConnection
import com.dantsu.escposprinter.connection.usb.UsbPrintersConnections


class PrintUtil(usbDevice: UsbDevice, usbManager: UsbManager) {


    companion object {
        private var instance: PrintUtil? = null
        val TAG = "PRINT"

        @JvmName("getInstance1")
        fun getInstance(): PrintUtil {
            return instance!!
        }

        fun newInstance(usbDevice: UsbDevice, usbManager: UsbManager) {
            instance = PrintUtil(usbDevice, usbManager)
        }

        private val ACTION_USB_PERMISSION = "com.lockboxth.lockboxkiosk.Print.USB_PERMISSION"
        private val usbReceiver: BroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val action = intent.action
                if (ACTION_USB_PERMISSION == action) {
                    synchronized(this) {
                        val usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager?
                        val usbDevice = intent.getParcelableExtra<Parcelable>(UsbManager.EXTRA_DEVICE) as UsbDevice?
                        if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                            if (usbManager != null && usbDevice != null) {
                                Log.d(TAG, "EXTRA_PERMISSION_GRANTED")
                                newInstance(usbDevice, usbManager)
                            }
                        }
                    }
                }
            }
        }

        fun requestPrinterPermission(context: Context) {
            Log.d(TAG, "requestPrinterPermission")
            val usbConnection: UsbConnection? = UsbPrintersConnections.selectFirstConnected(context)
            val usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager?
            if (usbConnection != null && usbManager != null) {
                val permissionIntent = PendingIntent.getBroadcast(
                    context,
                    0,
                    Intent(ACTION_USB_PERMISSION),
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_MUTABLE else 0
                )
                val filter = IntentFilter(ACTION_USB_PERMISSION)
                context.registerReceiver(usbReceiver, filter)
                usbManager.requestPermission(usbConnection.device, permissionIntent)
            }
        }
    }

    private var mEndPoint: UsbEndpoint? = null
    private val forceCLaim = true
    private val packetSize = 128
    private var mInterface: UsbInterface? = null
    private var mDevice: UsbDevice? = usbDevice
    private var connection: UsbDeviceConnection? = null

    private val labelWidth = 100 //mm.
    private val labelHeight = 70 //mm.

    init {
        connection = usbManager.openDevice(usbDevice)
        Log.d(TAG, "usbManager.openDevice(usbDevice) ${connection == null}")
        mInterface = usbDevice.getInterface(0)
        mEndPoint = mInterface!!.getEndpoint(1)
    }

    fun printImage(base64: String) {

        val base = base64.split(',').last()
        val decodedString: ByteArray = Base64.decode(base, Base64.DEFAULT)
        val mBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)

        if (mBitmap != null) {
            val tsc = LabelCommand()
            tsc.addSize(labelWidth, labelHeight)  // defines the label(กระดาษ) width and length.
            tsc.addGap(3) // ระยะห่างช่องว่างแต่ละ label (mm)
            tsc.addCls()  //clears the image buffer
            tsc.addBitmap(5, 20, LabelCommand.BITMAP_MODE.OVERWRITE, 800, mBitmap, "1") // print mode "0" or "1" (1 ชัดกว่า)
            tsc.addPrint(1)  // prints the label format currently stored in the image buffer.
            val datas = tsc.command
            val bytes = LabelUtils.ByteTo_byte(datas)
            sendCommand(bytes)
        }
    }

    fun printImage(bitmap: Bitmap) {
        val tsc = LabelCommand()
        tsc.addSize(labelWidth, labelHeight)  // defines the label(กระดาษ) width and length.
        tsc.addGap(3) // ระยะห่างช่องว่างแต่ละ label (mm)
        tsc.addCls()  //clears the image buffer
        tsc.addBitmap(5, 20, LabelCommand.BITMAP_MODE.OVERWRITE, 800, bitmap, "1") // print mode "0" or "1" (1 ชัดกว่า)
        tsc.addPrint(1)  // prints the label format currently stored in the image buffer.
        val datas = tsc.command
        val bytes = LabelUtils.ByteTo_byte(datas)
        sendCommand(bytes)
    }

    private fun sendCommand(bytes: ByteArray) {
        mEndPoint = mInterface!!.getEndpoint(1)
        if (connection == null) {
            Log.e(TAG, "CONNECTION IS NULL")
        } else {
            connection!!.claimInterface(mInterface!!, forceCLaim)
            val thread = Thread {

                Log.d(TAG, "bytes.size ${bytes.size}")

                if (bytes.size > packetSize) {
                    /*** send large command (bitmap) ***/
                    Log.d(TAG, "start")
                    sendLargeCommand(bytes)
                    Log.d(TAG, "end")
                } else {
                    Log.d(TAG, "start")
                    val x = connection!!.bulkTransfer(mEndPoint, bytes, bytes.size, 5000)
                    Log.d(TAG, x.toString())
                    Log.d(TAG, "end")
                    connection!!.releaseInterface(mInterface!!);
                    connection!!.close();
                }
            }
            thread.run()
        }
    }

    private fun sendLargeCommand(command: ByteArray) {
        for (i in 0..command.size step packetSize) {
            val length = Math.min(packetSize, command.size - i)
            Log.d(TAG, length.toString())
            val packet = ByteArray(length)
            System.arraycopy(command, i, packet, 0, length)
            Log.d(TAG, "connection : ${connection == null}")
            val result: Int = connection?.bulkTransfer(mEndPoint, packet, length, 5000) ?: -999
            if (result < 0) {

                Log.d(TAG, "result < 0 >>> $result")
                // Error occurred
                break
            }
        }
    }


}