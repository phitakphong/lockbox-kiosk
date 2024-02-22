package com.lockboxth.lockboxkiosk.customdialog


import android.os.Bundle
import android.os.CountDownTimer
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.lockboxth.lockboxkiosk.R
import kotlinx.android.synthetic.main.dialog_pudo_print_confirm.*

class ConfirmSenderPrintDialog : DialogFragment() {

    var onOkClickListener: ((Boolean) -> Unit)? = null
    var timeoutCallback: (() -> Unit)? = null
    private var currentState: String? = null

    companion object {
        fun newInstance(): ConfirmSenderPrintDialog = ConfirmSenderPrintDialog().apply {

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog?.setCancelable(false)
        dialog?.window?.decorView?.systemUiVisibility = View.GONE;
        return inflater.inflate(R.layout.dialog_pudo_print_confirm, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        val timeLengthMilli: Long = (60 * 1000)
        val timer = object : CountDownTimer(timeLengthMilli, 1000) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                timeoutCallback?.invoke()
            }
        }
        timer.start()
        btnConfirm.setOnClickListener {
            if (!currentState.isNullOrEmpty()) {
                dismiss()
                timer.cancel()
                onOkClickListener?.invoke(currentState == "Printed")
            }
        }

        btnPrinted.setOnClickListener {
            setOnClick("Printed")
        }

        btnPrint.setOnClickListener {
            setOnClick("Print")
        }

    }

    private fun setOnClick(tag: String) {

        clearState()

        val colorWhite = ContextCompat.getColor(requireContext(), R.color.white)

        val background = ContextCompat.getDrawable(requireContext(), R.drawable.btn_item_yellow)
        val btnId = resources.getIdentifier("btn${tag}", "id", requireContext().packageName!!)
        val imgId = resources.getIdentifier("img${tag}", "id", requireContext().packageName!!)
        val tvId = resources.getIdentifier("tv${tag}", "id", requireContext().packageName!!)

        val btn = dialog!!.findViewById<LinearLayoutCompat>(btnId)
        btn.background = background

        val img = dialog!!.findViewById<ImageView>(imgId)
        img.setColorFilter(colorWhite)

        val tv = dialog!!.findViewById<TextView>(tvId)
        tv.setTextColor(colorWhite)

        currentState = tag

    }

    private fun clearState() {

        val colorBlack = ContextCompat.getColor(requireContext(), R.color.black)
        val defaultBackground = ContextCompat.getDrawable(requireContext(), R.drawable.card_item_white_border_gray_radius_16)

        btnPrinted.background = defaultBackground
        imgPrinted.setColorFilter(colorBlack)
        tvPrinted.setTextColor(colorBlack)

        btnPrint.background = defaultBackground
        imgPrint.setColorFilter(colorBlack)
        tvPrint.setTextColor(colorBlack)

        currentState = null

    }


}