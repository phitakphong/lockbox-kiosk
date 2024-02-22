package com.lockboxth.lockboxkiosk.service.hardware

import android.text.TextUtils
import android.util.Log
import android_serialport_api.SerialPort
import com.google.gson.JsonObject
import java.io.File
import java.io.IOException
import java.io.OutputStream
import java.nio.charset.StandardCharsets
import java.security.InvalidParameterException
import java.util.*

class HardwareService {

    val TAG = "SC20 HDMI MDB"

    private val serialPorts: ArrayList<SerialPort> = ArrayList()
    private val outputStreams: ArrayList<OutputStream> = ArrayList()
    private val readThreads: ArrayList<Thread> = ArrayList()

    private var dataReceived: ((String) -> Unit)? = null

    private var isRunning = false

    private fun onDataReceived(buffer: ByteArray, size: Int, channel: String) {

        var rx = "$channel RX"
        val receiveBuffer = ByteArray(size)
        for (i in 0 until size) {
            rx += String.format(" %02X", buffer[i])
            receiveBuffer[i] = buffer[i]
        }

        var str = String(receiveBuffer, StandardCharsets.US_ASCII)
        str = str.trim()

        Log.i(TAG, rx)
        Log.i(TAG, "$channel RX $str")

        if (dataReceived != null) {
            dataReceived!!(str)
        }

    }

    @Throws(SecurityException::class, IOException::class, InvalidParameterException::class)
    fun getSerialPort(port: String, baudrate: Int): SerialPort {
        return SerialPort(File(port), baudrate, 0)
    }

    fun hex2binary(hexvalue: String): CharArray? {
        var binaryval = Integer.toString(hexvalue.toInt(16), 2)
        binaryval = String.format("%8s", binaryval).replace(' ', '0')

        // Creating array of string length
        val ch = CharArray(binaryval.length)

        // Copy character by character into array
        for (i in 0 until binaryval.length) {
            ch[i] = binaryval[i]
        }
        return ch
    }

    fun getHexFormString(data: String): String {
        val hexList: MutableList<String?> = java.util.ArrayList()
        val values = data.toCharArray()
        for (letter in values) {
            val value = letter.code
            val hexOutput = String.format("%X", value)
            hexList.add(hexOutput)
        }
        return TextUtils.join(" ", hexList)
    }

    fun byteArrayToHexString(data: ByteArray): String? {
        val sb = StringBuilder(data.size * 3)
        for (b in data) {
            sb.append(String.format("%02X", b).uppercase(Locale.getDefault())).append(" ")
        }
        return sb.toString()
    }

    fun hexStringToByteArray(s: String): ByteArray {
        var s = s
        s = s.replace(" ", "")
        val buffer = ByteArray(s.length / 2)
        var i = 0
        while (i < s.length) {
            buffer[i / 2] = s.substring(i, i + 2).toInt(16).toByte()
            i += 2
        }
        return buffer
    }

    fun calculateLRC(hexString: String): String? {
        val buf = hexStringToByteArray(hexString)
        var chkSum = 0
        for (b in buf) {
            chkSum += b.toInt()
        }
        chkSum = chkSum and 0xff
        chkSum = 0x100 - chkSum and 0xff
        return String.format("%02X", chkSum)
    }

    fun reverse(a: CharArray): CharArray? {
        var k: Int
        var t: Int
        val n = a.size
        var i: Int = 0
        while (i < n / 2) {
            t = a[i].toInt()
            a[i] = a[n - i - 1]
            a[n - i - 1] = t.toChar()
            i++
        }
        return a
    }

    private fun initThread() {
        isRunning = true
        for (i in 1..7) {
            Log.i(TAG, "Init Serial /dev/ttysWK$i")
            val serialPort = getSerialPort("/dev/ttysWK$i", 115200)
            val outputStream = serialPort.outputStream
            outputStreams.add(outputStream)
            val inputStream = serialPort.inputStream
            val readThread = object : Thread() {
                override fun run() {
                    super.run()
                    while (!isInterrupted) {
                        var size: Int
                        try {
                            val buffer = ByteArray(64)
                            if (inputStream == null) return
                            size = inputStream.read(buffer)
                            if (size > 0) {
                                onDataReceived(buffer, size, "WK$i")
                            }
                        } catch (e: IOException) {
                            e.printStackTrace()
                            return
                        }
                    }
                }
            }
            readThread.start()
            readThreads.add(readThread)
            serialPorts.add(serialPort)
        }


    }

    fun setOnDataReceived(dataReceived: ((String) -> Unit)) {
        this.dataReceived = dataReceived
    }

    fun writeCommand(command: String) {
        var cmd = command.replace("\"", "")
        cmd = cmd.replace("\\", "")
        Log.i(TAG, "TX $cmd")
        cmd = getHexFormString(cmd) + " 0D 0A"
        val buffer: ByteArray = hexStringToByteArray(cmd)
        outputStreams.forEach { s ->
            s.write(buffer, 0, buffer.size)
        }
        Thread.sleep(500)
    }

    fun destroy() {
        isRunning = false
        readThreads.forEach { t ->
            t.interrupt()
        }
        serialPorts.forEach { s ->
            s.close()
        }
        serialPorts.clear()
        outputStreams.clear()
    }

    init {
        Log.i("initThread", "initThread")
        this.initThread()
    }

    companion object {
        private var instance: HardwareService? = null
            get() {
                if (field == null) {
                    synchronized(HardwareService::class.java) {
                        if (field == null) {
                            field = HardwareService()
                        }
                    }
                }
                return field
            }

        @JvmName("getInstance1")
        fun getInstance(): HardwareService {
            return instance!!
        }
    }


}