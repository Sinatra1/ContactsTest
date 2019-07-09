package ru.shumilov.vladislav.contactstest

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import ru.shumilov.vladislav.contactstest.modules.contacts.ui.list.ContactsListFragment
import androidx.navigation.NavController
import androidx.navigation.Navigation


class AppActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.app_activity)

        navController = Navigation.findNavController(this, R.id.nav_host_fragment)
    }
}
