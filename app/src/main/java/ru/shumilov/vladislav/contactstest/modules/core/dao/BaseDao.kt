package ru.shumilov.vladislav.contactstest.core.dao

import android.os.Handler
import android.os.Looper
import io.realm.Realm
import io.realm.RealmModel
import io.realm.RealmObject
import io.realm.Sort
import ru.shumilov.vladislav.contactstest.core.models.BaseModel
import ru.shumilov.vladislav.contactstest.core.preferences.DateHelper
import ru.simpls.brs2.commons.modules.core.preferenses.DaoPreferencesHelper
import ru.shumilov.vladislav.contactstest.modules.core.injection.ApplicationScope
import ru.simpls.brs2.commons.functions.safe
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import javax.inject.Provider

@ApplicationScope
open class BaseDao<Model : BaseModel> @Inject constructor(
        private val daoPreferencesHelper: DaoPreferencesHelper,
        private val realmProvider: Provider<Realm>) {

    companion object {
        const val NOT_NULL = "not_null"
        const val IS_NULL = "is_null"
        const val TRUE = "true"
        const val FALSE = "false"
        const val DESC = "desc"
    }

    protected val dateHelper: DateHelper = DateHelper()

    open fun save(model: Model?): Model? {
        val preparedModel = beforeSave(model)

        try {
            if (preparedModel is RealmObject) {
                with(realmProvider.get()) {
                    use { realm ->
                        realm.executeTransaction {
                            realm?.insertOrUpdate(preparedModel)

                            daoPreferencesHelper.saveLoadMoment(preparedModel.javaClass.simpleName)
                        }
                    }
                }
            }
        } catch (e: RuntimeException) {
            Timber.e(this.javaClass.simpleName, e.message)
        } finally {
            return preparedModel
        }
    }

    open fun saveList(models: List<Model>?): List<Model>? {
        if (models == null || models.isEmpty()) {
            return models
        }

        beforeSaveList(models)
        try {
            val realm = realmProvider.get()
            if (models.first() is RealmObject) {
                realm.executeTransaction {
                    realm?.insertOrUpdate(models as MutableCollection<RealmModel>)

                    daoPreferencesHelper.saveLoadMoment(models.first().javaClass.simpleName)
                }
            }
        } catch (e: RuntimeException) {
            Timber.e(this.javaClass.simpleName, e.message)
        } finally {
            return models
        }
    }

    protected fun beforeSaveList(models: List<Model>?): List<Model>? {
        if (models == null) {
            return models
        }

        for (model: Model in models) {
            beforeSave(model)
        }

        return models
    }

    protected fun beforeSave(model: Model?): Model? {
        if (model == null) {
            return model
        }

        if (model.id == null) {
            model.id = generateId()
        }

        model.created_at = dateHelper.dateToDbStr()
        model.updated_at = dateHelper.dateToDbStr()
        model.name_lowercase = model.name?.toLowerCase()

        return model
    }

    fun getById(modelId: String?): Model? {
        var model: Model? = null

        val realm = realmProvider.get()

        safe {
            model = realm.copyFromRealm(
                    realm.where((getEmptyModel() as RealmObject).javaClass)
                            .equalTo("is_deleted", false)
                            .equalTo("id", modelId).findFirst()) as Model?
        }

        return model
    }

    fun getList(
            whereList: HashMap<String, String>? = null,
            sortByList: HashMap<String, String>? = null): List<Model>? {
        val realm = realmProvider.get()
        var query = realm.where((getEmptyModel() as RealmObject).javaClass).equalTo("is_deleted", false)
        var sortKey = getSortKey()
        var sortValue = Sort.ASCENDING

        if (whereList != null && whereList?.size > 0) {
            for (entry in whereList) {
                var value = entry.value

                if (value == TRUE || value == FALSE) {
                    query.equalTo(entry.key, value.toBoolean())
                } else if (value == NOT_NULL) {
                    query.isNotNull(entry.key)
                } else if (value == IS_NULL) {
                    query.isNull(entry.key)
                } else {
                    query.contains(entry.key, value)
                }
            }
        }

        if (sortByList != null && sortByList?.size > 0) {
            for (entry in sortByList) {
                var value = entry.value

                sortKey = entry.key

                if (value == DESC) {
                    sortValue = Sort.DESCENDING
                } else {
                    sortValue = Sort.ASCENDING
                }
            }
        }

        return realm.copyFromRealm(query.findAll().sort(sortKey, sortValue)) as List<Model>?
    }

    fun getCount(): Long {
        val realm = realmProvider.get()

        return realm.where((getEmptyModel() as RealmObject).javaClass).count()
    }

    fun deleteAll(): Boolean {
        val models = getList()

        if (models == null || models.isEmpty()) {
            return false
        }

        models.forEach {
            delete(it)
        }

        return true
    }

    fun delete(model: Model): Boolean {
        val realm = realmProvider.get()

        realm.executeTransaction {
            if (model is RealmObject) {
                model.is_deleted = true
                model.deleted_at = dateHelper.dateToDbStr()

                realm.insertOrUpdate(model)

                daoPreferencesHelper.saveLoadMoment((getEmptyModel() as RealmObject).javaClass.simpleName)
            }
        }

        return true
    }

    fun clear() {
        val realm = realmProvider.get()

        realm.executeTransaction {
            realm.delete((getEmptyModel() as RealmObject).javaClass)
            daoPreferencesHelper.clearLoadMoment((getEmptyModel() as RealmObject).javaClass.simpleName)
        }
    }

    fun clearDataBase() {
        val realm = realmProvider.get()

        realm.executeTransaction {
            realm.deleteAll()
            daoPreferencesHelper.clearLoadMoment((getEmptyModel() as RealmObject).javaClass.simpleName)
        }
    }

    fun clearById(modelId: String?) {
        if (modelId == null) {
            return
        }

        val realm = realmProvider.get()
        realm.executeTransaction {
            val query = realm.where((getEmptyModel() as RealmObject).javaClass).equalTo("id", modelId).findFirst()
            query?.deleteFromRealm()
            daoPreferencesHelper.clearLoadMoment((getEmptyModel() as RealmObject).javaClass.simpleName)
        }
    }

    protected fun generateId(): String {
        var count = getCount()

        count = (count + 1) * (-1)

        return count.toString()
    }

    protected fun getSortKey(): String {
        return "name"
    }

    open protected fun getEmptyModel(): Model? {
        return null
    }
}

