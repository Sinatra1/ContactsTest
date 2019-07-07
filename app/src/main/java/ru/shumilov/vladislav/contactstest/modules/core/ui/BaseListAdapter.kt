package ru.shumilov.vladislav.contactstest.modules.core.ui

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

abstract class BaseListAdapter<Model> : RecyclerView.Adapter<BaseViewHolder<Model>>(), BaseViewHolder.OnItemClickListener<Model> {

    protected lateinit var items: ArrayList<Model>
    protected var listener: RecyclerViewListener<Model>? = null
        set(listener) {this.listener = listener}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<Model> {
        val layoutInflater = LayoutInflater.from(parent.context)
        return getViewHolder(layoutInflater, parent)
    }

    protected abstract fun getViewHolder(inflater: LayoutInflater, view: View) : BaseViewHolder<Model>

    override fun onBindViewHolder(holder: BaseViewHolder<Model>, position: Int) {
        val item = items[position]

        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    open fun addItems(items: List<Model>) {
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

    override fun onItemClick(item: Model, position: Int) {
        listener?.onItemClick(items[position], position)
    }
}