package ru.shumilov.vladislav.contactstest.modules.contacts.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import ru.shumilov.vladislav.contactstest.core.models.BaseModel

open class EducationPeriod : RealmObject() {
    @PrimaryKey
    @Required
    var id: String? = null
    var start: String? = null
    var end: String? = null
}