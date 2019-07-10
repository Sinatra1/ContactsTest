package ru.shumilov.vladislav.contactstest.modules.core.preferences

import ru.shumilov.vladislav.contactstest.modules.core.injection.ApplicationScope

@ApplicationScope
class TextHelper {

    fun getTextWithFirstUpper(text: String?): String {
        if (text == null) {
            return ""
        }

        return Character.toUpperCase(text[0]) + text.substring(1)
    }
}