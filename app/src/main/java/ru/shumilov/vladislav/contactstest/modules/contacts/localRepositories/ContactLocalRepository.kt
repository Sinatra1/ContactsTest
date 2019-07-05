package ru.shumilov.vladislav.contactstest.modules.contacts.localRepositories

import ru.shumilov.vladislav.contactstest.core.localRepositories.BaseLocalRepository
import ru.shumilov.vladislav.contactstest.modules.contacts.dao.ContactDao
import ru.shumilov.vladislav.contactstest.modules.contacts.models.Contact
import ru.shumilov.vladislav.contactstest.modules.core.injection.ApplicationScope
import javax.inject.Inject

@ApplicationScope
class ContactLocalRepository @Inject constructor(
        private val contactDao: ContactDao) : BaseLocalRepository<Contact>(contactDao) {

}