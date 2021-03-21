package `in`.nic.raj_dungarpur.data

import androidx.annotation.Keep

@Keep
data class MeetResponse(
    val link: String,
    val message: String
)