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
import com.lockboxth.lockboxkiosk.http.model.CountryItem
import kotlinx.android.synthetic.main.recycler_country_item.view.*

class CountryRecyclerAdapter(
    private val context: Context,
    private var data: List<CountryItem>
) : RecyclerView.Adapter<CountryListViewHolder>() {

    private var inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private var onClick: ((CountryItem) -> Unit)? = null

    private var selectedCountry: CountryItem? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountryListViewHolder {
        val inflaterView = inflater.inflate(R.layout.recycler_country_item, parent, false)
        return CountryListViewHolder(inflaterView)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: CountryListViewHolder, position: Int) {
        val item = data[position]
        holder.bindView(position, item, selectedCountry != null && selectedCountry!!.code == item.code)
        holder.onClick {
            if (this.onClick != null) {
                this.onClick!!(item)
            }
            this.setSelectedCountry(item)
        }

    }

    fun onClick(onClick: (CountryItem) -> Unit) {
        this.onClick = onClick
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setSelectedCountry(country: CountryItem) {
        this.selectedCountry = country
        notifyDataSetChanged()
    }

    fun setItems(data: List<CountryItem>) {
        this.data = data
        notifyDataSetChanged()
    }

}

@SuppressLint("SetTextI18n")
class CountryListViewHolder(v: View) : RecyclerView.ViewHolder(v) {

    private var view: View = v
    lateinit var item: CountryItem

    fun bindView(index: Int, item: CountryItem, selectedItem: Boolean) {
        this.item = item
        Util.loadCountryFlag(item.code!!, this.view.imgCountryFlag)

        this.view.tvCountryName.text = "${item.dial_code} ${item.name}"

        if (selectedItem) {
            this.view.imgChecked.visibility = View.VISIBLE
            this.view.listItem.background = ContextCompat.getDrawable(this.view.context, R.drawable.card_item_white_2_radius_8)
        } else {
            this.view.imgChecked.visibility = View.GONE
            this.view.listItem.background = null
        }

    }

    fun onClick(onClick: (CountryItem) -> Unit) {
        this.view.listItem.setOnClickListener {
            onClick(this@CountryListViewHolder.item)
        }
    }

}