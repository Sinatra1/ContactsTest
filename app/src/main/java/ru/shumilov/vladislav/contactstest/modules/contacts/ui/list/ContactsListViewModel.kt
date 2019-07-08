package ru.shumilov.vladislav.contactstest.modules.contacts.ui.list

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import ru.shumilov.vladislav.contactstest.modules.contacts.interactors.ContactInteractor
import ru.shumilov.vladislav.contactstest.modules.contacts.models.ContactShort


class ContactsListViewModel constructor(
        private val contactInteractor: ContactInteractor) : ViewModel() {

    private val mustShowProgress = MutableLiveData<Boolean>().apply { true }
    private val mustShowContactsError = MutableLiveData<Boolean>().apply { false }
    private val contacts = MutableLiveData<List<ContactShort>>().apply { emptyList<ContactShort>() }
    private val compositeDisposable = CompositeDisposable()

    fun loadContacts() {
        mustShowProgress.value = true

        val request = contactInteractor.getList()

        compositeDisposable.add(request.subscribe({ contacts ->
            onLoadedContactsSuccess(contacts)
        }, { error ->
            onLoadedContactsError()
        }))
    }

    fun loadContactsForce() {
        val request = contactInteractor.getListFromServer()

        compositeDisposable.add(request.subscribe({ contacts ->
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

    fun getContacts(): LiveData<List<ContactShort>> {
        return contacts
    }

    private fun onLoadedContactsSuccess(contacts: List<ContactShort>) {
        mustShowProgress.postValue(false)
        mustShowContactsError.postValue(false)
        this.contacts.postValue(contacts)
    }

    private fun onLoadedContactsError() {
        mustShowProgress.postValue(false)
        mustShowContactsError.postValue(true)
    }

    override fun onCleared() {
        compositeDisposable.clear()

        super.onCleared()
    }
}