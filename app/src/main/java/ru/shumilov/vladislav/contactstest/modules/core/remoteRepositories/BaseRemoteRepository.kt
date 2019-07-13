package ru.shumilov.vladislav.contactstest.modules.core.remoteRepositories

import ru.shumilov.vladislav.contactstest.core.models.BaseModel

abstract class BaseRemoteRepository<Model : BaseModel, ModelResponse, Api> {

    protected abstract fun getApi(): Api

    abstract fun responseToModel(modelResponse: ModelResponse): Model
}