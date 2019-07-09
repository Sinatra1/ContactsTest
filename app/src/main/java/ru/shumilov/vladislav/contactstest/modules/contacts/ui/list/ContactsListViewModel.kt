package ru.shumilov.vladislav.contactstest.modules.contacts.ui.list

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import ru.shumilov.vladislav.contactstest.modules.contacts.interactors.ContactInteractor
import ru.shumilov.vladislav.contactstest.modules.contacts.models.ContactShort
import java.util.concurrent.TimeUnit


class ContactsListViewModel constructor(
        private val contactInteractor: ContactInteractor) : ViewModel() {

    companion object {
        const val INPUT_FREQUENCY_MILLIS = 1000L //1 sec
    }

    private val mustShowProgress = MutableLiveData<Boolean>().apply { false }
    private val mustShowContactsError = MutableLiveData<Boolean>().apply { false }
    private val contacts = MutableLiveData<List<ContactShort>>().apply { emptyList<ContactShort>() }
    private val compositeDisposable = CompositeDisposable()
    private var inProcess = false

    fun loadContacts() {
        if (inProcess) {
            return
        }

        inProcess = true

        mustShowProgress.value = true

        val request = contactInteractor.getList()

        compositeDisposable.add(request.subscribe({ contacts ->
            onLoadedContactsSuccess(contacts)
        }, { error ->
            onLoadedContactsError()
        }))
    }

    fun loadContactsForce(query: String? = null) {
        if (inProcess) {
            return
        }

        inProcess = true

        val request = contactInteractor.getListFromServer(query)

        compositeDisposable.add(request.subscribe({ contacts ->
            onLoadedContactsSuccess(contacts)
        }, { error ->
            onLoadedContactsError()
        }))
    }

    fun searchContacts(querySubject: PublishSubject<String>) {
        val request = querySubject.debounce(INPUT_FREQUENCY_MILLIS, TimeUnit.MILLISECONDS)
                .switchMap {
                    query->
                    return@switchMap contactInteractor.getListFromLocal(query)
                }

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
        inProcess = false
        mustShowProgress.postValue(false)
        mustShowContactsError.postValue(false)
        this.contacts.postValue(contacts)
    }

    private fun onLoadedContactsError() {
        inProcess = false
        mustShowProgress.postValue(false)
        mustShowContactsError.postValue(true)
    }

    override fun onCleared() {
        compositeDisposable.clear()

        super.onCleared()
    }
}