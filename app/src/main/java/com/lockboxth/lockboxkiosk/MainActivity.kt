package com.lockboxth.lockboxkiosk

import android.Manifest
import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.PendingIntent
import android.app.admin.DevicePolicyManager
import android.content.*
import android.content.pm.PackageInstaller
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.*
import android.provider.Settings
import android.util.DisplayMetrics
import android.util.Log
import android.view.Display
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.lockboxth.lockboxkiosk.cardreader.SmartCardDevice
import com.lockboxth.lockboxkiosk.customdialog.SelectLanguageDialog
import com.lockboxth.lockboxkiosk.helpers.BaseActivity
import com.lockboxth.lockboxkiosk.helpers.TransactionType
import com.lockboxth.lockboxkiosk.http.model.kiosk.KioskAdsResponse
import com.lockboxth.lockboxkiosk.http.model.kiosk.KioskProfileRequest
import com.lockboxth.lockboxkiosk.http.repository.KioskRepository
import com.lockboxth.lockboxkiosk.print.PrintUtil
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File


class MainActivity : BaseActivity() {

    private val startTime = 0
    private val interval = (1 * 1000).toLong()
    private var countDownTimer: CountDownTimer? = null

    private var ads: KioskAdsResponse? = null

    private val permissions = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.READ_PHONE_STATE
    )
    private var permIndex: Int = -1

    private val updateApkFilename = "app-debug.apk"
    private val updateUrl = "http://192.168.1.41/app-debug.apk"
    private var updateVersion = "1.0.1"


    override fun onCreate(savedInstanceState: Bundle?) {

        allowOpenLocker = true

        setLocale(appPref.currentLanguage)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        appPref.kioskInfo = RegisterKioskResponse(1, appPref.kioskInfo!!.token)

        locker.visibility = View.GONE
        topup.visibility = View.GONE
        pudo.visibility = View.GONE
        booking.visibility = View.GONE
        ap.visibility = View.GONE

        locker.setOnClickListener {
            val intent = Intent(this@MainActivity, LockerActivity::class.java)
            startActivity(intent)
        }

        topup.setOnClickListener {
            appPref.currentTransactionType = TransactionType.TOPUP
            val intent = Intent(this@MainActivity, RegisterActivity::class.java)
            startActivity(intent)
        }

        pudo.setOnClickListener {
            val intent = Intent(this@MainActivity, PudoActivity::class.java)
            startActivity(intent)
        }

        booking.setOnClickListener {
            appPref.currentTransactionType = TransactionType.BOOKING
            val intent = Intent(this@MainActivity, PudoVerifyTypeActivity::class.java)
            startActivity(intent)
        }

        ap.setOnClickListener {
            val intent = Intent(this@MainActivity, LockerActivity::class.java)
            intent.putExtra("is_ap", true)
            startActivity(intent)
        }

        appPref.currentTransactionType = null
        appPref.currentTransactionId = null
        appPref.currentBookingId = null
        appPref.currentVerifyType = null
        appPref.outType = null

        checkPermission()

        try {
            val device: SmartCardDevice? = SmartCardDevice.getSmartCardDevice(applicationContext, "Smartcard", object : SmartCardDevice.SmartCardDeviceEvent {
                override fun OnReady(device: SmartCardDevice?) {
                    PrintUtil.requestPrinterPermission(applicationContext)
                }

                override fun OnDetached(device: SmartCardDevice?) {}
            })

            if (device == null) {
                showMessage("SmartCardDevice not ready", false)
                Log.d("SmartCardDevice", "Smart Card is null")
            }
        } catch (e: Exception) {
            showMessage("SmartCardDevice not ready", false)
        }

        (application as MyApplication).initMonitor()

        showProgressDialog()
        KioskRepository.getInstance().config(
            appPref.kioskInfo!!.generalprofile_id,
            onSuccess = { resp ->
                hideProgressDialog()
                appPref.currentConfig = resp.main_menu
                if (resp.main_menu.find { m -> m.id == 1  ||  m.id == 8 } != null) {
                    locker.visibility = View.VISIBLE
                    booking.visibility = View.VISIBLE
                }
                if (resp.main_menu.find { m -> m.id == 2 || m.id == 4 || m.id == 7 } != null) {
                    pudo.visibility = View.VISIBLE
                }
                if (resp.main_menu.find { m -> m.id == 5 } != null) {
                    topup.visibility = View.VISIBLE
                }
                if (resp.main_menu.find { m -> m.id == 6 } != null) {
                    ap.visibility = View.VISIBLE
                }
            },
            onFailure = { e ->
                hideProgressDialog()
                showMessage(e, timer = false) {
                    finish()
                }
            }
        )

        btnLang.setOnClickListener {
            SelectLanguageDialog.newInstance().apply {
                onSelectedListener = { lang ->
                    Log.d("LANG", appPref.currentLanguage + " <> " + lang)
                    dismiss()
                    if (appPref.currentLanguage != lang) {
                        appPref.currentLanguage = lang
                        goToMainActivity()
                    }
                }
            }.run {
                show(supportFragmentManager, "")
            }
        }


    }


    private fun isAvailableUpdate(): Boolean {
        val versionName = BuildConfig.VERSION_NAME
        return versionName != updateVersion
    }

    @SuppressLint("Range")
    private fun downloadUpdate(context: Context) {
        try {

            val TAG = "INAPPUPDATE"
            val apkFilePath = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), updateApkFilename)
            if (apkFilePath.exists()) {
                apkFilePath.delete()
            }
            Log.v(TAG, "Downloading request on url :$updateUrl")
            val request = DownloadManager.Request(Uri.parse(updateUrl))
            request.setDescription(updateVersion)
            request.setTitle(context.getString(R.string.app_name))

            val uri = Uri.parse("file://$apkFilePath")
            request.setDestinationUri(uri)
            val manager = context.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            val downloadId = manager.enqueue(request)
            showProgressDialog("Updating...")
            Thread {
                var downloading = true
                while (downloading) {
                    val q = DownloadManager.Query()
                    q.setFilterById(downloadId)
                    val cursor: Cursor = manager.query(q)
                    cursor.moveToFirst()
                    val bytesDownloaded: Int = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                    if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) === DownloadManager.STATUS_SUCCESSFUL) {
                        downloading = false
                    }
                    val bytesTotal: Int = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                    if (bytesTotal != 0) {
                        val dlProgress = (bytesDownloaded * 100L / bytesTotal).toInt()
                        this@MainActivity.runOnUiThread {
                            println("Updating : ${dlProgress}%")
                            showProgressDialog("Updating ${dlProgress}%")
                        }
                    }
                    cursor.close()
                }
            }.start()

            val onComplete: BroadcastReceiver = object : BroadcastReceiver() {
                override fun onReceive(ctxt: Context, intent: Intent) {
                    var intent = intent
                    if (apkFilePath.exists()) {
                        val apkUri = FileProvider.getUriForFile(context, applicationContext.packageName + ".fileprovider", apkFilePath)
                        intent = Intent(Intent.ACTION_INSTALL_PACKAGE)
                        intent.data = apkUri
                        intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true)
                        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                        context.startActivity(intent)
//
//                        val command = "pm install -r ${apkFilePath.absolutePath}"
//                        Runtime.getRuntime().exec(arrayOf("su", "-c", command))

                        install(context, "com.lockboxth.lockboxkiosk", apkFilePath.absolutePath)

                    } else {
                        hideProgressDialog()
                        showMessage("Something went wrong")
                    }
                    context.unregisterReceiver(this)
                }
            }

            context.registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        } catch (e: Exception) {
            e.printStackTrace()
            hideProgressDialog()
            showMessage("Update fail.")
        }
    }

    fun install(context: Context, packageName: String, apkPath: String) {
        val TAG = "INAPPUPDATE"
        val packageInstaller = context.packageManager.packageInstaller
        println("Installing")

        // Prepare params for installing one APK file with MODE_FULL_INSTALL
        // We could use MODE_INHERIT_EXISTING to install multiple split APKs
        val params = PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL)
        params.setAppPackageName(packageName)

        // Get a PackageInstaller.Session for performing the actual update
        val sessionId = packageInstaller.createSession(params)
        val session = packageInstaller.openSession(sessionId)

        // Copy APK file bytes into OutputStream provided by install Session
        val out = session.openWrite(packageName, 0, -1)
        val fis = File(apkPath).inputStream()
        fis.copyTo(out)
        session.fsync(out)
        out.close()

        // The app gets killed after installation session commit
        session.commit(
            PendingIntent.getBroadcast(
                context, sessionId,
                Intent("android.intent.action.MAIN"), 0
            ).intentSender
        )
        println("Install success")
        updateVersion = ""
    }

    private fun checkPermission() {
        permIndex += 1
        if (permIndex >= permissions.size) {
            return
        }
        val p = permissions[permIndex]
        val perm = ContextCompat.checkSelfPermission(this@MainActivity, p)
        if (perm != PackageManager.PERMISSION_GRANTED) {
            permissionsResultCallback.launch(p)
        } else {
            println("$p Permission isGranted")
            checkPermission()
        }
    }

    private val permissionsResultCallback = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        if (!it) {
            Toast.makeText(this@MainActivity, "Permission denied", Toast.LENGTH_SHORT).show()
        }
        checkPermission()
    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        createTimer()
    }

    private fun createTimer() {
        if (ads == null || ads?.screen_saver?.size == 0) {
            return
        }
        if (countDownTimer != null) {
            countDownTimer!!.cancel()
        }

        val screenSaver = ads!!.screen_saver[0]
        val startTime = (10 * 1000).toLong()

        Log.d("SSVV", screenSaver.source)

        countDownTimer = object : CountDownTimer(startTime, interval) {
            override fun onTick(millisUntilFinished: Long) {
                Log.d("CountDownTimer", millisUntilFinished.toString())
            }

            override fun onFinish() {
                val intent = Intent(this@MainActivity, ScreenServerActivity::class.java)
                intent.putExtra("videoUrl", screenSaver.source)
                startActivity(intent)
            }
        }.start()
    }

    override fun onResume() {
        super.onResume()
        getAds()

    }

    override fun onPause() {
        if (countDownTimer != null) {
            countDownTimer!!.cancel()
        }
        super.onPause()
    }

    private fun getAds() {
        try {
            if (ads == null) {
                val req = KioskProfileRequest(appPref.kioskInfo!!.generalprofile_id)
                KioskRepository.getInstance().ads(
                    req,
                    onSuccess = { resp ->
                        ads = resp
                        createTimer()
                    },
                    onFailure = { error ->
                        showMessage(error)
                    }
                )
            } else {
                createTimer()
            }
        } catch (ex: Exception) {
        }
    }
}
