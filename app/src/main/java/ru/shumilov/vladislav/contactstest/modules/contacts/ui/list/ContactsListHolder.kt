package ru.shumilov.vladislav.contactstest.modules.contacts.ui.list

import android.support.v7.widget.RecyclerView
import ru.shumilov.vladislav.contactstest.databinding.ContactsListRowBinding
import ru.shumilov.vladislav.contactstest.modules.contacts.models.ContactShort
import ru.shumilov.vladislav.contactstest.modules.core.preferences.PhoneHelper
import android.databinding.ObservableField

class ContactsListHolder constructor(private val binding: ContactsListRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

    val phoneHelper = PhoneHelper()
    var item = ObservableField<ContactShort>()

    fun bind(item: ContactShort) {
        this.item.set(item)
        binding.viewHolder = this
        binding.executePendingBindings()
    }

    fun setListener(listener: OnItemClickListener, item: ContactShort, position: Int) {
        itemView.setOnClickListener { listener.onItemClick(item, position) }
    }

    interface OnItemClickListener {
        fun onItemClick(item: ContactShort, position: Int)
    }
}