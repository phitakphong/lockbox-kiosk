package com.lockboxth.lockboxkiosk.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.lockboxth.lockboxkiosk.R
import com.lockboxth.lockboxkiosk.helpers.Util
import com.lockboxth.lockboxkiosk.http.model.pudo.PudoParcelListItem
import kotlinx.android.synthetic.main.recycler_pudo_pickup_item.view.*

class PudoPickupRecyclerAdapter(
    private val context: Context,
    private var data: List<PudoParcelListItem>
) : RecyclerView.Adapter<PudoPickupViewHolder>() {

    private var inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PudoPickupViewHolder {
        val inflaterView = inflater.inflate(R.layout.recycler_pudo_pickup_item, parent, false)
        return PudoPickupViewHolder(inflaterView)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: PudoPickupViewHolder, position: Int) {
        val item = data[position]
        holder.bindView(item)
    }

    fun updateDataSource(data: List<PudoParcelListItem>) {
        this.data = data
        notifyDataSetChanged()
    }


}

@SuppressLint("SetTextI18n")
class PudoPickupViewHolder(v: View) : RecyclerView.ViewHolder(v) {

    private var view: View = v

    fun bindView(item: PudoParcelListItem) {

        this.view.tvSize.text = "Size " + item.locker_size
        this.view.tvText1.text = "Loker No. " + item.locker_no
        this.view.tvText2.text = item.tracking_number
        this.view.tvText3.text = "• " + item.status_text
        if (item.status == "confirm") {
            this.view.tvText3.setTextColor(ContextCompat.getColor(this.view.context, R.color.green))
        } else {
            this.view.tvText3.setTextColor(ContextCompat.getColor(this.view.context, R.color.teal_700))
        }

        Util.setIconSize(item.locker_size, this.view.imgLockerSize)

    }


}