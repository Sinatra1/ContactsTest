package ru.shumilov.vladislav.contactstest.modules.contacts.ui.detail

import android.arch.lifecycle.ViewModel
import ru.shumilov.vladislav.contactstest.modules.contacts.interactors.ContactInteractor

class ContactDetailViewModel constructor(
        private val contactInteractor: ContactInteractor) : ViewModel() {
}