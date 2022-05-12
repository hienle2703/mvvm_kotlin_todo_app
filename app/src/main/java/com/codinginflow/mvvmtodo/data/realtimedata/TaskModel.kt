package com.codinginflow.mvvmtodo.data.realtimedata

import java.text.DateFormat

data class
TaskModel(
    val id: String,
    val name: String,
    val important: Boolean = false,
    val completed: Boolean = false,
    val created: Long = System.currentTimeMillis(),
) {
    val createDateFormatted: String
        get() = DateFormat.getDateTimeInstance().format(created)
}