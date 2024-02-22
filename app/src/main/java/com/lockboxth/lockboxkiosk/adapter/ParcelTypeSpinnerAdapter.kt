package com.lockboxth.lockboxkiosk.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.lockboxth.lockboxkiosk.R
import com.lockboxth.lockboxkiosk.http.model.pudo.PudoKeyVal


class ParcelTypeSpinnerAdapter(
    context: Context,
    items: List<PudoKeyVal>
) : ArrayAdapter<PudoKeyVal?>(context, 0, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return initView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return initView(position, convertView, parent)
    }

    private fun initView(
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): View {
        var cv = convertView
        if (cv == null) {
            cv = LayoutInflater.from(context).inflate(R.layout.spiner_parcel_type_item, parent, false)
        }

        val currentItem = getItem(position)
        val textViewName = cv!!.findViewById<TextView>(R.id.textView)
        if (currentItem != null) {
            textViewName.text = currentItem.value
        }
        return cv
    }
}