package ru.shumilov.vladislav.contactstest.modules.contacts.interactors

import android.text.TextUtils
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
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
        private const val START_SOURCE_NUMBER = 1
        private const val COUNT_SOURCES = 3
        private const val UPDATE_FREQUENCY_MILLIS = 60 * 1000 //1 min
    }

    private val dateHelper = DateHelper()
    private var contactsShort: ArrayList<ContactShort> = ArrayList()
    var isGettingListFromServer = true

    fun getById(id: String): Observable<Contact> {
        val request = Observable.create(ObservableOnSubscribe<Contact> { emitter ->
            emitter.onNext(
                    contactLocalRepository.getById(id)!!
            )
        }).subscribeOn(Schedulers.single()).observeOn(Schedulers.single())

        return request
    }

    fun getList(query: String? = null): Observable<List<ContactShort>> {
        isGettingListFromServer = mustGetListFromServer()

        if (isGettingListFromServer) {
            return getListFromServer()
        }

        return getListFromLocal(query)
    }

    fun getListFromServer(): Observable<List<ContactShort>> {
        contactsShort = ArrayList()

        return Observable.range(START_SOURCE_NUMBER, COUNT_SOURCES)
                .flatMap { number ->
                    return@flatMap contactRemoteRepository.getList(formatResourceNumber(number))
                }.subscribeOn(Schedulers.io())
                .observeOn(Schedulers.single())
                .flatMap { contacts ->
                    val savedContacts = contactLocalRepository.saveList(contacts)

                    return@flatMap Observable.fromArray(savedContacts)
                }
                .observeOn(Schedulers.computation())
                .flatMap { contacts ->
                    val contactsShort = modelsToShortList(contacts)
                    this.contactsShort.addAll(contactsShort)
                    return@flatMap Observable.fromArray(contactsShort)
                }
                .observeOn(Schedulers.single())
    }

    fun getListFromLocal(query: String? = null): Observable<List<ContactShort>> {
        val request = Observable.create(ObservableOnSubscribe<List<Contact>> { emitter ->
            emitter.onNext(
                    contactLocalRepository.getList(query)!!
            )
        })

        return request
                .subscribeOn(Schedulers.single())
                .observeOn(Schedulers.computation())
                .map { contacts ->
                    contactsShort = modelsToShortList(contacts) as ArrayList<ContactShort>
                    return@map contactsShort
                }
    }

    fun getSortedContactsShort(query: String? = null): List<ContactShort> {
        if (!TextUtils.isEmpty(query) && isGettingListFromServer) {
            contactsShort = modelsToShortList(contactLocalRepository.getList(query)!!) as ArrayList<ContactShort>
        } else if (isGettingListFromServer) {
            contactsShort.sortBy { it.name }
        }

        return contactsShort
    }

    private fun formatResourceNumber(number: Int): String {
        return number.toString().padStart(2, '0')
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