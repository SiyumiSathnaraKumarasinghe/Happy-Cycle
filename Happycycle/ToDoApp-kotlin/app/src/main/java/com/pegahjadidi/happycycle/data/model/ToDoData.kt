package com.pegahjadidi.happycycle.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ToDoData(
    var id: Int, // Keep this if you still need an ID field
    var title: String,
    var priority: Priority,
    var description: String,
    var timeStamp: Long,
    var completed: Boolean
) : Parcelable