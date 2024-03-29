package ru.shumilov.vladislav.contactstest.modules.contacts.injection

import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import ru.shumilov.vladislav.contactstest.modules.contacts.api.ContactApi
import ru.shumilov.vladislav.contactstest.modules.core.injection.ContactScope
import ru.shumilov.vladislav.contactstest.modules.login.injection.BaseModule
import javax.inject.Named

@Module
class ContactModule : BaseModule<ContactApi>() {

    @Provides
    @ContactScope
    fun provideContactApi(@Named("contactApi") retrofit: Retrofit): ContactApi {
        return super.provideApi(retrofit, ContactApi::class.java)
    }

    @Provides
    @ContactScope
    @Named("contactApi")
    fun provideContactApiRetrofit(gson: Gson,
                                  okHttpClient: OkHttpClient): Retrofit {

        return super.provideApiRetrofit(gson, okHttpClient)
    }
}