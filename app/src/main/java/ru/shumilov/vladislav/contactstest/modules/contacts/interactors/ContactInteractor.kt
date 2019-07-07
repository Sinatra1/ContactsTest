package ru.shumilov.vladislav.contactstest.modules.contacts.interactors

import ru.shumilov.vladislav.contactstest.core.interactors.BaseInteractor
import ru.shumilov.vladislav.contactstest.modules.contacts.api.ContactApi
import ru.shumilov.vladislav.contactstest.modules.contacts.localRepositories.ContactLocalRepository
import ru.shumilov.vladislav.contactstest.modules.contacts.models.Contact
import ru.shumilov.vladislav.contactstest.modules.contacts.models.ContactShort
import ru.shumilov.vladislav.contactstest.modules.core.injection.ApplicationScope
import ru.shumilov.vladislav.contactstest.modules.core.preferences.PhoneHelper
import rx.Observable
import javax.inject.Inject


@ApplicationScope
class ContactInteractor @Inject constructor(
        private val contactApi: ContactApi,
        private val contactLocalRepository: ContactLocalRepository) : BaseInteractor<Contact, Contact>(contactLocalRepository) {

    companion object {
        const val FIRST_SOURCE_NUMBER = "01"
        const val SECOND_SOURCE_NUMBER = "02"
        const val THIRD_SOURCE_NUMBER = "03"
    }

    private val phoneHelper = PhoneHelper()

    fun getListFromServer(): Observable<List<ContactShort>> {
        return Observable.zip(
                contactApi.getList(FIRST_SOURCE_NUMBER),
                contactApi.getList(SECOND_SOURCE_NUMBER),
                contactApi.getList(THIRD_SOURCE_NUMBER)
        ) { firstContacts,
            secondContacts,
            thirdContacts ->

            return@zip onGetListFromServer(firstContacts as ArrayList<Contact>, secondContacts, thirdContacts)
        }
    }

    protected fun onGetListFromServer(
            firstContacts: ArrayList<Contact>,
            secondContacts: List<Contact>,
            thirdContacts: List<Contact>): List<ContactShort> {

        firstContacts.addAll(secondContacts)
        firstContacts.addAll(thirdContacts)

        for (contact: Contact in firstContacts) {
            responseToModel(contact)
        }

        contactLocalRepository.saveList(firstContacts)

        return modelsToShortList(firstContacts)
    }

    override fun responseToModel(contact: Contact): Contact {
        contact.phone = phoneHelper.formattedPhoneToOnlyNumbers(contact.phone)

        if (contact.educationPeriod?.id == null) {
            contact.educationPeriod?.id = contact.id
        }

        return contact
    }

    protected fun modelsToShortList(contacts: List<Contact>): List<ContactShort> {
        val shortContacts = ArrayList<ContactShort>()

        for (contact: Contact in contacts) {
            shortContacts.add(modelToShort(contact))
        }

        return shortContacts
    }

    protected fun modelToShort(contact: Contact): ContactShort {
        val contactShort = ContactShort()

        contactShort.id = contact.id
        contactShort.name = contact.name
        contactShort.name_lowercase = contact.name_lowercase
        contactShort.created_at = contact.created_at
        contactShort.updated_at = contact.updated_at
        contactShort.is_deleted = contact.is_deleted
        contactShort.deleted_at = contact.deleted_at
        contactShort.phone = contact.phone
        contactShort.height = contact.height
        contactShort.temperament = contact.temperament

        return contactShort
    }
}