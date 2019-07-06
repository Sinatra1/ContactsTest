package ru.shumilov.vladislav.contactstest.modules.core.preferences

import ru.shumilov.vladislav.contactstest.modules.core.injection.ApplicationScope

@ApplicationScope
class PhoneHelper {

    companion object {
        const val ONLY_NUMBERS = "\\D+";
    }

    open fun formattedPhoneToOnlyNumbers(phone: String?): String? {
        if (phone == null) {
            return null
        }

        return phone.replace(Regex(ONLY_NUMBERS), "")
    }
}