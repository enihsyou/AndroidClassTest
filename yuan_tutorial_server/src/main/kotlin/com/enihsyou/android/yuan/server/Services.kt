package com.enihsyou.android.yuan.server

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service

@Service
class AccountService(
    val authenticationProvider: UserAuthenticationProvider,
    val teacherRepository: YuTeacherRepository,
    val studentRepository: YuStudentRepository
) {

    fun createTeacher(teacherRegistrationDTO: TeacherRegistrationDTO) {}
    fun createStudent(studentRegistrationDTO: StudentRegistrationDTO) {}
    fun loginTeacher(loginDTO: LoginDTO) {}
    fun loginStudent(loginDTO: LoginDTO) {}
    fun passwordTeacher(passwordDTO: PasswordDTO) {}
    fun passwordStudent(passwordDTO: PasswordDTO) {}
}

@Service
class QueryService {

}

@Component
class UserAuthenticationProvider(
    val yuTeacherRepository: YuTeacherRepository,
    val yuStudentRepository: YuStudentRepository
) {

    fun authenticateTeacher(authentication: Authentication) {
        authenticate(authentication, "Teacher")
    }

    fun authenticateStudent(authentication: Authentication) {
        authenticate(authentication, "Student")
    }

    private fun authenticate(authentication: Authentication, role: String): Authentication {
        val inputUsername = authentication.name as String
        val inputPassword = authentication.credentials as String

        return loadUser(inputUsername, role)
            .let { password ->
                PasswordUtil.checkEquality(inputPassword, password)

                val authorities = listOf(SimpleGrantedAuthority(role))

                UsernamePasswordAuthenticationToken(inputUsername, password, authorities)
            }
    }

    private fun loadUser(username: String, role: String): String {
        return when (role) {
            "Teacher" -> yuTeacherRepository.loadByUsername(username).password
            "Student" -> yuStudentRepository.loadByUsername(username).password
            else      -> throw NoWhenBranchMatchedException()
        }
    }
}
