package ru.shumilov.vladislav.contactstest.modules.contacts.injection

import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import ru.shumilov.vladislav.contactstest.modules.contacts.api.ContactApi
import ru.shumilov.vladislav.contactstest.modules.core.injection.ApplicationScope
import ru.shumilov.vladislav.contactstest.modules.login.injection.BaseModule
import javax.inject.Named

@Module
class ContactModule : BaseModule<ContactApi>() {

    @Provides
    @ApplicationScope
    @Named("contactApi")
    fun provideContactApi(retrofit: Retrofit): ContactApi {
        return super.provideApi(retrofit, ContactApi::class.java)
    }
}