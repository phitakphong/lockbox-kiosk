package com.lockboxth.lockboxkiosk.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lockboxth.lockboxkiosk.R
import com.lockboxth.lockboxkiosk.helpers.Util
import com.lockboxth.lockboxkiosk.http.model.locker.UseTransaction
import kotlinx.android.synthetic.main.recycler_transaction_item.view.*

class TransactionItemRecyclerAdapter(
    private val context: Context,
    private var data: ArrayList<UseTransaction>
) : RecyclerView.Adapter<TransactionItemViewHolder>() {

    private var inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private var selectedLocker: ArrayList<Int> = arrayListOf()

    private var onClick: ((ArrayList<Int>) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionItemViewHolder {
        val inflaterView = inflater.inflate(R.layout.recycler_transaction_item, parent, false)
        return TransactionItemViewHolder(inflaterView)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: TransactionItemViewHolder, @SuppressLint("RecyclerView") position: Int) {

        val item = data[position]
        val checked = selectedLocker.indexOf(item.block_use) >= 0

        holder.bindView(item, checked)
        holder.onClick {

            val checkedItem = selectedLocker.indexOf(item.block_use) >= 0
            if (checkedItem) {
                selectedLocker.remove(item.block_use)
            } else {
                selectedLocker.add(item.block_use)
            }
            notifyItemChanged(position)
            this.onClick!!(selectedLocker)
        }
    }

    fun setOnClick(onClick: ((ArrayList<Int>) -> Unit)) {
        this.onClick = onClick
    }


}

@SuppressLint("SetTextI18n")
class TransactionItemViewHolder(v: View) : RecyclerView.ViewHolder(v) {

    private var view: View = v
    lateinit var item: UseTransaction

    fun bindView(item: UseTransaction, selectedItem: Boolean) {

        this.item = item
        Util.setIconSize(item.locker_no, this.view.imgIcon)
        this.view.tvLockerNo.text = item.locker_no
        this.view.chk.isChecked = selectedItem

    }

    fun onClick(onClick: () -> Unit) {
        this.view.listItem.setOnClickListener {
            onClick()
        }
        this.view.chk.setOnClickListener {
            onClick()
        }
    }

}