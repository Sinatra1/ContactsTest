package ru.shumilov.vladislav.contactstest.modules.contacts.dao

import io.realm.Realm
import ru.shumilov.vladislav.contactstest.core.dao.BaseDao
import ru.shumilov.vladislav.contactstest.modules.contacts.models.Contact
import ru.simpls.brs2.commons.modules.core.preferenses.DaoPreferencesHelper
import ru.shumilov.vladislav.contactstest.modules.core.injection.ApplicationScope
import javax.inject.Inject
import javax.inject.Provider

@ApplicationScope
class ContactDao @Inject constructor(
        private val daoPreferencesHelper: DaoPreferencesHelper,
        private val realmProvider: Provider<Realm>) : BaseDao<Contact>(daoPreferencesHelper, realmProvider) {

    override fun getEmptyModel(): Contact? {
        return Contact()
    }
}