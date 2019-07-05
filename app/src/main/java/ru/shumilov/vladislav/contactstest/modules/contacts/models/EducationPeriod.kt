package ru.shumilov.vladislav.contactstest.modules.contacts.models

import io.realm.RealmObject
import ru.shumilov.vladislav.contactstest.core.models.BaseModel

open class EducationPeriod : RealmObject() {
    var start: String? = null
    var end: String? = null
}