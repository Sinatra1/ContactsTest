package ru.shumilov.vladislav.contactstest.modules.core.remoteRepositories

abstract class BaseRemoteRepository<Model, ModelResponse, Api> {

    protected abstract fun getApi(): Api

    abstract fun responseToModel(modelResponse: ModelResponse): Model
}