package ru.shumilov.vladislav.contactstest.modules.core.injection.modules

import android.content.Context
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import ru.shumilov.vladislav.contactstest.modules.core.preferenses.DaoPreferencesHelper
import ru.shumilov.vladislav.contactstest.modules.core.injection.ApplicationContext
import ru.shumilov.vladislav.contactstest.modules.core.injection.ApplicationScope


@Module
@ApplicationScope
class HttpModule {

    companion object {
        const val CACHE_SIZE = 100L * 1024 * 1024
    }

    @Provides
    @ApplicationScope
    fun provideOkHttpClient(@ApplicationContext context: Context, daoPreferencesHelper: DaoPreferencesHelper): OkHttpClient {
        return with(OkHttpClient.Builder()) {
            cache(Cache(context.cacheDir, CACHE_SIZE))

            networkInterceptors().add(object : Interceptor {
                override fun intercept(chain: Interceptor.Chain?): Response {
                    var original = chain?.request()

                    var builder = original?.newBuilder()
                            ?.method(original.method(), original.body())

                    val request = builder?.build()

                    var response: Response = chain!!.proceed(request)

                    return response
                }

            })

            build()
        }
    }
}