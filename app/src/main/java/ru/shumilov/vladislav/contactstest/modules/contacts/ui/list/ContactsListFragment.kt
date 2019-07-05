package ru.shumilov.vladislav.contactstest.modules.contacts.ui.list

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.shumilov.vladislav.contactstest.R
import ru.shumilov.vladislav.contactstest.app
import ru.simpls.brs2.commons.functions.safe
import javax.inject.Inject


class ContactsListFragment @Inject constructor(): Fragment() {

    companion object {
        fun newInstance(): ContactsListFragment {
            return ContactsListFragment()
        }
    }

    private lateinit var viewModel: ContactsListViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.contacts_list, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        safe {
            app()?.createContactComponent()?.inject(this)

            viewModel = ViewModelProviders.of(this).get(ContactsListViewModel::class.java)
        }
    }

    override fun onDestroyView() {
        safe {
            app()?.clearContactComponent()
        }

        super.onDestroyView()
    }
}
