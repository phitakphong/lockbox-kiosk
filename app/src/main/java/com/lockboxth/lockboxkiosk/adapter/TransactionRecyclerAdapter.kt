package com.lockboxth.lockboxkiosk.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lockboxth.lockboxkiosk.R
import com.lockboxth.lockboxkiosk.http.model.locker.UseTransaction
import kotlinx.android.synthetic.main.recycler_transaction_root_item.view.*


class TransactionRecyclerAdapter(
    private val context: Context,
    private var data: Map<String, ArrayList<UseTransaction>>
) : RecyclerView.Adapter<TransactionViewHolder>() {

    private var inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private var selectedLocker: ArrayList<Int> = arrayListOf()
    private var selectedPosition = 0
    private var previousSelected = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val inflaterView = inflater.inflate(R.layout.recycler_transaction_root_item, parent, false)
        return TransactionViewHolder(inflaterView)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: TransactionViewHolder, @SuppressLint("RecyclerView") position: Int) {

        val key = data.keys.toList()[position]
        val items = data[key]!!

        val selected = position == selectedPosition

        holder.bindView(position, items, selected) { s ->
            selectedLocker = s
        }

        if (selected) {
            holder.show()
        } else if (previousSelected == position) {
            holder.hide()
        }

        holder.onClick {
            if (selectedPosition != position) {
                previousSelected = selectedPosition
                selectedPosition = position
                selectedLocker = arrayListOf()
                notifyItemChanged(previousSelected)
            } else {
                previousSelected = -1
                selectedPosition = -1
            }
            notifyItemChanged(position)
        }

    }

    val selectedTransaction: String?
        get() {
            if (selectedPosition >= 0) {
                return this.data.keys.toList()[selectedPosition]
            }
            return null
        }
    val selectedItems: ArrayList<Int>
        get() {
            return selectedLocker
        }

}

@SuppressLint("SetTextI18n")
class TransactionViewHolder(v: View) : RecyclerView.ViewHolder(v) {

    private var view: View = v
    lateinit var items: ArrayList<UseTransaction>

    fun bindView(index: Int, items: ArrayList<UseTransaction>, selectedItem: Boolean, onCheckedChange: (ArrayList<Int>) -> Unit) {

        this.items = items
        this.view.tvTransName.text = "TRANSACTION ${index + 1}"
        if (selectedItem) {
            this.view.listItem.background = ContextCompat.getDrawable(this.view.context, R.drawable.card_item_purple_radius_8)
            this.view.imgIcon.setImageResource(R.drawable.ic_chevron_down)
            this.view.tvTransName.setTextColor(ContextCompat.getColor(this.view.context, R.color.white))
        } else {
            this.view.listItem.background = ContextCompat.getDrawable(this.view.context, R.drawable.card_item_white_2_radius_8)
            this.view.imgIcon.setImageResource(R.drawable.ic_chevron_up)
            this.view.tvTransName.setTextColor(ContextCompat.getColor(this.view.context, R.color.purple))
        }

        this.view.recyclerView.layoutManager = LinearLayoutManager(this.view.context)
        val adapter = TransactionItemRecyclerAdapter(this.view.context, items)
        adapter.setOnClick { s ->
            onCheckedChange(s)
        }
        this.view.recyclerView.adapter = adapter
        this.view.recyclerViewRoot.visibility = View.GONE

    }

    fun show() {
        this.view.recyclerViewRoot.visibility = View.VISIBLE
    }

    fun hide() {
        this.view.recyclerViewRoot.visibility = View.GONE
    }

    fun onClick(onClick: () -> Unit) {
        this.view.listItem.setOnClickListener {
            onClick()
        }
    }

}