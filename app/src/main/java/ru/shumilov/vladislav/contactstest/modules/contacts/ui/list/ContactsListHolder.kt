package ru.shumilov.vladislav.contactstest.modules.contacts.ui.list

import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import ru.shumilov.vladislav.contactstest.R
import ru.shumilov.vladislav.contactstest.modules.contacts.models.ContactShort
import ru.shumilov.vladislav.contactstest.modules.core.preferences.PhoneHelper
import ru.shumilov.vladislav.contactstest.modules.core.ui.BaseViewHolder
import javax.inject.Inject

class ContactsListHolder @Inject constructor(
        protected val inflater: LayoutInflater,
        protected var view: View) : BaseViewHolder<ContactShort>(inflater, view) {

    private val nameView: TextView = view.findViewById(R.id.name)
    private val phoneView: TextView = view.findViewById(R.id.phone)
    private val heightView: TextView = view.findViewById(R.id.height)
    private val phoneHelper = PhoneHelper()

    override fun setDataToView() {
        nameView.text = item?.name
        phoneView.text = phoneHelper.onlyNumbersToFormattedPhone(item?.phone)
        heightView.text = item?.height.toString()
    }

}