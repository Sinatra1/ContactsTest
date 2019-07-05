package ru.shumilov.vladislav.contactstest.modules.contacts.api

import retrofit2.http.GET
import retrofit2.http.Path
import ru.shumilov.vladislav.contactstest.modules.contacts.models.Contact
import rx.Observable

interface ContactApi {
    @GET("generated-{number}.json")
    fun getList(@Path("number") number: String): Observable<List<Contact>>
}