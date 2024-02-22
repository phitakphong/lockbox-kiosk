package com.lockboxth.lockboxkiosk.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.lockboxth.lockboxkiosk.R
import com.lockboxth.lockboxkiosk.http.model.pudo.PudoReceiverAddress
import kotlinx.android.synthetic.main.recycler_pudo_address_item.view.*

class AddressRecyclerAdapter(
    private val context: Context,
    private var data: List<PudoReceiverAddress>
) : RecyclerView.Adapter<AddressViewHolder>() {

    private var inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private var onClick: ((PudoReceiverAddress) -> Unit)? = null

    private var selectedAddress: PudoReceiverAddress? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        val inflaterView = inflater.inflate(R.layout.recycler_pudo_address_item, parent, false)
        return AddressViewHolder(inflaterView)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        val item = data[position]
        holder.bindView(item, selectedAddress?.address_id)
        holder.onClick {
            if (this.onClick != null) {
                this.onClick!!(item)
            }
            this.setSelected(item)
        }

    }

    fun onClick(onClick: (PudoReceiverAddress) -> Unit) {
        this.onClick = onClick
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setSelected(address: PudoReceiverAddress) {
        this.selectedAddress = address
        notifyDataSetChanged()
    }

    fun setItems(data: List<PudoReceiverAddress>) {
        this.data = data
        notifyDataSetChanged()
    }

}

@SuppressLint("SetTextI18n")
class AddressViewHolder(v: View) : RecyclerView.ViewHolder(v) {

    private var view: View = v
    lateinit var item: PudoReceiverAddress

    fun bindView(item: PudoReceiverAddress, selectedItem: Int? = null) {
        this.item = item
        this.view.tvTitle1.text = item.full_name
        this.view.tvTitle2.text = item.phone
        this.view.tvTitle3.text = item.full_address
        if (item.address_id == selectedItem) {
            this.view.icRdo.setImageDrawable(ContextCompat.getDrawable(this.view.context, R.drawable.ic_checked))
        } else {
            this.view.icRdo.setImageDrawable(ContextCompat.getDrawable(this.view.context, R.drawable.ic_uncheck))
        }

    }

    fun onClick(onClick: (PudoReceiverAddress) -> Unit) {
        this.view.listItem.setOnClickListener {
            onClick(this@AddressViewHolder.item)
        }
    }

}