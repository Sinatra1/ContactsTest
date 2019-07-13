package ru.shumilov.vladislav.contactstest.modules.core.functions

import timber.log.Timber

inline fun safe(f: (() -> Unit)) {
    try {
        f()
    } catch (e: Exception) {
        Timber.e(e, e.message)
    }
}