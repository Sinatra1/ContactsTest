package ru.shumilov.vladislav.contactstest.modules.contacts.localRepositories

import android.text.TextUtils
import ru.shumilov.vladislav.contactstest.core.dao.BaseDao
import ru.shumilov.vladislav.contactstest.core.localRepositories.BaseLocalRepository
import ru.shumilov.vladislav.contactstest.modules.contacts.dao.ContactDao
import ru.shumilov.vladislav.contactstest.modules.contacts.models.Contact
import ru.shumilov.vladislav.contactstest.modules.core.injection.ContactScope
import ru.shumilov.vladislav.contactstest.modules.core.preferences.PhoneHelper
import javax.inject.Inject

@ContactScope
class ContactLocalRepository @Inject constructor(
        private val contactDao: ContactDao
) : BaseLocalRepository<Contact>(contactDao) {

    protected val phoneHelper = PhoneHelper()

    override fun getList(query: String?, whereList: HashMap<String, String>): List<Contact>? {
        val realmQuery = contactDao.getListQuery()

        if (!TextUtils.isEmpty(query)) {
            val phone = phoneHelper.formattedPhoneToOnlyNumbers(query)

            if (!TextUtils.isEmpty(phone)) {
                realmQuery.contains("phone", phone)
                realmQuery.or()
            }

            realmQuery.contains("name_lowercase", query!!.toLowerCase())
        }

        val sortList = hashMapOf(contactDao.getSortKey() to BaseDao.ASC)

        return contactDao.getList(whereList, sortList, realmQuery)
    }

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