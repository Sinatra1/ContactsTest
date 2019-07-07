package ru.shumilov.vladislav.contactstest.modules.contacts.ui.list

import android.view.LayoutInflater
import android.view.View
import ru.shumilov.vladislav.contactstest.modules.contacts.models.Contact
import ru.shumilov.vladislav.contactstest.modules.core.ui.BaseListAdapter


class ContactsListAdapter : BaseListAdapter<Contact>() {

    override fun getViewHolder(inflater: LayoutInflater, view: View): ContactsListHolder {
        return ContactsListHolder(inflater, view)
    }

    override fun onItemClick(item: Contact, position: Int) {
        super.onItemClick(item, position)
    }
}