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
import android.arch.lifecycle.Observer
import kotlinx.android.synthetic.main.contacts_list.*
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import ru.shumilov.vladislav.contactstest.modules.contacts.models.Contact


class ContactsListFragment @Inject constructor(): Fragment() {

    companion object {
        fun newInstance(): ContactsListFragment {
            return ContactsListFragment()
        }
    }

    @Inject
    protected lateinit var contactsListViewModelFactory: ContactsListViewModelFactory

    protected lateinit var viewModel: ContactsListViewModel
    protected lateinit var contactsListAdapter: ContactsListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.contacts_list, container, false)

        contactsListAdapter = ContactsListAdapter()
        contactsListRecyclerView.layoutManager = LinearLayoutManager(context)
        contactsListRecyclerView.adapter = contactsListAdapter

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        safe {
            app()?.createContactComponent()?.inject(this)

            viewModel = ViewModelProviders.of(this, contactsListViewModelFactory)
                    .get(ContactsListViewModel::class.java)

            setListeners()

            viewModel.loadContacts()
        }
    }

    override fun onDestroyView() {
        safe {
            app()?.clearContactComponent()
        }

        super.onDestroyView()
    }

    private fun setListeners() {
        viewModel.getProgressState().observe(this, Observer { mustShowProgress ->
            mustShowProgress?.let {
                if (mustShowProgress) {
                    showProgress()
                } else {
                    hideProgress()
                }
            }
        })

        viewModel.getContactsErrorState().observe(this, Observer { mustShowContactsError ->
            mustShowContactsError?.let {
                if (mustShowContactsError) {
                    showContactsError()
                }
            }
        })


        viewModel.getContacts().observe(this, Observer { contacts ->

        })
    }

    private fun showProgress() {
        progressBar.visibility = View.VISIBLE
    }

    private fun showContactsError() {
        Snackbar.make(view!!, getString(R.string.load_contacts_error), Snackbar.LENGTH_LONG).show()
    }

    private fun hideProgress() {
        progressBar.visibility = View.GONE
    }

    private fun showContacts(contacts: List<Contact>) {
        contactsListAdapter.addItems(contacts)
    }
}
