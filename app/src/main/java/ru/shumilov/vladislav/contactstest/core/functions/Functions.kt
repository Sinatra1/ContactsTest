package ru.simpls.brs2.commons.functions

import timber.log.Timber


inline fun safe(f: (() -> Unit)) {
    try {
        f()
    } catch (e: Exception) {
        Timber.e(e, e.message)
    }
}

inline fun wasInit(f: () -> Unit): Boolean {
    try {
        f()
    } catch(e: RuntimeException) {
        return false
    }
    return true
}
