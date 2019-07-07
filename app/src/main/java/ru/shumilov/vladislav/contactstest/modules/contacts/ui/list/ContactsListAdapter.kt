package ru.shumilov.vladislav.contactstest.modules.contacts.ui.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.shumilov.vladislav.contactstest.R
import ru.shumilov.vladislav.contactstest.modules.contacts.models.ContactShort
import ru.shumilov.vladislav.contactstest.modules.core.ui.BaseListAdapter


class ContactsListAdapter : BaseListAdapter<ContactShort>() {

    override fun getView(inflater: LayoutInflater, parent: ViewGroup): View {
        return inflater.inflate(R.layout.contacts_list_row, parent, false)
    }

    override fun getViewHolder(inflater: LayoutInflater, view: View): ContactsListHolder {
        return ContactsListHolder(inflater, view)
    }

    override fun onItemClick(item: ContactShort, position: Int) {
        super.onItemClick(item, position)
    }
}