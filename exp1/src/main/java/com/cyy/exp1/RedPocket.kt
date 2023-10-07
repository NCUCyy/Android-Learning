package com.cyy.exp1

import androidx.compose.ui.text.resolveDefaults
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.Scanner
import kotlin.concurrent.timer
import kotlin.reflect.typeOf
import kotlin.system.exitProcess


enum class Mode {
    FixedMember, UnfixedMember
}

class MoneyException(override var message: String) : Exception(message) {
    override fun toString(): String = message
}

// 红包类(所有属性都提供默认值---等价于空参构造器)
class RedPocket(
    // 群主
    var host: User.Host,
    // 红包余额
    var remainMoney: Double = 0.0,
    // 红包大小
    var size: Int = 0,
    // 抢红包的成员
    var userList: MutableList<User> = mutableListOf<User>()
) {
    // 红包列表(userList)
    private fun addUser(user: User) {
        this.userList.add(user)
    }

    fun grab(user: User) {
        try {
            this.shareMoney(user)
        } catch (e: MoneyException) {
            println(e)
        }
    }

    private fun getRandomMoney(): Double {
        var money: Double
        if (this.userList.size == this.size - 1) {
            // 如果只剩下最后一个红包,只需要把剩余的钱都给这个人即可
            money = this.remainMoney
        } else {
            // 随机生产多少钱
            money = this.remainMoney * Math.random()
            // 金额四舍五入
            val df = DecimalFormat("#.##")
            df.roundingMode = RoundingMode.CEILING
            money = df.format(money).toDouble()

            // 判断金额是否有问题（抛出异常）-------------永远不会抛出这个异常～
            if (money > this.remainMoney) {
                throw MoneyException("红包余额不足！")
            }
        }
        return money
    }

    // 抢红包(Host/Member)
    private fun shareMoney(user: User) {
        if (this.userList.size == this.size) {
            // 人数已满
            throw MoneyException("抢红包失败---${user.name}手慢啦，红包已领完～")
        }

        // 这个人能抢到的【金额】
        var money: Double = this.getRandomMoney()

        // 添加用户到红包列表
        addUser(user)
        // user抢到多少钱
        user.getMoney(money)
        // 更新红包余额
        this.remainMoney -= money
        // 提示
        println("${user.name}抢到的金额为${user.receivedMoney} ")

    }

    fun showCase() {
        // 展示所有抢到红包的人的情况
        userList.sort()
        userList.forEach {
            var record = "${it.name}抢到的金额为${it.receivedMoney}元 ------共有钱：${it.money}元"
            if (it != host)
                println("普通成员$record")
            else
                println("群主$record")
        }
    }
}

sealed class User(var name: String, var money: Double, var receivedMoney: Double) :
    Comparable<User> {
    // 定义比较器(按照抢到的红包大小进行排序)
    override fun compareTo(other: User): Int = (this.receivedMoney - other.receivedMoney).toInt()

    // 通用方法：Member和Host都可以抢红包
    fun getMoney(money: Double) {
        this.money += money
        this.receivedMoney = money
    }


    class Host(name: String, money: Double, receivedMoney: Double) :
        User(name, money, receivedMoney) {
        // 发红包(Host)
        fun drawMoney(
            host: User.Host,
            money: Double,
            size: Int,
            users: MutableList<User> = mutableListOf()
        ): RedPocket {
            if (money > this.money)
                throw MoneyException("您的余额不足，请更换红包金额后重试！")
            this.money -= money
            // 把钱拿出去，包成一个红包
            return RedPocket(
                host = host,
                remainMoney = money,
                size = size,
                userList = users,
            )
        }
    }

    class Member(name: String, money: Double, receivedMoney: Double) :
        User(name, money, receivedMoney) {
    }
}


fun main() {
    println("-------模拟多人抢红包应用-------")

    var host = User.Host("cyy", 600.0, 0.0)
    var users = mutableListOf(
        host,
        User.Member("gyh", 10.0, 0.0),
        User.Member("xyj", 60.0, 0.0),
        User.Member("kch", 30.0, 0.0),
        User.Member("lsq", 100.0, 0.0),
        User.Member("lc", 90.0, 0.0)
    )
    println("(成员列表如下)")
    for (i in 0 until users.size) {
        println("${i + 1}、${users[i].name}---余额${users[i].money}")
    }
    var redPocket: RedPocket
    var tmp: Double = 0.0
    while (true) {
        try {
            print("输入红包的「个数」：")
            val scan = Scanner(System.`in`)
            val num = scan.nextInt()
            if (num > users.size)
                throw Exception("人数超过最大限制！")
            print("输入「金额」：")
            val money = scan.nextDouble()
            // 群主包红包
            redPocket = host.drawMoney(host, money, num)
            tmp = money
            break
        } catch (e: Exception) {
            println(e)
        }
    }
    // 打乱顺序
    users.shuffle()
    // 用户抢红包
    println("\n----------模拟抢红包的【过程】----------")
    users.forEach {
        Thread.sleep(1000)
        redPocket.grab(it)
    }

    // 显示结果
    println("\n----------展示抢红包的【结果】----------")
    println("红包总金额：$tmp")
    redPocket.showCase()
}