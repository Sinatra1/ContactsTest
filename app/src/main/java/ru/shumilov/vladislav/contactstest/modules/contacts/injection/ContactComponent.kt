package ru.shumilov.vladislav.contactstest.modules.contacts.injection

import dagger.Subcomponent
import ru.shumilov.vladislav.contactstest.modules.contacts.api.ContactApi
import ru.shumilov.vladislav.contactstest.modules.contacts.ui.detail.ContactDetailFragment
import ru.shumilov.vladislav.contactstest.modules.contacts.ui.list.ContactsListFragment
import ru.shumilov.vladislav.contactstest.modules.core.injection.ApplicationScope

@Subcomponent(modules = [ContactModule::class])
@ApplicationScope
interface ContactComponent {

    fun contactApi() : ContactApi

    fun inject(fragment: ContactsListFragment)

    fun inject(fragment: ContactDetailFragment)
}