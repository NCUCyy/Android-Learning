package com.cyy.exp1

import java.time.LocalDate

class MyCalendar {

    /**
     * 判断是否位闰年
     */
    fun isLeapYear(year: Int): Boolean {
        return (year % 400 == 0) || (year % 4 == 0 && year % 100 != 0)
    }

    /**
     * 获得月总天数
     */
    fun getDays(year: Int, month: Int): Int {
        return when (month) {
            1, 3, 5, 7, 8, 10, 12 -> 31
            2 -> if (isLeapYear(year)) 29 else 28
            4, 6, 9, 11 -> 30
            else -> throw IllegalStateException("Unexpected value: $month")
        }
    }

    /**
     * 获得连续 monthLength个月——每月的总天数
     *
     * @param year        年
     * @param month       月
     * @param monthLength 连续的月份数
     * @return 返回数组
     */
    fun getDaysArray(year: Int, month: Int, monthLength: Int): IntArray {
        val arr = IntArray(monthLength)
        for (i in 0 until monthLength) {
            arr[i] = getDays(year, month + i)
        }
        return arr
    }

    /**
     * 获得连续 monthLength个月——每月第一天的星期数
     *
     * @param year        年
     * @param month       月
     * @param monthLength 连续月份数
     * @return 返回数组
     */
    fun getFirstDayOfWeekArray(year: Int, month: Int, monthLength: Int): IntArray {
        var localDate: LocalDate
        val arr = IntArray(monthLength)
        for (i in 0 until monthLength) {
            localDate = LocalDate.of(year, month + i, 1)
            arr[i] = localDate.dayOfWeek.value
        }
        return arr
    }

    fun getLastDayOfWeekArray(year: Int, month: Int, monthLength: Int): IntArray {
        var localDate: LocalDate
        val arr = IntArray(monthLength)
        for (i in 0 until monthLength) {
            localDate = LocalDate.of(year, month + i, getDays(year, month + i))
            arr[i] = localDate.dayOfWeek.value
        }
        return arr
    }

    /**
     * 获得日历的标题
     *
     * @param year
     * @param month
     * @return
     */
    fun getCalendarTitle(year: Int, month: Int, monthLength: Int): String {
        var result = ""
        for (i in 0 until monthLength) {
            result += "%15s年%2s月%20s".format(year, month + i, " ")
        }
        result += "\n"
        for (i in 0 until monthLength) {
            result += "%4s%4s%4s%4s%4s%4s%4s%10s".format(
                "SUN",
                "MON",
                "TUS",
                "WED",
                "THU",
                "FRI",
                "SAT",
                " "
            )
        }
        result += "\n"
        return result
    }

    /**
     * 获得日历的内容
     *
     * @param year  年
     * @param month 月
     */
    fun getCalendar(year: Int, month: Int, monthLength: Int): String {
        //连续monthLength个月，每月的总天数
        val days = getDaysArray(year, month, monthLength)
        //连续monthLength个月，每月第一天的星期数
        val firstDay = getFirstDayOfWeekArray(year, month, monthLength)
        val lastDay = getLastDayOfWeekArray(year, month, monthLength)
        //每个月，当前行，遍历到的天数
        val currentDays = IntArray(monthLength) { 1 }
        val first = BooleanArray(monthLength) { true }
        val end = BooleanArray(monthLength) { false }

        var result = getCalendarTitle(year, month, monthLength)

        //行循环：
        for (line in 0 until 6) {
            //月遍历：
            for (i in 0 until monthLength) {
                //第一行加空格：
                if (first[i]) {
                    val blankLength = firstDay[i] % 7 * 4
                    if (blankLength != 0) {
                        val formatStr = "%" + blankLength + "s"
                        result += formatStr.format(" ")
                        first[i] = false
                    }
                }
                //如果该月已经输出完毕，就用等长度的空格补充最后一行
                if (currentDays[i] > days[i]) {
                    result += "%28s".format(" ")
                    result += "%10s".format(" ")
                    continue
                }
                //输出一行的日：（循环规定此行此月写几个日子）
                for (j in firstDay[i] % 7 until 7) {
                    result += "%4d".format(currentDays[i]++)
                    if (currentDays[i] > days[i]) {
                        end[i] = true
                        break
                    }
                }
                //最后一行末尾加空格：
                if (end[i]) {
                    val blankLength = (6 - lastDay[i] % 7) * 4
                    if (blankLength != 0) {
                        val formatStr = "%" + blankLength + "s"
                        result += formatStr.format(" ")
                    }
                }
                firstDay[i] = 0
                result += "%10s".format(" ")

            }
            result += "\n"
        }
        return result
    }


}

fun main(args: Array<String>) {
    val calendar = MyCalendar()
    val year = 2023
    val monthLength = 4
    var result = ""
    for (month in 1..12 step monthLength) {
        result += calendar.getCalendar(year, month, monthLength)
    }
    println(result)
}