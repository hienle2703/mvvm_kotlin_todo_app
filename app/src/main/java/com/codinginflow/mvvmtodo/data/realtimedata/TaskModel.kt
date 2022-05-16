package com.codinginflow.mvvmtodo.data.realtimedata

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.text.DateFormat

@Parcelize
data class
TaskModel(
    val id: String = "",
    val name: String = "",
    val important: Boolean = false,
    val completed: Boolean = false,
    val created: Long = System.currentTimeMillis(),
) : Parcelable {
    val createDateFormatted: String
        get() = DateFormat.getDateTimeInstance().format(created)
}