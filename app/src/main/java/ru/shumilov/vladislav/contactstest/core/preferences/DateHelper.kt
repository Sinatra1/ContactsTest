package ru.shumilov.vladislav.contactstest.core.preferences

import ru.shumilov.vladislav.contactstest.modules.core.injection.ApplicationScope
import java.text.SimpleDateFormat
import java.util.*

@ApplicationScope
open class DateHelper {

    companion object {
        const val DB_FORMAT = "yyyy-MM-dd HH:mm:ss"
    }

    open fun dateToDbStr(date: Date? = null, formatStr: String? = DB_FORMAT): String {
        var tmpDate = date

        if (tmpDate == null) {
            tmpDate = Date()
        }

        val sm = SimpleDateFormat(formatStr)

        return sm.format(tmpDate)
    }

    open fun dbStrToDate(dbDate: String, formatStr: String? = DB_FORMAT): Date {

        val sm = SimpleDateFormat(formatStr)

        return sm.parse(dbDate)
    }

    open fun dbDatetimeToHumanDate(datetime: String): String {
        val dateList = datetime.split(" ")
        val dbDate = dateList[0]
        val dbDateList = dbDate.split("-")

        return dbDateList.reversed().joinToString(".") + " " + dateList[1]
    }

    open fun dbDatetimeToHumanTime(datetime: String): String {
        val dateList = datetime.split(" ")
        val dbTime = dateList[1]
        val dbTimeList = dbTime.split(":")

        return dbTimeList[0] + ":" + dbTimeList[1]
    }

    open fun dbDatetimeToHumanDateTime(datetime: String): String {
        val humanTime = dbDatetimeToHumanTime(datetime)

        if (isTodayDate(datetime)) {
            return humanTime
        }

        val humanDate = dbDatetimeToHumanDate(datetime)

        return humanTime + " " + humanDate
    }

    open fun isTodayDate(datetime: String): Boolean {
        val dateTimeDate = dbStrToDate(datetime)

        if (dbDatetimeToHumanDate(dateToDbStr(dateTimeDate)) == dbDatetimeToHumanDate(dateToDbStr())) {
            return true
        }

        return false
    }

    open fun now(): Long {
        return System.currentTimeMillis();
    }

}
