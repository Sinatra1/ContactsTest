package ru.shumilov.vladislav.contactstest.modules.core.preferenses

import android.content.Context
import android.content.SharedPreferences
import ru.shumilov.vladislav.contactstest.modules.core.injection.ApplicationContext
import ru.shumilov.vladislav.contactstest.modules.core.injection.ApplicationScope
import javax.inject.Inject

@ApplicationScope
open class DaoPreferencesHelper {

    companion object {
        const val DATA_LOADED_MOMENT = "data_loaded_moment"
        private const val PREF_FILE_NAME = "dao.preferences"
    }

    private val sharedPreferences: SharedPreferences

    @Inject
    constructor(@ApplicationContext context: Context) {
        sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE)
    }

    fun wasDataLoadedBefore(): Boolean {
        return getDataLoadMoment() > 0L
    }

    fun saveDataLoadMoment(loadMoment: Long = System.currentTimeMillis()) {
        saveLoadMoment(DATA_LOADED_MOMENT, loadMoment)
    }

    fun getDataLoadMoment(): Long {
        return getSavedMoment(DATA_LOADED_MOMENT)
    }

    fun saveLoadMoment(className: String, loadMoment: Long = System.currentTimeMillis(), login: String = "save") {
        sharedPreferences.edit()
                .putLong("$className/$login", loadMoment)
                .apply()
    }

    fun saveClearMoment(className: String, action: String = "clear") {
        saveLoadMoment(className, 0L, action)
    }

    fun saveDeleteMoment(className: String, action: String = "delete") {
        saveLoadMoment(className, 0L, action)
    }

    fun getSavedMoment(className: String, action: String = "save"): Long {
        return sharedPreferences.getLong("$className/$action", 0L)
    }

    fun saveString(className: String, value: String) {
        sharedPreferences.edit()
                .putString("$className", value)
                .apply()
    }

    fun saveBoolean(className: String, value: Boolean) {
        sharedPreferences.edit()
                .putBoolean("$className", value)
                .apply()
    }

    fun clearString(className: String) {
        sharedPreferences.edit()
                .remove(className)
                .apply()

    }

    fun getString(className: String): String {
        return sharedPreferences.getString("$className", "")
    }

    fun getBoolean(className: String): Boolean {
        return sharedPreferences.getBoolean("$className", false)
    }
}

