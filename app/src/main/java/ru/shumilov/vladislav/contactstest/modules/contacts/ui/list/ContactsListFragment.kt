package ru.shumilov.vladislav.contactstest.modules.contacts.ui.list

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
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
import kotlinx.android.synthetic.main.contacts_list.*
import ru.shumilov.vladislav.contactstest.R
import ru.shumilov.vladislav.contactstest.app
import ru.shumilov.vladislav.contactstest.databinding.ContactsListBinding
import ru.shumilov.vladislav.contactstest.modules.contacts.models.ContactShort
import ru.shumilov.vladislav.contactstest.modules.core.ui.RecyclerViewListener
import javax.inject.Inject


class ContactsListFragment @Inject constructor() : Fragment(), SwipeRefreshLayout.OnRefreshListener, RecyclerViewListener<ContactShort> {
    companion object {
        private const val QUERY_KEY = "query_key"
    }

    @Inject
    protected lateinit var viewModelFactory: ContactsListViewModelFactory
    private lateinit var binding: ContactsListBinding

    private lateinit var searchView: SearchView
    private var query: String? = null
    private val contactsListAdapter: ContactsListAdapter by lazy {
        ContactsListAdapter()
    }
    private val viewModel: ContactsListViewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory)
                .get(ContactsListViewModel::class.java)
    }
    private lateinit var navController: NavController

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
        binding = DataBindingUtil.inflate(inflater, R.layout.contacts_list, container, false)

        binding.viewModel = viewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        contactsListRecyclerView.layoutManager = LinearLayoutManager(context)
        contactsListRecyclerView.adapter = contactsListAdapter
        contactsListRecyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

        navController = Navigation.findNavController(view)
        viewModel.setNavController(navController)
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

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(query: String): Boolean {
                this@ContactsListFragment.query = query
                viewModel.searchContacts(query)
                return true
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                this@ContactsListFragment.query = query
                viewModel.searchContacts(query)
                return true
            }
        })

        viewModel.setSearchContactsListener()
    }

    override fun onDestroyView() {
        app()?.clearContactComponent()

        super.onDestroyView()
    }

    override fun onRefresh() {
        viewModel.loadContactsForce(query)
    }

    private fun setListeners() {
        viewModel.getContactsError().observe(this, Observer { contactsError ->
            showContactsError(contactsError)
        })

        viewModel.getContacts().observe(this, Observer { contacts ->
            showContacts(contacts)
        })

        viewModel.getIsShownRefreshingIcon().observe(this, Observer { isShownRefreshingIcon ->
            swipeRefreshLayout.isRefreshing = isShownRefreshingIcon!!
        })
    }

    private fun showContactsError(contactsError: String?) {
        if (contactsError == null) {
            return
        }

        Snackbar.make(view!!, contactsError!!, Snackbar.LENGTH_LONG).show()
        viewModel.clearContactsError()
    }

    private fun showContacts(contacts: List<ContactShort>?) {
        contactsListAdapter.addItems(contacts)
    }

    override fun onItemClick(contactShort: ContactShort, position: Int) {
        viewModel.showContactDetail(contactShort.id)
    }

    override fun onItemAdd(item: ContactShort, position: Int) {

    }
}
