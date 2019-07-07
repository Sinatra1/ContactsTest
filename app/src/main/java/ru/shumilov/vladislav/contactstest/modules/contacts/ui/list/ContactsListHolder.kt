package ru.shumilov.vladislav.contactstest.modules.contacts.ui.list

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import ru.shumilov.vladislav.contactstest.R
import ru.shumilov.vladislav.contactstest.modules.contacts.models.Contact
import ru.shumilov.vladislav.contactstest.modules.core.ui.BaseViewHolder

class ContactsListHolder constructor(inflater: LayoutInflater, view: View) : BaseViewHolder<Contact>(inflater, view) {

    private val nameView: TextView = itemView.findViewById(R.id.name)
    private val phoneView: TextView = itemView.findViewById(R.id.phone)
    private val heightView: TextView = itemView.findViewById(R.id.height)

    override fun setDataToView() {
        nameView.text = item?.name
        phoneView.text = item?.phone
        heightView.text = item?.height.toString()
    }

}