package ru.shumilov.vladislav.contactstest.modules.contacts.ui.list

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.text.TextUtils
import android.view.*
import kotlinx.android.synthetic.main.contacts_list.*
import ru.shumilov.vladislav.contactstest.R
import ru.shumilov.vladislav.contactstest.app
import ru.shumilov.vladislav.contactstest.modules.contacts.models.ContactShort
import ru.simpls.brs2.commons.functions.safe
import javax.inject.Inject
import io.reactivex.subjects.PublishSubject




class ContactsListFragment @Inject constructor(): Fragment(), SwipeRefreshLayout.OnRefreshListener {

    companion object {
        private const val QUERY_KEY = "query_key"
    }

    @Inject
    protected lateinit var contactsListViewModelFactory: ContactsListViewModelFactory

    protected lateinit var viewModel: ContactsListViewModel
    protected lateinit var contactsListAdapter: ContactsListAdapter
    protected lateinit var searchView: SearchView
    protected var query: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.contacts_list, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        contactsListAdapter = ContactsListAdapter()
        contactsListRecyclerView.layoutManager = LinearLayoutManager(context)
        contactsListRecyclerView.adapter = contactsListAdapter

        safe {
            app()?.createContactComponent()?.inject(this)

            viewModel = ViewModelProviders.of(this, contactsListViewModelFactory)
                    .get(ContactsListViewModel::class.java)

            setListeners()

            if (savedInstanceState == null) {
                viewModel.loadContacts()
            }
        }
    }

    override fun onResume() {
        super.onResume()

        swipeRefreshLayout.setOnRefreshListener(this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putString(QUERY_KEY, query)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        if (savedInstanceState == null) {
            return
        }

        val query = savedInstanceState.getString(QUERY_KEY)

        if (!TextUtils.isEmpty(query)) {
            this.query = query
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.contacts_list_menu, menu)

        initSearchListener(menu)
    }

    private fun initSearchListener(menu: Menu) {
        searchView = menu.findItem(R.id.searchView).actionView as SearchView

        if (!TextUtils.isEmpty(query)) {
            searchView.setQuery(query, false)
            searchView.isIconified = false
        }

        val subject = PublishSubject.create<String>()

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(query: String): Boolean {
                this@ContactsListFragment.query = query
                subject.onNext(query)
                return true
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                this@ContactsListFragment.query = query
                subject.onNext(query)
                return true
            }
        })

        viewModel.searchContacts(subject)
    }

    override fun onDestroyView() {
        safe {
            app()?.clearContactComponent()
        }

        super.onDestroyView()
    }

    override fun onRefresh() {
        safe {
            viewModel.loadContactsForce(query)
        }
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
            showContacts(contacts)
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

        if (swipeRefreshLayout.isRefreshing) {
            swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun showContacts(contacts: List<ContactShort>?) {
        contactsListAdapter.addItems(contacts)
    }
}
