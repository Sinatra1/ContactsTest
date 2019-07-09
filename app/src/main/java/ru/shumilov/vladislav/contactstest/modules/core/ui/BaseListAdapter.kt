package ru.shumilov.vladislav.contactstest.modules.core.ui

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

abstract class BaseListAdapter<Model> : RecyclerView.Adapter<BaseViewHolder<Model>>(), BaseViewHolder.OnItemClickListener<Model> {

    protected var items: ArrayList<Model> = ArrayList()

    protected var listener: RecyclerViewListener<Model>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<Model> {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = getView(layoutInflater, parent)
        return getViewHolder(layoutInflater, view)
    }

    protected abstract fun getViewHolder(inflater: LayoutInflater, view: View) : BaseViewHolder<Model>

    protected abstract fun getView(inflater: LayoutInflater, parent: ViewGroup): View

    override fun onBindViewHolder(holder: BaseViewHolder<Model>, position: Int) {
        val item = items[position]

        holder.bind(item)
        holder.setListener(this, items[position], position)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    open fun addItems(items: List<Model>?) {
        this.items = items as ArrayList<Model>
        notifyDataSetChanged()
    }

    open fun addItem(item: Model?) {
        if (item == null) {
            return
        }

        items.add(item)
        notifyDataSetChanged()

        listener?.onItemAdd(item, items.size - 1)
    }

    protected fun removeItem(position: Int) {
        items.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, items.size)
    }

    override fun onItemClick(item: Model, position: Int) {
        listener?.onItemClick(items[position], position)
    }

    fun setClickListener(listener: RecyclerViewListener<Model>) {
        this.listener = listener
    }
}