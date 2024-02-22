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
import com.lockboxth.lockboxkiosk.http.model.pudo.PudoKeyVal
import com.lockboxth.lockboxkiosk.http.model.pudo.PudoShipmentItem
import kotlinx.android.synthetic.main.recycler_shipment_item.view.*

class PudoCpPrefixRecyclerAdapter(
    private val context: Context,
    private var data: List<String>,
    private var onClick: ((String) -> Unit)? = null
) : RecyclerView.Adapter<PudoCpPrefixViewHolder>() {

    private var inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    var selectedItem: String? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PudoCpPrefixViewHolder {
        val inflaterView = inflater.inflate(R.layout.recycler_cp_prefix_item, parent, false)
        return PudoCpPrefixViewHolder(inflaterView)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: PudoCpPrefixViewHolder, position: Int) {
        val item = data[position]
        val itemSelected = selectedItem == item
        holder.bindView(item, itemSelected)
        holder.onClick { s ->
            selectedItem = s
            onClick?.invoke(s)
            notifyDataSetChanged()
        }

    }

}

@SuppressLint("SetTextI18n")
class PudoCpPrefixViewHolder(v: View) : RecyclerView.ViewHolder(v) {

    private var view: View = v
    var item: String? = null

    fun bindView(item: String, selected: Boolean) {
        this.item = item

        this.view.tvName.text = this.item
        if (selected) {
            this.view.listItem.background = ContextCompat.getDrawable(this.view.context, R.drawable.card_item_white_border_8)
        } else {
            this.view.listItem.background = ContextCompat.getDrawable(this.view.context, R.drawable.card_item_white_8)
        }

    }

    fun onClick(onClick: (String) -> Unit) {
        this.view.listItem.setOnClickListener {
            onClick(this@PudoCpPrefixViewHolder.item!!)
        }
    }


}