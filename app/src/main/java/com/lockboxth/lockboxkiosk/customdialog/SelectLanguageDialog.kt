package com.lockboxth.lockboxkiosk.customdialog

import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import com.lockboxth.lockboxkiosk.R
import kotlinx.android.synthetic.main.dialog_select_language.*

class SelectLanguageDialog : DialogFragment() {

    var onSelectedListener: ((String) -> Unit)? = null

    companion object {
        fun newInstance(): SelectLanguageDialog = SelectLanguageDialog().apply {}
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_select_language, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        btnTh.setOnClickListener {
            onSelectedListener?.invoke("th")
        }
        btnEn.setOnClickListener {
            onSelectedListener?.invoke("en")
        }
    }
}