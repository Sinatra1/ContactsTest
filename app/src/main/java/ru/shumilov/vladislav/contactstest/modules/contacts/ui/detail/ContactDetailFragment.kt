package ru.shumilov.vladislav.contactstest.modules.contacts.ui.detail

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.contact_detail.*
import ru.shumilov.vladislav.contactstest.R
import ru.shumilov.vladislav.contactstest.app
import ru.shumilov.vladislav.contactstest.databinding.ContactDetailBinding
import ru.shumilov.vladislav.contactstest.modules.contacts.models.Contact
import ru.shumilov.vladislav.contactstest.modules.core.preferences.PhoneHelper
import javax.inject.Inject


class ContactDetailFragment : Fragment() {

    companion object {
        private const val CONTACT_ID_KEY = "contact_id_key"

        fun getBundle(contactId: String): Bundle {
            val bundle = Bundle()
            bundle.putString(CONTACT_ID_KEY, contactId)

            return bundle
        }
    }

    @Inject
    protected lateinit var viewModelFactory: ContactDetailViewModelFactory
    private lateinit var binding: ContactDetailBinding

    private val phoneHelper = PhoneHelper()
    private lateinit var contact: Contact
    private val viewModel: ContactDetailViewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory)
                .get(ContactDetailViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.contact_detail, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        app()?.createContactComponent()?.inject(this)

        setListeners()

        binding.viewModel = viewModel

        if (savedInstanceState == null) {
            loadContact()
        }
    }

    private fun loadContact() {
        if (arguments == null) {
            return
        }

        val contactId = arguments?.getString(CONTACT_ID_KEY)

        viewModel.loadContact(contactId)
    }

    private fun setListeners() {
        viewModel.getContactError().observe(this, Observer { contactError ->
            showContactError(contactError)
        })

        phone.setOnClickListener {
            phoneHelper.dialPhoneNumber(contact.phone, this)
        }
    }

    private fun showContactError(contactError: String?) {
        if (contactError == null) {
            return
        }

        Snackbar.make(view!!, contactError!!, Snackbar.LENGTH_LONG).show()
        viewModel.clearContactError()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode!!, permissions!!, grantResults!!)

        phoneHelper.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onStop() {
        phoneHelper.onDestroy()

        super.onStop()
    }

    override fun onDestroyView() {
        app()?.clearContactComponent()

        super.onDestroyView()
    }
}