package ru.vshumilov.agroholdingapp.modules.core.injection

import android.content.Context
import com.google.gson.Gson
import dagger.Component
import ru.shumilov.vladislav.contactstest.App
import ru.vshumilov.agroholdingapp.modules.core.injection.modules.AppModule
import ru.vshumilov.agroholdingapp.modules.core.injection.modules.GsonModule
import ru.vshumilov.agroholdingapp.modules.core.injection.modules.HttpModule
import ru.vshumilov.agroholdingapp.modules.core.injection.modules.RealmModule


@Component(modules = arrayOf(AppModule::class, GsonModule::class, HttpModule::class, RealmModule::class))
@ApplicationScope
interface AppComponent {

    @ApplicationContext
    fun context(): Context

    fun app(): App
    fun gson(): Gson
    //fun realm(): Realm
}