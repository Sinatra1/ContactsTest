package ru.shumilov.vladislav.contactstest.core.localRepositories

import android.text.TextUtils
import ru.shumilov.vladislav.contactstest.core.dao.BaseDao
import ru.shumilov.vladislav.contactstest.core.models.BaseModel
import java.util.*

abstract class BaseLocalRepository<Model: BaseModel> constructor(private val baseDao: BaseDao<Model>) {

    fun getList(query: String? = null, whereList : HashMap<String, String> = HashMap<String, String>()) : List<Model>? {
        if (query != null) {
            if (!TextUtils.isEmpty(query)) {
                whereList[getDefaultQueryField()] = query
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

    protected fun beforeSave(model: Model?) : Model? {
        return model
    }

    protected fun afterSave(model: Model?) : Model? {
        return model
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