package ru.shumilov.vladislav.contactstest

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import ru.shumilov.vladislav.contactstest.modules.contacts.ui.list.ContactsListFragment

class AppActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.app_activity)

        showContactsList()
    }

    private fun showContactsList() {
        showFragment(ContactsListFragment.newInstance())
    }

    private fun showFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.container, fragment)
        fragmentTransaction.addToBackStack(fragment.javaClass.simpleName);
        fragmentTransaction.commit()
    }
}
