package ru.shumilov.vladislav.contactstest.modules.contacts.ui.detail

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.text.TextUtils
import io.reactivex.disposables.CompositeDisposable
import ru.shumilov.vladislav.contactstest.modules.contacts.interactors.ContactInteractor
import ru.shumilov.vladislav.contactstest.modules.contacts.models.Contact

class ContactDetailViewModel constructor(
        private val contactInteractor: ContactInteractor) : ViewModel() {

    private val mustShowContactError = MutableLiveData<Boolean>().apply { false }
    private val contact = MutableLiveData<Contact>().apply { Contact() }
    private val compositeDisposable = CompositeDisposable()

    fun loadContact(id: String? = null) {
        if (TextUtils.isEmpty(id)) {
            return
        }

        val request = contactInteractor.getById(id!!)

        compositeDisposable.add(request.subscribe({ contact ->
            onLoadedContactSuccess(contact)
        }, {
            onLoadedContactError()
        }))
    }

    fun getContact(): LiveData<Contact> {
        return contact
    }

    fun getContactErrorState(): LiveData<Boolean> {
        return mustShowContactError
    }

    private fun onLoadedContactSuccess(contact: Contact) {
        mustShowContactError.postValue(false)
        this.contact.postValue(contact)
    }

    private fun onLoadedContactError() {
        mustShowContactError.postValue(true)
    }

    override fun onCleared() {
        compositeDisposable.clear()

        super.onCleared()
    }
}