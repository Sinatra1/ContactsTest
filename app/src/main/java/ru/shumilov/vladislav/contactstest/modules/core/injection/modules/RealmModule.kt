package ru.shumilov.vladislav.contactstest.modules.core.injection.modules

import dagger.Module
import dagger.Provides
import io.realm.Realm
import io.realm.RealmConfiguration
import ru.shumilov.vladislav.contactstest.modules.core.injection.ApplicationScope

@Module
class RealmModule {

    companion object {
        const val REALM_SCHEMA_VERSION: Long = 1
        const val REALM_DB_NAME = "contacts"
    }

    @Provides
    @ApplicationScope
    fun provideRealm(realmConfiguration: RealmConfiguration): Realm = Realm.getInstance(realmConfiguration)


    @Provides
    @ApplicationScope
    fun provideRealmConfig(): RealmConfiguration {

        if (REALM_DB_NAME == null) {
            throw IllegalStateException("allconfig.properties должен содержать параметр realm.name с значением имени файла realm")
        }

        val realmConfig = RealmConfiguration.Builder()
                .compactOnLaunch { totalBytes, usedBytes ->
                    // Compact if the file is over 500KB in size and less than 20% 'used'
                    val minSize = (500 * 1024).toLong()
                    return@compactOnLaunch totalBytes > minSize && usedBytes / totalBytes.toDouble() < 0.2
                }
                .name(REALM_DB_NAME)
                .schemaVersion(REALM_SCHEMA_VERSION)

        realmConfig.deleteRealmIfMigrationNeeded()

        return realmConfig.build()
    }
}
