package com.cyy.exp1

import android.widget.Switch
import androidx.compose.animation.core.withInfiniteAnimationFrameNanos
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.ui.unit.DpSize
import java.math.RoundingMode
import java.text.DecimalFormat


// 红包类(所有属性都提供默认值---等价于空参构造器)
class RedPocket(
    // 红包余额
    var remainMoney: Double = 0.0,
    // 红包大小
    var size: Int = 0,
    // 抢红包的成员
    var userList: MutableList<User> = mutableListOf<User>(),
    var mode: Mode = Mode.Unfixed_Member
) {


    // 红包列表(userList)
    private fun addUser(user: User) {
        this.userList.add(user)
    }

    fun assignUsers(users: MutableList<User>) {
        this.userList = users
    }

    fun assignSize(size: Int) {
        this.size = size
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
        if (mode == Mode.Fixed_Member) {
            // 判断这个人是否能抢
            if (this.userList.indexOf(user) == -1) {
                throw MoneyException("抢红包失败---${user.name}不能抢这个红包哟～")
                return
            }
        }
        if (this.userList.size == this.size) {
            // 人数已满
            throw MoneyException("抢红包失败---${user.name}手慢啦，红包已领完～")
            return
        }
        // 这个人能抢到的【金额】
        var money: Double = this.getRandomMoney()

        if (this.mode == Mode.Unfixed_Member)
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
        userList.forEach {
            println("${it.name}抢到的金额为${it.receivedMoney} ")
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
            money: Double,
            size: Int,
            mode: Mode,
            users: MutableList<User> = mutableListOf()
        ): RedPocket {
            if (money > this.money)
                throw MoneyException("您的余额不足，请更换红包金额后重试！")
            this.money -= money
            // 把钱拿出去，包成一个红包
            return RedPocket(remainMoney = money, size = size, userList = users, mode = mode)
        }
    }

    class Member(name: String, money: Double, receivedMoney: Double) :
        User(name, money, receivedMoney) {
    }
}

class MoneyException(override var message: String) : Exception(message) {
    override fun toString(): String = message
}


// 成员固定
fun drawFixed() {
    var host = User.Host("cyy", 600.0, 0.0)
    var users = mutableListOf(
        host,
        User.Member("gyh", 10.0, 0.0),
        User.Member("xyj", 60.0, 0.0),
        User.Member("kch", 30.0, 0.0),
        User.Member("lsq", 100.0, 0.0),
        User.Member("lc", 90.0, 0.0)
    )
    var redPocket = host.drawMoney(200.0, 10, Mode.Fixed_Member, users)
    // 用户抢红包
    users.forEach {
        redPocket.grab(it)
    }
    var user = User.Member("xwz", 90.0, 0.0)
    redPocket.grab(user)
    // 显示结果
    println("----------抢红包的结果----------")
    redPocket.showCase()
}

// 成员不固定
fun drawUnFixed() {
    var host = User.Host("cyy", 600.0, 0.0)
    var member1 = User.Member("gyh", 10.0, 0.0)
    var member2 = User.Member("xyj", 60.0, 0.0)
    var member3 = User.Member("kch", 30.0, 0.0)
    val member4 = User.Member("lsq", 100.0, 0.0)
    var member5 = User.Member("lc", 90.0, 0.0)

    // 规定红包的{金额}和{大小}
    var redPocket = host.drawMoney(money = 100.0, size = 3, mode = Mode.Unfixed_Member)
    // 用户抢红包
    try {
        redPocket.grab(member1)
        redPocket.grab(member2)
        redPocket.grab(member3)
        // 抢红包人数达到限制后，其他用户抢，只会在第一个人抢的时候抛一次异常(需要引入线程技术：并发问题)
        redPocket.grab(member4)
        redPocket.grab(member5)
    } catch (e: MoneyException) {
        println(e)
    }
    // 显示结果
    println("----------抢红包的结果----------")
    redPocket.showCase()
}

enum class Mode {
    Fixed_Member, Unfixed_Member

}

fun main() {
    println("-----------人员固定-----------")
    drawFixed()
    println("\n@@@@@@@@@@@@@@@@@@@@@@@@@\n")
    println("-----------人员不固定-----------")
    drawUnFixed()
}