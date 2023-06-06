package com.seejay.badmintonkaki.models

import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Session (
    var sessionId: String = "",
    var groupId: String = "",
    var sessionDate: String = "",
    var sessionTimeStart: String = "",
    var sessionTimeEnd: String = "",
    var sessionDescription: String = "",
    var sessionLimit: Int = 0,
    var sessionMembers: List<String> = listOf<String>()
): Parcelable
