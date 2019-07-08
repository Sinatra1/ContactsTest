package ru.shumilov.vladislav.contactstest.modules.contacts.api

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import ru.shumilov.vladislav.contactstest.modules.contacts.models.Contact

interface ContactApi {
    @GET("generated-{number}.json")
    fun getList(@Path("number") number: String): Observable<List<Contact>>
}