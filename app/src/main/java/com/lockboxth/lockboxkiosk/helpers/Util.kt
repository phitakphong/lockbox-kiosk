package com.lockboxth.lockboxkiosk.helpers

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.lockboxth.lockboxkiosk.R
import com.lockboxth.lockboxkiosk.http.HttpResponse
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.*
import com.google.gson.JsonParseException
import java.io.ByteArrayOutputStream

import android.util.Base64
import com.google.gson.reflect.TypeToken
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.ParseException


@SuppressLint("SimpleDateFormat")
class Util {
    companion object {

        @Throws(JsonParseException::class)
        fun convertDate(date: String): Date? {
            var d = date.replace("T", " ")
            d = d.replace("Z", "")
            val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            formatter.timeZone = TimeZone.getTimeZone("UTC")
            return try {
                formatter.parse(d)
            } catch (e: ParseException) {
                null
            }
        }

        fun dateToString(date: Date): String {
            val format = SimpleDateFormat("dd/MM/yyyy HH:mm")
            return format.format(date)
        }

        fun loadImage(url: String, imageView: ImageView) {
            Glide.with(Contextor.getInstance().context)
                .load(url)
                .into(imageView)
        }

        fun loadCountryFlag(countryCode: String, imageView: ImageView) {
            loadImage("https://flagcdn.com/w40/${countryCode.lowercase()}.png", imageView)
        }

        fun handleResponseErrorCode(error: String): String {
            val errorObj = handleResponseErrorObj(error)
            when (errorObj.code) {
                "OPEN_LIMIT" -> {
                    return errorObj.message!!
                }
            }
            return errorObj.code ?: ""
        }

        fun handleResponseErrorObj(error: String): HttpResponse<Any> {
            return Gsoner.getInstance().gson.fromJson(error, object : TypeToken<HttpResponse<Any>>() {}.type)
        }

        fun handleResponseErrorMessage(error: String): String {
            val errorObj = handleResponseErrorObj(error)
            return errorObj.message ?: ""
        }

        fun setIconSize(s: String, icon: ImageView) {
            val context = Contextor.getInstance().context
            if (s.startsWith("S", true)) {
                icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_size_s))
            } else if (s.startsWith("M", true)) {
                icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_size_m))
            } else if (s.startsWith("L", true)) {
                icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_size_l))
            } else if (s.startsWith("XL", true)) {
                icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_size_xl))
            } else if (s.startsWith("E", true)) {
                icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_size_e))
            } else {
                icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_size_m))
            }
        }

        fun takeScreenShot(view: View): String {
            val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            val bgDrawable = view.background
            if (bgDrawable != null) {
                bgDrawable.draw(canvas)
            } else {
                canvas.drawColor(Color.WHITE)
            }
            view.draw(canvas)
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
            val byteArray: ByteArray = byteArrayOutputStream.toByteArray()
            return toBase64(byteArray)
        }

        fun toBase64(arr: ByteArray): String {
            val base64 = "data:image/png;base64,${Base64.encodeToString(arr, Base64.DEFAULT)}";
            return base64
        }

        fun formatMoney(value: Float): String {
            val df = DecimalFormat("###,###.##")
            df.roundingMode = RoundingMode.FLOOR
            var roundoff = df.format(value)
            if (roundoff.endsWith(".0")) {
                roundoff = roundoff.replace(".0", "")
            }
            return roundoff
        }

        fun fullscreen(decorView: View?) {
            decorView?.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }

        fun String.firstCharUpper() = replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
        }

    }
}