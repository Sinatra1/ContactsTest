package ru.shumilov.vladislav.contactstest.core.preferences

import ru.shumilov.vladislav.contactstest.modules.core.injection.ApplicationScope
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*

@ApplicationScope
class DateHelper {

    companion object {
        const val DB_FORMAT = "yyyy-MM-dd HH:mm:ss"
        const val HUMAN_DATE_FORMAT = "dd.MM.yyyy"
    }

    fun dateToDbStr(date: Date? = null, formatStr: String? = DB_FORMAT): String {
        var tmpDate = date

        if (tmpDate == null) {
            tmpDate = Date()
        }

        val sm = SimpleDateFormat(formatStr)

        return sm.format(tmpDate)
    }

    fun dbStrToDate(dbDate: String, formatStr: String? = DB_FORMAT): Date {

        val sm = SimpleDateFormat(formatStr)

        return sm.parse(dbDate)
    }

    fun dbDatetimeToHumanDate(datetime: String): String {
        val dateList = datetime.split(" ")
        val dbDate = dateList[0]
        val dbDateList = dbDate.split("-")

        return dbDateList.reversed().joinToString(".") + " " + dateList[1]
    }

    fun dbDatetimeToHumanTime(datetime: String): String {
        val dateList = datetime.split(" ")
        val dbTime = dateList[1]
        val dbTimeList = dbTime.split(":")

        return dbTimeList[0] + ":" + dbTimeList[1]
    }

    fun dbDatetimeToHumanDateTime(datetime: String): String {
        val humanTime = dbDatetimeToHumanTime(datetime)

        if (isTodayDate(datetime)) {
            return humanTime
        }

        val humanDate = dbDatetimeToHumanDate(datetime)

        return humanTime + " " + humanDate
    }

    fun isTodayDate(datetime: String): Boolean {
        val dateTimeDate = dbStrToDate(datetime)

        if (dbDatetimeToHumanDate(dateToDbStr(dateTimeDate)) == dbDatetimeToHumanDate(dateToDbStr())) {
            return true
        }

        return false
    }

    fun now(): Long {
        return System.currentTimeMillis();
    }

    fun utcToHumanDate(utcStr: String?): String {
        if (utcStr == null) {
            return ""
        }

        return SimpleDateFormat(HUMAN_DATE_FORMAT).format(utcStrToDate(utcStr))
    }

    fun utcStrToDate(utcStr: String): Date {
        return SimpleDateFormat(DB_FORMAT).parse(utcStr.replace("T", " "))
    }
}
