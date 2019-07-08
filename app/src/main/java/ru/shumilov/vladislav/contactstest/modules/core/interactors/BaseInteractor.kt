package ru.shumilov.vladislav.contactstest.core.interactors

import ru.shumilov.vladislav.contactstest.core.localRepositories.BaseLocalRepository
import ru.shumilov.vladislav.contactstest.core.models.BaseModel

abstract class BaseInteractor<Model: BaseModel, ModelResponse> constructor(
        private val baseLocalRepository: BaseLocalRepository<Model>) {

    abstract fun responseToModel(modelResponse: ModelResponse): Model
}