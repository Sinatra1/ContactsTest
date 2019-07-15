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
import android.databinding.ObservableField
import androidx.navigation.NavController
import ru.shumilov.vladislav.contactstest.R
import ru.shumilov.vladislav.contactstest.modules.contacts.ui.detail.ContactDetailFragment


class ContactsListViewModel constructor(
        private val contactInteractor: ContactInteractor,
        private val errorHelper: ErrorHelper) : ViewModel() {

    companion object {
        const val INPUT_FREQUENCY_MILLIS = 1000L //1 sec
    }

    val isShownProgress = ObservableField<Boolean>().apply { false }
    private val isShowRefreshingIcon = MutableLiveData<Boolean>().apply { value = false }
    private val contactsError = MutableLiveData<String>()
    private val contacts = MutableLiveData<List<ContactShort>>().apply { value = ArrayList() }
    private val compositeDisposable = CompositeDisposable()
    private var inProcess = false
    private var querySubject = PublishSubject.create<String>()
    private lateinit var navController: NavController

    fun loadContacts() {
        if (inProcess) {
            return
        }

        inProcess = true
        isShownProgress.set(inProcess)

        val request = contactInteractor.getList()

        compositeDisposable.add(request.subscribe({ contacts ->
            if (!contactInteractor.isGettingListFromServer) {
                onLoadedContactsSuccess(contactInteractor.getSortedContactsShort())
            }
        }, { error ->
            onLoadedContactsError(error)
        }, {
            if (contactInteractor.getListFromServerError != null) {
                onLoadedContactsError(contactInteractor.getListFromServerError!!)
                return@subscribe
            }

            onLoadedContactsSuccess(contactInteractor.getSortedContactsShort())
        }))
    }

    fun loadContactsForce(query: String? = null) {
        if (inProcess) {
            if (!isShowRefreshingIcon.value!!) {
                isShowRefreshingIcon.postValue(false)
            }

            return
        }

        inProcess = true
        isShowRefreshingIcon.postValue(inProcess)

        val request = contactInteractor.getListFromServer()

        compositeDisposable.add(request.subscribe({ contacts ->

        }, { error ->
            onLoadedContactsError(error)
        }, {
            if (contactInteractor.getListFromServerError != null) {
                onLoadedContactsError(contactInteractor.getListFromServerError!!)
                return@subscribe
            }

            onLoadedContactsSuccess(contactInteractor.getSortedContactsShort(query))
        }))
    }

    fun searchContacts(query: String) {
        querySubject.onNext(query)
    }

    fun showContactDetail(contactId: String?) {
        if (contactId == null || inProcess) {
            return
        }

        navController.navigate(R.id.contactDetailFragment, ContactDetailFragment.getBundle(contactId))
    }

    fun setSearchContactsListener() {
        querySubject = PublishSubject.create<String>()
        val request = querySubject.debounce(INPUT_FREQUENCY_MILLIS, TimeUnit.MILLISECONDS)
                .switchMap { query ->
                    inProcess = true
                    return@switchMap contactInteractor.getListFromLocal(query)
                }

        compositeDisposable.add(request.subscribe({ contacts ->
            onLoadedContactsSuccess(contacts)
        }, { error ->
            onLoadedContactsError(error)
        }))
    }

    fun getContactsError(): LiveData<String> {
        return contactsError
    }

    fun getIsShownRefreshingIcon() : LiveData<Boolean> {
        return isShowRefreshingIcon
    }

    fun clearContactsError() {
        this.contactsError.postValue(null)
    }

    fun getContacts(): LiveData<List<ContactShort>> {
        return contacts
    }

    fun setNavController(navController: NavController) {
        this.navController = navController
    }

    private fun onLoadedContactsSuccess(contacts: List<ContactShort>) {
        inProcess = false
        isShownProgress.set(inProcess)
        isShowRefreshingIcon.postValue(inProcess)
        this.contacts.postValue(contacts)
    }

    private fun onLoadedContactsError(error: Throwable) {
        inProcess = false
        isShownProgress.set(inProcess)
        isShowRefreshingIcon.postValue(inProcess)
        contactsError.postValue(errorHelper.getErrorMessage(error))
    }

    override fun onCleared() {
        compositeDisposable.clear()

        super.onCleared()
    }
}