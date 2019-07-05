package ru.shumilov.vladislav.contactstest.modules.contacts.interactors

import io.realm.RealmList
import ru.shumilov.vladislav.contactstest.core.interactors.BaseInteractor
import ru.shumilov.vladislav.contactstest.modules.contacts.api.ContactApi
import ru.shumilov.vladislav.contactstest.modules.contacts.localRepositories.ContactLocalRepository
import ru.shumilov.vladislav.contactstest.modules.contacts.models.Contact
import ru.shumilov.vladislav.contactstest.modules.core.injection.ApplicationScope
import rx.Observable
import rx.subscriptions.Subscriptions
import timber.log.Timber
import javax.inject.Inject
import rx.subscriptions.CompositeSubscription


@ApplicationScope
class ContactInteractor @Inject constructor(
        private val contactApi: ContactApi,
        private val contactLocalRepository: ContactLocalRepository) : BaseInteractor<Contact, Contact>(contactLocalRepository) {

    private val compositeSubscription = CompositeSubscription()

    fun getListFromServer(number: String): Observable<List<Contact>>? {
        val request = contactApi.getList(number)?.cache()

        compositeSubscription.add(request?.subscribe(
                { serverBases ->
                    serverBases.forEach {
                        contactLocalRepository.save(responseToModel(it))
                    }
                },
                { error ->
                    Timber.e(this.javaClass.simpleName, error.message)
                }
        ))

        return request
    }

    override fun responseToModel(contact: Contact): Contact {
        return contact
    }

    override fun onDestroy() {
        compositeSubscription.unsubscribe()
    }
}