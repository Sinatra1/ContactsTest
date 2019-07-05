package ru.vshumilov.agroholdingapp.modules.core.injection.modules

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import ru.vshumilov.agroholdingapp.modules.core.injection.ApplicationScope

@Module
class GsonModule {

    @Provides
    @ApplicationScope
    fun provideGson(): Gson =
            GsonBuilder()
                    .setLenient().create()
}