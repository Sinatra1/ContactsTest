package ru.shumilov.vladislav.contactstest.modules.core.preferences

import android.content.Context
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException
import ru.shumilov.vladislav.contactstest.R
import ru.shumilov.vladislav.contactstest.modules.core.injection.ApplicationContext
import ru.shumilov.vladislav.contactstest.modules.core.injection.ApplicationScope
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

@ApplicationScope
class ErrorHelper @Inject constructor(@ApplicationContext private val context: Context) {

    fun getErrorMessage(error: Throwable): String {
        Timber.e(error)

        return when (error) {
            is HttpException -> when (error.code()) {
                304 -> context.getString(R.string.not_modified_error)
                400 -> context.getString(R.string.bad_request_error)
                401 -> context.getString(R.string.unauthorized_error)
                403 -> context.getString(R.string.forbidden_error)
                404 -> context.getString(R.string.not_found_error)
                405 -> context.getString(R.string.method_not_allowed_error)
                409 -> context.getString(R.string.conflict_error)
                422 -> context.getString(R.string.unprocessable_error)
                500 -> context.getString(R.string.server_error)
                else -> context.getString(R.string.unknown_error)
            }
            is IOException -> context.getString(R.string.no_network_connection)
            else -> context.getString(R.string.unknown_error)
        }
    }
}