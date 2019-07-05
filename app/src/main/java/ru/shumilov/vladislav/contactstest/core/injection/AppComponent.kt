package ru.shumilov.vladislav.contactstest.modules.core.injection

import android.content.Context
import com.google.gson.Gson
import dagger.Component
import io.realm.Realm
import ru.shumilov.vladislav.contactstest.App
import ru.shumilov.vladislav.contactstest.modules.contacts.injection.ContactComponent
import ru.shumilov.vladislav.contactstest.modules.contacts.injection.ContactModule
import ru.shumilov.vladislav.contactstest.modules.core.injection.modules.AppModule
import ru.shumilov.vladislav.contactstest.modules.core.injection.modules.GsonModule
import ru.shumilov.vladislav.contactstest.modules.core.injection.modules.HttpModule
import ru.shumilov.vladislav.contactstest.modules.core.injection.modules.RealmModule


@Component(modules = [AppModule::class, GsonModule::class, HttpModule::class, RealmModule::class])
@ApplicationScope
interface AppComponent {

    @ApplicationContext
    fun context(): Context

    fun app(): App
    fun gson(): Gson
    fun realm(): Realm

    fun plusContactComponent(contactModule: ContactModule): ContactComponent
}