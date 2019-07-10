package ru.shumilov.vladislav.contactstest.modules.contacts.interactors

import android.text.TextUtils
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.functions.Function3
import io.reactivex.schedulers.Schedulers
import ru.shumilov.vladislav.contactstest.core.interactors.BaseInteractor
import ru.shumilov.vladislav.contactstest.core.preferences.DateHelper
import ru.shumilov.vladislav.contactstest.modules.contacts.api.ContactApi
import ru.shumilov.vladislav.contactstest.modules.contacts.localRepositories.ContactLocalRepository
import ru.shumilov.vladislav.contactstest.modules.contacts.models.Contact
import ru.shumilov.vladislav.contactstest.modules.contacts.models.ContactShort
import ru.shumilov.vladislav.contactstest.modules.core.injection.ContactScope
import ru.simpls.brs2.commons.modules.core.preferenses.DaoPreferencesHelper
import javax.inject.Inject


@ContactScope
class ContactInteractor @Inject constructor(
        private val contactApi: ContactApi,
        private val contactLocalRepository: ContactLocalRepository,
        private val daoPreferencesHelper: DaoPreferencesHelper) : BaseInteractor<Contact, Contact>(contactLocalRepository) {

    companion object {
        const val FIRST_SOURCE_NUMBER = "01"
        const val SECOND_SOURCE_NUMBER = "02"
        const val THIRD_SOURCE_NUMBER = "03"
        const val UPDATE_FREQUENCY_MILLIS = 60 * 1000 //1 min
    }

    private val dateHelper = DateHelper()

    fun getById(id: String): Observable<Contact> {
        val request = Observable.create(ObservableOnSubscribe<Contact> { emitter ->
            emitter.onNext(
                    contactLocalRepository.getById(id)!!
            )
        }).subscribeOn(Schedulers.single()).observeOn(Schedulers.single())

        return request
    }

    fun getList(query: String? = null): Observable<List<ContactShort>> {
        if (mustGetListFromServer()) {
            return getListFromServer(query)
        }

        return getListFromLocal(query)
    }

    fun getListFromServer(query: String? = null): Observable<List<ContactShort>> {
        return Observable.zip(
                contactApi.getList(FIRST_SOURCE_NUMBER).onErrorReturn {
                    emptyList()
                },
                contactApi.getList(SECOND_SOURCE_NUMBER).onErrorReturn {
                    emptyList()
                },
                contactApi.getList(THIRD_SOURCE_NUMBER).onErrorReturn {
                    emptyList()
                },
                Function3<List<Contact>, List<Contact>, List<Contact>, List<Contact>>
                zip@{ firstContacts,
                      secondContacts,
                      thirdContacts: List<Contact> ->

                    return@zip onGetListFromServer(firstContacts as ArrayList<Contact>, secondContacts, thirdContacts)
                }).subscribeOn(Schedulers.io())
                .observeOn(Schedulers.single())
                .map { reponseContacts ->

                    var contacts = emptyList<Contact>()

                    if (reponseContacts != null) {
                        contacts = reponseContacts
                    }

                    contacts = contactLocalRepository.saveList(contacts)!!

                    if (!TextUtils.isEmpty(query)) {
                        contacts = contactLocalRepository.getList(query)!!
                    }

                    return@map modelsToShortList(contacts)
                }
    }

    fun getListFromLocal(query: String? = null): Observable<List<ContactShort>> {
        val request = Observable.create(ObservableOnSubscribe<List<Contact>> { emitter ->
            emitter.onNext(
                    contactLocalRepository.getList(query)!!
            )
        })

        return request
                .subscribeOn(Schedulers.single())
                .observeOn(Schedulers.single())
                .map { contacts ->
                    return@map modelsToShortList(contacts)
                }
    }

    protected fun onGetListFromServer(
            firstContacts: ArrayList<Contact>,
            secondContacts: List<Contact>,
            thirdContacts: List<Contact>): List<Contact> {

        firstContacts.addAll(secondContacts)
        firstContacts.addAll(thirdContacts)

        return firstContacts
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

    protected fun mustGetListFromServer(): Boolean {
        val applicationLoadMomentMillis = daoPreferencesHelper.getContactsLoadedMoment()
        var nowMillis = dateHelper.now()

        val result = (
                (nowMillis - applicationLoadMomentMillis) > UPDATE_FREQUENCY_MILLIS &&
                        !daoPreferencesHelper.isFirstTimeApplicationLoaded()
                ) ||
                daoPreferencesHelper.isFirstTimeApplicationLoaded()

        return result
    }

    override fun responseToModel(modelResponse: Contact): Contact {
        return modelResponse
    }
}