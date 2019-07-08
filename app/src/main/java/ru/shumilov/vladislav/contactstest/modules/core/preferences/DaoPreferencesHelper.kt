package ru.simpls.brs2.commons.modules.core.preferenses

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import ru.shumilov.vladislav.contactstest.App
import ru.shumilov.vladislav.contactstest.modules.contacts.models.Contact
import ru.shumilov.vladislav.contactstest.modules.core.injection.ApplicationContext
import ru.shumilov.vladislav.contactstest.modules.core.injection.ApplicationScope
import javax.inject.Inject

@ApplicationScope
open class DaoPreferencesHelper {

    companion object {
        private const val APPLICATION = "Application"
        private const val IS_FIRST_TIME_APPLICATION_LOADED = "is_first_time_application_loaded"
        private const val PREF_FILE_NAME = "dao.preferences"
    }

    private lateinit var sharedPreferences: SharedPreferences

    @Inject
    constructor(@ApplicationContext context: Context) {
        sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE)
    }

    fun saveContactsLoadMoment() {
        saveLoadMoment(Contact().javaClass.simpleName)
    }

    fun getContactsLoadedMoment(): Long {
        return getSavedMoment(Contact().javaClass.simpleName)
    }

    fun isFirstTimeApplicationLoaded(): Boolean {
        return getBoolean(IS_FIRST_TIME_APPLICATION_LOADED)
    }

    fun setFirstTimeApplicationLoaded(value: Boolean = true) {
        saveBoolean(IS_FIRST_TIME_APPLICATION_LOADED, value)
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

    open fun saveBoolean(className: String, value: Boolean) {
        sharedPreferences.edit()
                .putBoolean("$className", value)
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

    open fun getBoolean(className: String): Boolean {
        return sharedPreferences.getBoolean("$className", false)
    }

    open fun isStoredDataExpired(className: String, periodMinutes: Int, login: String = "any"): Boolean {
        val loadMoment = getSavedMoment(className, login)

        return System.currentTimeMillis() - loadMoment > periodMinutes * 60 * 1000
    }
}

