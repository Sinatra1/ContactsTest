package ru.shumilov.vladislav.contactstest.modules.core.injection.modules

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.shumilov.vladislav.contactstest.App
import ru.simpls.brs2.commons.modules.core.preferenses.DaoPreferencesHelper
import ru.shumilov.vladislav.contactstest.modules.core.injection.ApplicationContext
import ru.shumilov.vladislav.contactstest.modules.core.injection.ApplicationScope

@Module
class AppModule(val app : App) {

    val appContext : Context

    init {
        appContext = app
    }

    @Provides
    @ApplicationContext
    fun provideContext() = appContext

    @Provides
    fun provideApp() : App = app

    @Provides
    @ApplicationScope
    fun getDaoPreferencesHelper(): DaoPreferencesHelper = DaoPreferencesHelper(appContext)

}