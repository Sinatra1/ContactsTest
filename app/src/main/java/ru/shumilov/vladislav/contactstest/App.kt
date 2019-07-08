package ru.shumilov.vladislav.contactstest

import android.app.Activity
import android.app.Application
import android.support.v4.app.Fragment
import com.facebook.stetho.Stetho
import com.uphyca.stetho_realm.RealmInspectorModulesProvider
import io.realm.Realm
import ru.shumilov.vladislav.contactstest.modules.contacts.injection.ContactComponent
import ru.shumilov.vladislav.contactstest.modules.contacts.injection.ContactModule
import ru.shumilov.vladislav.contactstest.modules.core.injection.AppComponent
import ru.shumilov.vladislav.contactstest.modules.core.injection.modules.AppModule
import ru.shumilov.vladislav.contactstest.modules.core.injection.DaggerAppComponent
import ru.simpls.brs2.commons.modules.core.preferenses.DaoPreferencesHelper
import kotlin.properties.Delegates

fun Activity.app() = this.application as App
fun Fragment.app() = this.activity?.app()

class App : Application() {

    var appComponent: AppComponent by Delegates.notNull()
    var contactComponent: ContactComponent? = null
    private lateinit var daoPreferencesHelper: DaoPreferencesHelper

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent.builder().appModule(AppModule(this)).build()

        initRealm()

        saveApplicationLoadMoment()
    }

    fun createContactComponent(): ContactComponent {
        if (contactComponent == null) {
            contactComponent = appComponent.plusContactComponent(ContactModule())
        }

        return contactComponent!!
    }

    fun clearContactComponent() {
        contactComponent = null
    }

    private fun initRealm() {
        Realm.init(this)
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(RealmInspectorModulesProvider.builder(this).build()).build())
    }

    private fun saveApplicationLoadMoment() {
        daoPreferencesHelper = DaoPreferencesHelper(this)

        if (daoPreferencesHelper.getApplicationLoadedMoment() == 0L) {
            daoPreferencesHelper.setFirstTimeApplicationLoaded(true)
        } else {
            daoPreferencesHelper.setFirstTimeApplicationLoaded(false)
        }

        daoPreferencesHelper = DaoPreferencesHelper(this)
        daoPreferencesHelper.saveApplicationLoadMoment()
    }
}
