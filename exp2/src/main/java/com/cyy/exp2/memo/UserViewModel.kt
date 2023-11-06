package com.cyy.exp2.memo

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate

data class User(val username: String, val birth: LocalDate, val gender: String)

class UserViewModel : ViewModel() {
    private val _user: MutableStateFlow<User?> = MutableStateFlow(null)
    val user = _user.asStateFlow()

    init {
        _user.value = User("cyy", LocalDate.of(2002, 12, 8), "男")
    }
}
