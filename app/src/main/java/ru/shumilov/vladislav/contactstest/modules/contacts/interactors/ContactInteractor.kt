package ru.shumilov.vladislav.contactstest.modules.contacts.interactors

import android.text.TextUtils
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.functions.Function3
import io.reactivex.schedulers.Schedulers
import ru.shumilov.vladislav.contactstest.core.preferences.DateHelper
import ru.shumilov.vladislav.contactstest.modules.contacts.localRepositories.ContactLocalRepository
import ru.shumilov.vladislav.contactstest.modules.contacts.models.Contact
import ru.shumilov.vladislav.contactstest.modules.contacts.models.ContactShort
import ru.shumilov.vladislav.contactstest.modules.contacts.remoteRepositories.ContactRemoteRepository
import ru.shumilov.vladislav.contactstest.modules.core.injection.ContactScope
import ru.shumilov.vladislav.contactstest.modules.core.preferenses.DaoPreferencesHelper
import javax.inject.Inject


@ContactScope
class ContactInteractor @Inject constructor(
        private val contactRemoteRepository: ContactRemoteRepository,
        private val contactLocalRepository: ContactLocalRepository,
        private val daoPreferencesHelper: DaoPreferencesHelper) {

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
                contactRemoteRepository.getList(FIRST_SOURCE_NUMBER).onErrorReturn {
                    emptyList()
                },
                contactRemoteRepository.getList(SECOND_SOURCE_NUMBER).onErrorReturn {
                    emptyList()
                },
                contactRemoteRepository.getList(THIRD_SOURCE_NUMBER).onErrorReturn {
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

                    val contacts = saveContacts(reponseContacts, query)

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

    private fun onGetListFromServer(
            firstContacts: ArrayList<Contact>,
            secondContacts: List<Contact>,
            thirdContacts: List<Contact>): List<Contact> {

        firstContacts.addAll(secondContacts)
        firstContacts.addAll(thirdContacts)

        return firstContacts
    }

    private fun saveContacts(reponseContacts: List<Contact>, query: String? = null): List<Contact> {
        contactLocalRepository.clearDataBase()
        var contacts = contactLocalRepository.saveList(reponseContacts)!!

        if (!TextUtils.isEmpty(query)) {
            contacts = contactLocalRepository.getList(query)!!
        }

        return contacts
    }

    private fun modelsToShortList(contacts: List<Contact>): List<ContactShort> {
        val shortContacts = ArrayList<ContactShort>()

        for (contact: Contact in contacts) {
            shortContacts.add(contactRemoteRepository.responseToModel(contact))
        }

        return shortContacts
    }

    private fun mustGetListFromServer(): Boolean {
        val dataLoadMomentMillis = daoPreferencesHelper.getDataLoadMoment()
        val nowMillis = dateHelper.now()

        return (
                (nowMillis - dataLoadMomentMillis) > UPDATE_FREQUENCY_MILLIS &&
                        daoPreferencesHelper.wasDataLoadedBefore()
                ) ||
                !daoPreferencesHelper.wasDataLoadedBefore()
    }
}