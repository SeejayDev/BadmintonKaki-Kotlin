package com.seejay.badmintonkaki.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserProfile (
    val profileId: String = "",
    val name: String = "",
    val phoneNumber: String = "",
    val email: String = "",
    val profileUrl: String = ""
): Parcelable {
    override fun toString(): String {
        return name
    }
}

