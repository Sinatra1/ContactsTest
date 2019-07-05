package ru.shumilov.vladislav.contactstest

import android.app.Application
import com.facebook.stetho.Stetho
import com.uphyca.stetho_realm.RealmInspectorModulesProvider
import ru.vshumilov.agroholdingapp.modules.core.injection.AppComponent
import ru.vshumilov.agroholdingapp.modules.core.injection.modules.AppModule
import kotlin.properties.Delegates

class App : Application() {

    var appComponent : AppComponent by Delegates.notNull()

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent.builder().appModule(AppModule(this)).build()

        initRealm()
    }

    private fun initRealm() {
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(RealmInspectorModulesProvider.builder(this).build()).build())
    }
}
