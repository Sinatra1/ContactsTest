package ru.shumilov.vladislav.contactstest.modules.contacts.localRepositories

import ru.shumilov.vladislav.contactstest.core.localRepositories.BaseLocalRepository
import ru.shumilov.vladislav.contactstest.modules.contacts.dao.ContactDao
import ru.shumilov.vladislav.contactstest.modules.contacts.models.Contact
import ru.shumilov.vladislav.contactstest.modules.core.injection.ApplicationScope
import ru.shumilov.vladislav.contactstest.modules.core.preferences.PhoneHelper
import javax.inject.Inject

@ApplicationScope
class ContactLocalRepository @Inject constructor(
        private val contactDao: ContactDao) : BaseLocalRepository<Contact>(contactDao) {

    protected val phoneHelper = PhoneHelper()

    override fun beforeSave(contact: Contact?): Contact? {
        if (contact == null) {
            return null
        }

        contact.phone = phoneHelper.formattedPhoneToOnlyNumbers(contact.phone)

        if (contact.educationPeriod?.id == null) {
            contact.educationPeriod?.id = contact.id
        }

        return contact
    }
}