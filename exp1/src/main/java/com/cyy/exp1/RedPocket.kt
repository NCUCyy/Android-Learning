package com.cyy.exp1


class RedPocket(var remainMoney: Double, var userList: MutableList<User> = mutableListOf<User>()) {
    // 红包列表(userList)
    fun addUser(user: User) {
        this.userList.add(user)
    }

    fun assignUser(users: MutableList<User>) {
        this.userList = users
    }

    // 抢红包(Host/Member)
    fun shareMoney(user: User) {
        // 随机生产多少钱
        val money = this.remainMoney * Math.random()

        // 判断金额是否有问题（抛出异常）
        if (money > this.remainMoney) {
            throw MoneyException("$money/$remainMoney")
        }
        // 添加用户到红包列表
//        addUser(user)
        // user抢到多少钱
        user.getMoney(money)
        // 更新红包余额
        this.remainMoney -= money
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
        fun drawMoney(money: Double): RedPocket {
            if (money > this.money)
                throw MoneyException("余额不足")
            this.money -= money
            // 把钱拿出去，包成一个红包
            return RedPocket(remainMoney = money)
        }
    }

    class Member(name: String, money: Double, receivedMoney: Double) :
        User(name, money, receivedMoney) {
    }
}

class MoneyException(override var message: String) : Exception(message) {
    override fun toString(): String = "红包余额不足！($message)"
}

fun main() {
    var host = User.Host("cyy", 600.0, 0.0)
    var users = mutableListOf(
        host,
        User.Member("gyh", 10.0, 0.0),
        User.Member("xyj", 60.0, 0.0),
        User.Member("kch", 30.0, 0.0),
        User.Member("lsq", 100.0, 0.0),
        User.Member("lc", 90.0, 0.0)
    )
    var redPocket = host.drawMoney(100.0)
    redPocket.assignUser(users)
    users.forEach {
        redPocket.shareMoney(it)
    }
    redPocket.showCase()
}