package com.seejay.badmintonkaki.models

import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Group (
    var groupId: String = "",
    var groupName: String = "",
    var groupOwnerId: String = "",
    var groupImage: String = "",
    var groupLocation: String = "",
    var groupState: String = "",
    var groupSkill: String = "",
    var groupMembers: List<String> = listOf<String>()
): Parcelable

