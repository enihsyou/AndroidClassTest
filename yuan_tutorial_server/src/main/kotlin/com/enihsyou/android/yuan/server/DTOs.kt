package com.enihsyou.android.yuan.server

import java.time.LocalDate

data class LoginDTO(
    val username: String,
    val password: String
)
data class PasswordDTO(
    val old: String,
    val new: String
)

data class TeacherRegistrationDTO(
    val username: String,
    val password: String,
    val grade: String,
    val subject: String,
    val name: String,
    val birth: LocalDate,
    val phone: String,
    val introduction: String
)

data class StudentRegistrationDTO(
    val username: String,
    val password: String
)

data class ReservationDTO(
    val date: String,
    val time: String
)
