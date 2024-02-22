package com.lockboxth.lockboxkiosk.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.lockboxth.lockboxkiosk.R
import com.lockboxth.lockboxkiosk.helpers.TransactionType
import com.lockboxth.lockboxkiosk.helpers.Util
import com.lockboxth.lockboxkiosk.http.model.pudo.PudoShipmentItem
import kotlinx.android.synthetic.main.recycler_shipment_item.view.*

class PudoShipmentRecyclerAdapter(
    private val context: Context,
    private var data: ArrayList<PudoShipmentItem>,
    private val type: TransactionType
) : RecyclerView.Adapter<ShipmentViewHolder>() {

    private var inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    var selectedItem: PudoShipmentItem? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShipmentViewHolder {
        val inflaterView = if (type == TransactionType.PUDO_SENDER_WALKIN) {
            inflater.inflate(R.layout.recycler_shipment_walkin_item, parent, false)
        } else {
            inflater.inflate(R.layout.recycler_shipment_item, parent, false)
        }
        return ShipmentViewHolder(inflaterView)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ShipmentViewHolder, position: Int) {
        val item = data[position]
        val itemSelected = selectedItem == item
        holder.bindView(item, itemSelected, type)
        holder.onClick { s ->
            selectedItem = s
            notifyDataSetChanged()
        }

    }

}

@SuppressLint("SetTextI18n")
class ShipmentViewHolder(v: View) : RecyclerView.ViewHolder(v) {

    private var view: View = v
    var item: PudoShipmentItem? = null

    fun bindView(item: PudoShipmentItem, selected: Boolean, type: TransactionType) {
        this.item = item

        Util.loadImage(item.image_url, this.view.img)
        this.view.tvName.text = this.item!!.name
        if (selected) {
            this.view.listItem.background = ContextCompat.getDrawable(this.view.context, R.drawable.card_item_white_border)
        } else {
            this.view.listItem.background = ContextCompat.getDrawable(this.view.context, R.drawable.card_item_white)
        }
        if (type == TransactionType.PUDO_SENDER_WALKIN) {
            this.view.findViewById<TextView>(R.id.tvPrice).text = "฿${this.item!!.fee}"
        }

    }

    fun onClick(onClick: (PudoShipmentItem) -> Unit) {
        this.view.listItem.setOnClickListener {
            onClick(this@ShipmentViewHolder.item!!)
        }
    }


}