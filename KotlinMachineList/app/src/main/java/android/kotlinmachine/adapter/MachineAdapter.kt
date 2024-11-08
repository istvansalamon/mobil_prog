package android.kotlinmachine.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.kotlinmachine.MainActivity
import android.kotlinmachine.R
import android.kotlinmachine.adapter.MachineAdapter.ViewHolder
import android.kotlinmachine.data.AppDatabase
import android.kotlinmachine.data.MachineItem
import android.kotlinmachine.touch.MachineTouchHelperAdapter
import kotlinx.android.synthetic.main.row_item.view.*
import java.util.*

class MachineAdapter : RecyclerView.Adapter<ViewHolder>, MachineTouchHelperAdapter {

    private val items = mutableListOf<MachineItem>()
    private val context: Context

    constructor(context: Context, items: List<MachineItem>) : super() {
        this.context = context
        this.items.addAll(items)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
                R.layout.row_item, parent, false
        )
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvName.text = items[position].name
        holder.tvQuantity.text = items[position].quantity.toString()
        holder.cbAktiv.isChecked = items[position].aktiv
        holder.tvDescript.text =items[position].descript
        holder.tvFault.text =items[position].descript

        holder.btnDelete.setOnClickListener {
            deleteItem(holder.adapterPosition)
        }
        holder.btnEdit.setOnClickListener {
            (holder.itemView.context as MainActivity).showEditItemDialog(
                    items[holder.adapterPosition])
        }
        holder.cbAktiv.setOnClickListener {
            items[position].aktiv = holder.cbAktiv.isChecked
            val dbThread = Thread {
                AppDatabase.getInstance(context).machineItemDAO().updateItem(items[position])
            }
            dbThread.start()
        }
    }
    /*Új elem hozzáadásakor hívódik meg*/
    fun addItem(item: MachineItem) {
        items.add(item)
        notifyItemInserted(items.lastIndex)
    }
    /*Elem törlésekor hívódik meg. Az adatbázisból törli az elemet (DAO-n keresztül)*/
    fun deleteItem(position: Int) {
        val dbThread = Thread {
            AppDatabase.getInstance(context).machineItemDAO().deleteItem(
                    items[position])
            (context as MainActivity).runOnUiThread{
                items.removeAt(position)
                notifyItemRemoved(position)
            }
        }
        dbThread.start()
    }
    /*Update-kor hívódik meg*/
    fun updateItem(item: MachineItem) {
        val idx = items.indexOf(item)
        items[idx] = item
        notifyItemChanged(idx)
    }

    override fun onItemDismissed(position: Int) {
        deleteItem(position)
    }

    override fun onItemMoved(fromPosition: Int, toPosition: Int) {
        Collections.swap(items, fromPosition, toPosition)

        notifyItemMoved(fromPosition, toPosition)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        /*a MachineItem elemek, ide kell a bővítés új taggal*/
        /*Itt a gombokat, checkboxot is lekérjük*/
        val tvName: TextView = itemView.tvName
        val tvQuantity: TextView = itemView.tvQuantity
        val cbAktiv: CheckBox = itemView.cbAktive
        val btnDelete: Button = itemView.btnDelete
        val btnEdit: Button = itemView.btnEdit
        val tvDescript:TextView=itemView.tvDescript
        val tvFault:TextView=itemView.tvFault
    }
}