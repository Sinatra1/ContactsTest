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
import ru.shumilov.vladislav.contactstest.modules.core.injection.DaggerAppComponent
import ru.shumilov.vladislav.contactstest.modules.core.injection.modules.AppModule
import timber.log.Timber
import kotlin.properties.Delegates

fun Activity.app() = this.application as App
fun Fragment.app() = this.activity?.app()

class App : Application() {

    var appComponent: AppComponent by Delegates.notNull()
    var contactComponent: ContactComponent? = null

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent.builder().appModule(AppModule(this)).build()

        initRealm()
        initLogs()
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

    private fun initLogs() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
