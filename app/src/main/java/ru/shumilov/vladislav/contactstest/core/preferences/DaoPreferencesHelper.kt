package ru.simpls.brs2.commons.modules.core.preferenses

import android.content.Context
import android.content.SharedPreferences
import ru.vshumilov.agroholdingapp.modules.core.injection.ApplicationContext
import ru.vshumilov.agroholdingapp.modules.core.injection.ApplicationScope
import javax.inject.Inject

@ApplicationScope
open class DaoPreferencesHelper {

    private val PREF_FILE_NAME = "dao.preferences"

    private lateinit var sharedPreferences: SharedPreferences

    @Inject
    constructor(@ApplicationContext context: Context) {
        sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE)
    }

    open fun saveLoadMoment(className: String, loadMoment: Long = System.currentTimeMillis(), login: String = "any") {
        sharedPreferences.edit()
                .putLong("$className/$login", loadMoment)
                .apply()
    }

    open fun clearLoadMoment(className: String, login: String = "any") {
        saveLoadMoment(className, 0L, login)
    }

    open fun getSavedMoment(className: String, login: String = "any"): Long {
        return sharedPreferences.getLong("$className/$login", 0L)
    }

    open fun saveString(className: String, value: String) {
        sharedPreferences.edit()
                .putString("$className", value)
                .apply()
    }

    open fun clearString(className: String) {
        sharedPreferences.edit()
                .remove(className)
                .apply()

    }

    open fun getString(className: String): String {
        return sharedPreferences.getString("$className", "")
    }


    open fun isStoredDataExpired(className: String, periodMinutes: Int, login: String = "any"): Boolean {
        val loadMoment = getSavedMoment(className, login)

        return System.currentTimeMillis() - loadMoment > periodMinutes * 60 * 1000
    }
}

