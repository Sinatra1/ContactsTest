package ru.shumilov.vladislav.contactstest.core.localRepositories

import android.text.TextUtils
import ru.shumilov.vladislav.contactstest.core.dao.BaseDao
import ru.shumilov.vladislav.contactstest.core.models.BaseModel
import java.util.*

abstract class BaseLocalRepository<Model: BaseModel> constructor(private val baseDao: BaseDao<Model>) {

    fun getList(query: String? = null, whereList : HashMap<String, String> = HashMap()) : List<Model>? {
        if (query != null) {
            if (!TextUtils.isEmpty(query)) {
                whereList[getDefaultQueryField()] = query.toLowerCase()
            }
        }

        return baseDao.getList(whereList)
    }

    protected fun getDefaultQueryField(): String {
        return "name_lowercase"
    }

    open fun save(model: Model): Model? {
        var newModel = beforeSave(model)

        newModel = baseDao.save(newModel)

        newModel = afterSave(newModel)

        return newModel
    }

    open fun saveList(models: List<Model>): List<Model>? {
        beforeSaveList(models)

        val newModels = baseDao.saveList(models)

        afterSaveList(newModels)

        return newModels
    }

    open protected fun beforeSaveList(models: List<Model>?) : List<Model>? {
        if (models == null) {
            return models
        }

        for (model: Model in models) {
            beforeSave(model)
        }

        return models
    }

    open protected fun beforeSave(model: Model?) : Model? {
        return model
    }

    open protected fun afterSave(model: Model?) : Model? {
        return model
    }

    open protected fun afterSaveList(models: List<Model>?) : List<Model>? {
        if (models == null || models.isEmpty()) {
            return models
        }

        for (model: Model in models) {
            afterSave(model)
        }

        return models
    }

    fun getById(modelId: String?) : Model? {
        if (modelId == null) {
            return null
        }

        return baseDao.getById(modelId)
    }

    fun delete(modelId: String?): Boolean {
        if (modelId == null) {
            return false
        }

        val model = baseDao.getById(modelId)

        if (model == null || (model as BaseModel).id == null) {
            return false
        }

        return baseDao.delete(model)
    }

    fun deleteAll() = baseDao.deleteAll()

    fun clearDataBase() = baseDao.clearDataBase()
}