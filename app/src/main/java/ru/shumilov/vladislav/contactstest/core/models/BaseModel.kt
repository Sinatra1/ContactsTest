package ru.shumilov.vladislav.contactstest.core.models

interface BaseModel {

    var id: String?

    var name: String?
    var name_lowercase: String?

    var is_deleted: Boolean
    var deleted_at: String?

    var created_at: String?
    var updated_at: String?
}