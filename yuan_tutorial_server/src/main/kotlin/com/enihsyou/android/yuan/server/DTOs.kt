package com.enihsyou.android.yuan.server

import com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.math.BigDecimal
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
    val price: BigDecimal,
    val introduction: String
)

data class StudentRegistrationDTO(
    val username: String,
    val password: String
)

data class TeacherWorkStatusDTO(
    val start: Int,
    val end: Int
)

data class TeacherFreeTimeDTO(
    val freetime: List<TeacherWorkStatusDTO>
)

data class ReservationDTO(
    val date: String,
    val time: String
) {

    val dateString get() = LocalDate.parse(date)
    val timeRange get() = time.split("-").map(String::toInt).let { IntRange(it.first(), it.last()) }
}

fun main(args: Array<String>) {
    println(
        jacksonObjectMapper()
            .registerModule(JavaTimeModule())
            .disable(WRITE_DATES_AS_TIMESTAMPS)
            .writeValueAsString(
                ""
            )
    )
}
