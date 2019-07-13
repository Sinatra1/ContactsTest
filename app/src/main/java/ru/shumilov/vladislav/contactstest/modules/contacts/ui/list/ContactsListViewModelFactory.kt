package ru.shumilov.vladislav.contactstest.modules.contacts.ui.list

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import ru.shumilov.vladislav.contactstest.modules.contacts.interactors.ContactInteractor
import ru.shumilov.vladislav.contactstest.modules.core.preferences.ErrorHelper
import javax.inject.Inject

class ContactsListViewModelFactory @Inject constructor(
        protected val contactInteractor: ContactInteractor,
        protected val errorHelper: ErrorHelper) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(ContactsListViewModel::class.java)) {
            ContactsListViewModel(contactInteractor, errorHelper) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

}