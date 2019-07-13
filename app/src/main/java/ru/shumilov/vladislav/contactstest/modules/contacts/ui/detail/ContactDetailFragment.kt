package ru.shumilov.vladislav.contactstest.modules.contacts.ui.detail

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.contact_detail.*
import ru.shumilov.vladislav.contactstest.R
import ru.shumilov.vladislav.contactstest.app
import ru.shumilov.vladislav.contactstest.modules.contacts.models.Contact
import ru.shumilov.vladislav.contactstest.modules.core.functions.safe
import ru.shumilov.vladislav.contactstest.modules.core.preferences.PhoneHelper
import ru.shumilov.vladislav.contactstest.modules.core.preferences.TextHelper
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

    private val phoneHelper = PhoneHelper()
    private val textHelper = TextHelper()
    private lateinit var contact: Contact
    private val viewModel: ContactDetailViewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory)
                .get(ContactDetailViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.contact_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        safe {
            app()?.createContactComponent()?.inject(this)
        }

        setListeners()

        if (savedInstanceState == null) {
            loadContact()
        }
    }

    private fun loadContact() {
        if (arguments == null) {
            return
        }

        val contactId = arguments?.getString(CONTACT_ID_KEY)

        if (TextUtils.isEmpty(contactId)) {
            return
        }

        viewModel.loadContact(contactId)
    }

    private fun setListeners() {
        viewModel.getContactErrorState().observe(this, Observer { mustShowContactsError ->
            mustShowContactsError?.let {
                if (mustShowContactsError) {
                    showContactError()
                }
            }
        })


        viewModel.getContact().observe(this, Observer { contact ->
            showContact(contact)
        })

        (phone as TextView).setOnClickListener(View.OnClickListener {
            phoneHelper.dialPhoneNumber(contact.phone, this)
        })
    }

    private fun showContactError() {
        Snackbar.make(view!!, getString(R.string.load_contact_error), Snackbar.LENGTH_LONG).show()
    }

    private fun showContact(contact: Contact?) {
        if (contact == null) {
            return
        }

        this.contact = contact

        name.text = contact.name
        phone.text = phoneHelper.onlyNumbersToFormattedPhone(contact.phone)
        temperament.text = textHelper.getTextWithFirstUpper(contact.temperament)
        educationPeriod.text = contact.educationPeriod?.toString()
        biography.text = contact.biography
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
}