package ru.shumilov.vladislav.contactstest.modules.contacts.ui.list

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import ru.shumilov.vladislav.contactstest.modules.contacts.interactors.ContactInteractor
import ru.shumilov.vladislav.contactstest.modules.contacts.models.Contact
import ru.shumilov.vladislav.contactstest.modules.core.injection.ApplicationScope
import rx.subscriptions.CompositeSubscription
import javax.inject.Inject


class ContactsListViewModel constructor(
        private val contactInteractor: ContactInteractor) : ViewModel() {

    private val mustShowProgress = MutableLiveData<Boolean>().apply { true }
    private val mustShowContactsError = MutableLiveData<Boolean>().apply { false }
    private val contacts = MutableLiveData<List<Contact>>().apply { emptyList<Contact>() }
    private val compositeSubscription = CompositeSubscription()

    fun loadContacts() {
        mustShowProgress.value = true

        val request = contactInteractor.getListFromServer()

        compositeSubscription.add(request.subscribe({ contacts ->
            onLoadedContactsSuccess(contacts)
        }, { error ->
            onLoadedContactsError()
        }))
    }

    fun getProgressState(): LiveData<Boolean> {
        return mustShowProgress
    }

    fun getContactsErrorState(): LiveData<Boolean> {
        return mustShowContactsError
    }

    fun getContacts(): LiveData<List<Contact>> {
        return contacts
    }

    private fun onLoadedContactsSuccess(contacts: List<Contact>) {
        mustShowProgress.postValue(false)
        mustShowContactsError.postValue(false)
        this.contacts.postValue(contacts)
    }

    private fun onLoadedContactsError() {
        mustShowProgress.postValue(false)
        mustShowContactsError.postValue(true)
    }

    override fun onCleared() {
        compositeSubscription.unsubscribe()

        super.onCleared()
    }
}