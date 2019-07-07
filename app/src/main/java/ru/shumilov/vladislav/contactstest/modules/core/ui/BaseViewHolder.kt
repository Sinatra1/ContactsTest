package ru.shumilov.vladislav.contactstest.modules.core.ui

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View

abstract class BaseViewHolder<Model> constructor(private val inflater: LayoutInflater, view: View) : RecyclerView.ViewHolder(view) {

    protected var item: Model? = null

    fun bind(item: Model) {
        this.item = item

        setDataToView()
    }

    abstract fun setDataToView()

    fun setListener(listener: OnItemClickListener<Model>, item: Model, position: Int) {
        itemView.setOnClickListener { listener.onItemClick(item, position) }
    }

    interface OnItemClickListener<Model> {
        fun onItemClick(item: Model, position: Int)
    }
}