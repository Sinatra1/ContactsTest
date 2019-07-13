package ru.shumilov.vladislav.contactstest.modules.contacts.remoteRepositories

import io.reactivex.Observable
import ru.shumilov.vladislav.contactstest.modules.contacts.api.ContactApi
import ru.shumilov.vladislav.contactstest.modules.contacts.models.Contact
import ru.shumilov.vladislav.contactstest.modules.contacts.models.ContactShort
import ru.shumilov.vladislav.contactstest.modules.core.injection.ContactScope
import ru.shumilov.vladislav.contactstest.modules.core.remoteRepositories.BaseRemoteRepository
import javax.inject.Inject

@ContactScope
class ContactRemoteRepository @Inject constructor(
        private val contactApi: ContactApi
) : BaseRemoteRepository<ContactShort, Contact, ContactApi>() {

    override fun getApi(): ContactApi {
        return contactApi
    }

    override fun responseToModel(modelResponse: Contact): ContactShort {
        val contactShort = ContactShort()

        contactShort.id = modelResponse.id
        contactShort.name = modelResponse.name
        contactShort.name_lowercase = modelResponse.name_lowercase
        contactShort.created_at = modelResponse.created_at
        contactShort.updated_at = modelResponse.updated_at
        contactShort.is_deleted = modelResponse.is_deleted
        contactShort.deleted_at = modelResponse.deleted_at
        contactShort.phone = modelResponse.phone
        contactShort.height = modelResponse.height
        contactShort.temperament = modelResponse.temperament

        return contactShort
    }

    fun getList(number: String): Observable<List<Contact>> {
        return contactApi.getList(number)
    }

}