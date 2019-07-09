package ru.shumilov.vladislav.contactstest.modules.contacts.ui.detail

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

class ContactDetailFragment : Fragment() {

    @Inject
    protected lateinit var viewModelFactory: ContactDetailViewModelFactory

    protected lateinit var viewModel: ContactDetailViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.contact_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        safe {
            app()?.createContactComponent()?.inject(this)

            viewModel = ViewModelProviders.of(this, viewModelFactory)
                    .get(ContactDetailViewModel::class.java)

            //TODO: setListeners()

            if (savedInstanceState == null) {
                //TODO: viewModel.loadContacts()
            }
        }
    }
}