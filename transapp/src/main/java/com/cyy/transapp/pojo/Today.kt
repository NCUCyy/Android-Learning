package com.cyy.transapp.pojo

import androidx.room.Entity

@Entity
class Today {
    var id: Int = 0
    var date: String = ""
    var content: String = ""
    var userId: Int = 0

    constructor() {

    }

    constructor(date: String, content: String, userId: Int) : this() {
        this.date = date
        this.content = content
        this.userId = userId
    }
}