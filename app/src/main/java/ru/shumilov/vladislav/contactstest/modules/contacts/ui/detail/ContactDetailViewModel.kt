package ru.shumilov.vladislav.contactstest.modules.contacts.ui.detail

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.text.TextUtils
import io.reactivex.disposables.CompositeDisposable
import ru.shumilov.vladislav.contactstest.modules.contacts.interactors.ContactInteractor
import ru.shumilov.vladislav.contactstest.modules.contacts.models.Contact
import ru.shumilov.vladislav.contactstest.modules.core.preferences.PhoneHelper
import ru.shumilov.vladislav.contactstest.modules.core.preferences.TextHelper
import android.databinding.ObservableField
import ru.shumilov.vladislav.contactstest.modules.core.preferences.ErrorHelper

class ContactDetailViewModel constructor(
        private val contactInteractor: ContactInteractor,
        private val errorHelper: ErrorHelper) : ViewModel() {

    private val contactError = MutableLiveData<String>()
    private val compositeDisposable = CompositeDisposable()
    val contact = ObservableField<Contact>()
    var contactLiveData = MutableLiveData<Contact>()
    val phoneHelper = PhoneHelper()
    val textHelper = TextHelper()

    fun loadContact(id: String? = null) {
        if (TextUtils.isEmpty(id)) {
            return
        }

        val request = contactInteractor.getById(id!!)

        compositeDisposable.add(request.subscribe({ contact ->
            onLoadedContactSuccess(contact)
        }, { error ->
            onLoadedContactError(error)
        }))
    }

    fun getContact(): LiveData<Contact> {
        return contactLiveData
    }

    fun getContactError(): LiveData<String> {
        return contactError
    }

    fun clearContactError() {
        this.contactError.postValue(null)
    }

    private fun onLoadedContactSuccess(contact: Contact) {
        this.contact.set(contact)
        contactLiveData.postValue(contact)
    }

    private fun onLoadedContactError(error: Throwable) {
        contactError.postValue(errorHelper.getErrorMessage(error))
    }

    override fun onCleared() {
        compositeDisposable.clear()

        super.onCleared()
    }
}