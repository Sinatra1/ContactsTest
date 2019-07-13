package ru.shumilov.vladislav.contactstest.modules.contacts.dao

import io.realm.Realm
import ru.shumilov.vladislav.contactstest.core.dao.BaseDao
import ru.shumilov.vladislav.contactstest.modules.contacts.models.Contact
import ru.shumilov.vladislav.contactstest.modules.core.injection.ContactScope
import ru.shumilov.vladislav.contactstest.modules.core.preferenses.DaoPreferencesHelper
import javax.inject.Inject
import javax.inject.Provider

@ContactScope
class ContactDao @Inject constructor(
        private val daoPreferencesHelper: DaoPreferencesHelper,
        private val realmProvider: Provider<Realm>) : BaseDao<Contact>(daoPreferencesHelper, realmProvider) {

    companion object {
        const val CONTACT_LOADED_MOMENT = "contact_loaded_moment"
    }

    override fun getEmptyModel(): Contact {
        return Contact()
    }

    override fun getModelKey(): String {
        return CONTACT_LOADED_MOMENT
    }
}