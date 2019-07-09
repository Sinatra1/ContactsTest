package ru.shumilov.vladislav.contactstest.modules.contacts.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import ru.shumilov.vladislav.contactstest.core.models.BaseModel
import ru.shumilov.vladislav.contactstest.core.preferences.DateHelper

open class EducationPeriod : RealmObject() {
    @PrimaryKey
    @Required
    var id: String? = null
    var start: String? = null
    var end: String? = null

    override fun toString(): String {
        val dateHelper = DateHelper()
        return dateHelper.utcToHumanDate(start) + " - " + dateHelper.utcToHumanDate(end)
    }
}