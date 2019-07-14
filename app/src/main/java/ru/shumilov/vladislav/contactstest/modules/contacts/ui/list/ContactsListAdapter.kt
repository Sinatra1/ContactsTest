package ru.shumilov.vladislav.contactstest.modules.contacts.ui.list

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import ru.shumilov.vladislav.contactstest.R
import ru.shumilov.vladislav.contactstest.databinding.ContactsListRowBinding
import ru.shumilov.vladislav.contactstest.modules.contacts.models.ContactShort
import ru.shumilov.vladislav.contactstest.modules.core.ui.RecyclerViewListener


class ContactsListAdapter : RecyclerView.Adapter<ContactsListHolder>(), ContactsListHolder.OnItemClickListener {

    protected var items: ArrayList<ContactShort> = ArrayList()

    protected var listener: RecyclerViewListener<ContactShort>? = null
    protected lateinit var binding: ContactsListRowBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsListHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.contacts_list_row, parent, false)
        return ContactsListHolder(binding)
    }

    override fun onBindViewHolder(holder: ContactsListHolder, position: Int) {
        val item = items[position]

        holder.bind(item)
        holder.setListener(this, items[position], position)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    open fun addItems(items: List<ContactShort>?) {
        this.items = items as ArrayList<ContactShort>
        notifyDataSetChanged()
    }

    override fun onItemClick(item: ContactShort, position: Int) {
        listener?.onItemClick(item, position)
    }

    fun setClickListener(listener: RecyclerViewListener<ContactShort>) {
        this.listener = listener
    }
}