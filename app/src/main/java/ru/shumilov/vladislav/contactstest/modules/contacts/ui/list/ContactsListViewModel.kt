package ru.shumilov.vladislav.contactstest.modules.contacts.ui.list

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import ru.shumilov.vladislav.contactstest.modules.contacts.interactors.ContactInteractor
import ru.shumilov.vladislav.contactstest.modules.contacts.models.ContactShort
import ru.shumilov.vladislav.contactstest.modules.core.preferences.ErrorHelper
import java.util.concurrent.TimeUnit


class ContactsListViewModel constructor(
        private val contactInteractor: ContactInteractor,
        private val errorHelper: ErrorHelper) : ViewModel() {

    companion object {
        const val INPUT_FREQUENCY_MILLIS = 1000L //1 sec
    }

    private val mustShowProgress = MutableLiveData<Boolean>().apply { value = false }
    private val contactsError = MutableLiveData<String>()
    private val contacts = MutableLiveData<List<ContactShort>>().apply { value = ArrayList() }
    private val compositeDisposable = CompositeDisposable()
    private val inProcess = MutableLiveData<Boolean>().apply { value = false }
    private val querySubject = PublishSubject.create<String>()

    fun loadContacts() {
        if (inProcess.value == true) {
            return
        }

        inProcess.postValue(true)

        mustShowProgress.value = true

        val request = contactInteractor.getList()

        compositeDisposable.add(request.subscribe({ contacts ->

        }, { error ->
            onLoadedContactsError(error)
        }, {
            onLoadedContactsSuccess(contactInteractor.getSortedContactsShort())
        }))
    }

    fun loadContactsForce(query: String? = null) {
        if (inProcess.value == true) {
            return
        }

        inProcess.postValue(true)

        val request = contactInteractor.getListFromServer()

        compositeDisposable.add(request.subscribe({ contacts ->

        }, { error ->
            onLoadedContactsError(error)
        }, {
            onLoadedContactsSuccess(contactInteractor.getSortedContactsShort(query))
        }))
    }

    fun searchContacts(query: String) {
        querySubject.onNext(query)
    }

    fun setSearchContactsListener() {
        val request = querySubject.debounce(INPUT_FREQUENCY_MILLIS, TimeUnit.MILLISECONDS)
                .switchMap { query ->
                    inProcess.postValue(true)
                    return@switchMap contactInteractor.getListFromLocal(query)
                }

        compositeDisposable.add(request.subscribe({ contacts ->
            onLoadedContactsSuccess(contacts)
        }, { error ->
            onLoadedContactsError(error)
        }))
    }

    fun getProgressState(): LiveData<Boolean> {
        return mustShowProgress
    }

    fun getContactsError(): LiveData<String> {
        return contactsError
    }

    fun clearContactsError() {
        this.contactsError.postValue(null)
    }

    fun getContacts(): LiveData<List<ContactShort>> {
        return contacts
    }

    fun getInProcessState(): LiveData<Boolean> {
        return inProcess
    }

    fun setQueryToSubject(query: String) {
        querySubject.onNext(query)
    }

    private fun onLoadedContactsSuccess(contacts: List<ContactShort>) {
        inProcess.postValue(false)
        mustShowProgress.postValue(false)
        this.contacts.postValue(contacts)
    }

    private fun onLoadedContactsError(error: Throwable) {
        inProcess.postValue(false)
        mustShowProgress.postValue(false)
        contactsError.postValue(errorHelper.getErrorMessage(error))
    }

    override fun onCleared() {
        compositeDisposable.clear()

        super.onCleared()
    }
}