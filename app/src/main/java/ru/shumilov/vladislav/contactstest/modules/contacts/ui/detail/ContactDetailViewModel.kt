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

class ContactDetailViewModel constructor(
        private val contactInteractor: ContactInteractor) : ViewModel() {

    private val mustShowContactError = MutableLiveData<Boolean>().apply { false }
    var contact = ObservableField<Contact>()
    private val compositeDisposable = CompositeDisposable()
    val phoneHelper = PhoneHelper()
    val textHelper = TextHelper()

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

    fun getContactErrorState(): LiveData<Boolean> {
        return mustShowContactError
    }

    private fun onLoadedContactSuccess(contact: Contact) {
        mustShowContactError.postValue(false)
        this.contact.set(contact)
    }

    private fun onLoadedContactError() {
        mustShowContactError.postValue(true)
    }

    override fun onCleared() {
        compositeDisposable.clear()

        super.onCleared()
    }
}