package ru.shumilov.vladislav.contactstest.modules.contacts.ui.list

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.shumilov.vladislav.contactstest.R

class ContactsListFragment: Fragment() {

    companion object {
        fun newInstance(): ContactsListFragment {
            return ContactsListFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.contacts_list, container, false)

        return view
    }
}
