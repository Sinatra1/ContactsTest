package ru.shumilov.vladislav.contactstest.modules.contacts.models

import ru.shumilov.vladislav.contactstest.core.models.BaseModel

open class ContactShort : BaseModel {

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
}