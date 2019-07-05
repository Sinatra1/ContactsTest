package ru.shumilov.vladislav.contactstest.modules.contacts.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import ru.shumilov.vladislav.contactstest.core.models.BaseModel

open class Contact : RealmObject(), BaseModel {

    @PrimaryKey
    @Required
    override var id: String? = null
    override var name: String? = null
    override var name_lowercase: String? = null
    override var created_at: String? = null
    override var updated_at: String? = null
    override var is_deleted: Boolean = false
    override var deleted_at: String? = null
    var phone: String? = null
    var height: Float? = null
    var temperament: String? = null
    var biography: String? = null
    var educationPeriod: EducationPeriod? = null
}