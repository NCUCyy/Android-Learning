package com.cyy.transapp.model

/**
 * 注意每个都必须有Not_BEGIN这个状态
 */
enum class UsernameState(val desc: String) {
    NOT_BEGIN(""),// 用户名未开始输入
    EXIST("Already Exist"),// 用户名已存在
    EMPTY("Can't Be Blank!"),// 用户名不能为空
    AVAILABLE(""),// 用户名可用
}

enum class ConfirmPasswordState(val desc: String) {
    NOT_BEGIN(""),// 确认密码未开始输入
    DIFFERENT("Password Inconsistent！"),// 两次密码不一致
    AVAILABLE(""),// 密码可用
}

enum class RegisterState(val desc: String) {
    NOT_BEGIN(""),// 未开始注册
    SUCCESS("注册成功"),// 注册成功
    FAILED("注册失败"),// 注册失败
}

enum class UsernameAndPasswordState(val desc: String) {
    NOT_BEGIN(""),// 用户名和密码未开始输入
    ERROR("Incorrect"),// 用户名和密码错误
    CORRECT(""),// 用户名和密码正确
}

enum class LoginState(val desc: String) {
    NOT_BEGIN(""), // 未开始登录
    SUCCESS("登录成功"),// 登录成功
    FAILED("登录失败"),// 登录失败
}