package ru.shumilov.vladislav.contactstest.modules.core.ui

interface RecyclerViewListener<Model> {
    fun onItemClick(item: Model, position: Int)

    fun onItemAdd(item: Model, position: Int)
}