package ru.shumilov.vladislav.contactstest.modules.contacts.ui.list

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.text.TextUtils
import android.view.*
import androidx.navigation.NavController
import androidx.navigation.Navigation
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.contacts_list.*
import ru.shumilov.vladislav.contactstest.R
import ru.shumilov.vladislav.contactstest.app
import ru.shumilov.vladislav.contactstest.modules.contacts.models.ContactShort
import ru.shumilov.vladislav.contactstest.modules.contacts.ui.detail.ContactDetailFragment
import ru.shumilov.vladislav.contactstest.modules.core.ui.RecyclerViewListener
import javax.inject.Inject


class ContactsListFragment @Inject constructor() : Fragment(), SwipeRefreshLayout.OnRefreshListener, RecyclerViewListener<ContactShort> {
    companion object {
        private const val QUERY_KEY = "query_key"
    }

    @Inject
    protected lateinit var viewModelFactory: ContactsListViewModelFactory

    private lateinit var searchView: SearchView
    private var query: String? = null
    private var inProcess: Boolean? = false
    private val contactsListAdapter: ContactsListAdapter by lazy {
        ContactsListAdapter()
    }
    private val viewModel: ContactsListViewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory)
                .get(ContactsListViewModel::class.java)
    }
    private val navController: NavController by lazy {
        Navigation.findNavController(view!!)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)

        app()?.createContactComponent()?.inject(this)

        setListeners()

        if (savedInstanceState == null) {
            viewModel.loadContacts()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.contacts_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        contactsListRecyclerView.layoutManager = LinearLayoutManager(context)
        contactsListRecyclerView.adapter = contactsListAdapter
        contactsListRecyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
    }

    override fun onResume() {
        super.onResume()

        swipeRefreshLayout.setOnRefreshListener(this)
        contactsListAdapter.setClickListener(this)
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
        app()?.clearContactComponent()

        super.onDestroyView()
    }

    override fun onRefresh() {
        if (inProcess == true) {
            hideRefreshingIcon()
            return
        }

        viewModel.loadContactsForce(query)
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

        viewModel.getInProcessState().observe(this, Observer { inProcess ->
            this.inProcess = inProcess
        })
    }

    private fun showProgress() {
        progressBar.visibility = View.VISIBLE
    }

    private fun showContactsError() {
        Snackbar.make(view!!, R.string.no_network_connection, Snackbar.LENGTH_LONG).show()
    }

    private fun hideProgress() {
        progressBar.visibility = View.GONE

        hideRefreshingIcon()
    }

    private fun hideRefreshingIcon() {
        if (swipeRefreshLayout.isRefreshing) {
            swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun showContacts(contacts: List<ContactShort>?) {
        contactsListAdapter.addItems(contacts)
    }

    override fun onItemClick(contactShort: ContactShort, position: Int) {
        showContactDetail(contactShort.id)
    }

    private fun showContactDetail(contactId: String?) {
        if (contactId == null || inProcess == true) {
            return
        }

        navController.navigate(R.id.contactDetailFragment, ContactDetailFragment.getBundle(contactId))
    }

    override fun onItemAdd(item: ContactShort, position: Int) {

    }
}
