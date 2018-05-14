package com.enihsyou.android.yuan.server

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class AccountService(
    private val authenticationProvider: UserAuthenticationProvider,
    private val teacherRepository: YuTeacherRepository,
    private val studentRepository: YuStudentRepository
) {

    fun createTeacher(teacherRegistrationDTO: TeacherRegistrationDTO): YuTeacher {
        teacherRepository.findByUsername(teacherRegistrationDTO.username)
            ?.run { throw UsernameAlreadyExistException(username) }
        val password_ = PasswordUtil.encodePassword(teacherRegistrationDTO.password)
        val newTeacher = teacherRegistrationDTO
            .run { YuTeacher(username, password_, grade, subject, name, birth, phone, price, introduction) }
        return teacherRepository.save(newTeacher)
    }

    fun createStudent(studentRegistrationDTO: StudentRegistrationDTO): YuStudent {
        studentRepository.findByUsername(studentRegistrationDTO.username)
            ?.run { throw UsernameAlreadyExistException(username) }
        val password_ = PasswordUtil.encodePassword(studentRegistrationDTO.password)
        val newStudent = studentRegistrationDTO.run { YuStudent(username, password_) }
        return studentRepository.save(newStudent)
    }

    fun loginTeacher(loginDTO: LoginDTO): YuTeacher {
        val (username, password) = loginDTO
        SecurityContextHolder.getContext().authentication =
            authenticationProvider.authenticateTeacher(UsernamePasswordAuthenticationToken(username, password))
        return teacherRepository.loadByUsername(username)
    }

    fun loginStudent(loginDTO: LoginDTO): YuStudent {
        val (username, password) = loginDTO
        SecurityContextHolder.getContext().authentication =
            authenticationProvider.authenticateStudent(UsernamePasswordAuthenticationToken(username, password))
        return studentRepository.loadByUsername(username)
    }

    fun passwordTeacher(passwordDTO: PasswordDTO, username: String) {
        val teacher = teacherRepository.loadByUsername(username)
        PasswordUtil.checkEquality(passwordDTO.old, teacher.password)
        teacher.password = PasswordUtil.encodePassword(passwordDTO.new)
    }

    fun passwordStudent(passwordDTO: PasswordDTO, username: String) {
        val student = studentRepository.loadByUsername(username)
        PasswordUtil.checkEquality(passwordDTO.old, student.password)
        student.password = PasswordUtil.encodePassword(passwordDTO.new)
    }
}

@Service
class TeacherService(
    private val teacherRepository: YuTeacherRepository,
    private val workStatusRepository: YuWorkStatusRepository
) {

    fun replaceFreetime(teacherFreeTimeDTO: TeacherFreeTimeDTO, username: String) {
        val teacher = teacherRepository.loadByUsername(username)
        val map = teacherFreeTimeDTO.freetime.map {
            YuWorkStatus(teacher, it.start, it.end)
        }
        teacher.freeTime.clear()
        teacher.freeTime.addAll(map)
        teacherRepository.save(teacher)
    }

    fun loadTeacher(username: String): YuTeacher {
        return teacherRepository.loadByUsername(username)
    }
}

@Service
class StudentService(
    private val studentRepository: YuStudentRepository,
    private val workStatusRepository: YuWorkStatusRepository
) {


    fun loadStudent(username: String): YuStudent {
        return studentRepository.loadByUsername(username)
    }

    fun payOrder(id: Long, username: String): Boolean {
        val student = loadStudent(username)
        student.orders.find { it.id == id }?.apply { isPaid = true }
        return true
    }
}

@Service
class QueryService(
    private val teacherRepository: YuTeacherRepository,
    private val studentRepository: YuStudentRepository
) {

    fun detailTeacher(id: Long) = teacherRepository.loadById(id)
    fun queryTeachers(
        grade: String?, // 年级
        subject: String?, // 科目
        name: String?, // 姓名
        age: Int?, // 年龄
        date: LocalDate?, // 辅导日期
        timeInterval: IntRange?, // 辅导时间
        priceInterval: IntRange? // 价格区间
    ): List<YuTeacher> = teacherRepository.findAll().toList().asSequence()
        .filter { grade?.run { it.grade == grade } != false }
        .filter { subject?.run { it.subject == subject } != false }
        .filter { name?.run { it.name == name } != false }
        .filter { age?.run { it.age == age } != false }
        .filter { date?.run { it.reservation.filter { it.date == date }.none() } != false }
        .filter { timeInterval?.run { it.reservation.filter { it.workTime.range == timeInterval }.none() } != false }
        .filter { priceInterval?.run { it.price.toInt() in this } != false }
        .toList()

    fun reservation(teacherId: Long, reservationDTO: ReservationDTO, studentName: String) {
        val teacher = teacherRepository.loadById(teacherId)
        val student = studentRepository.loadByUsername(studentName)
        val date = reservationDTO.dateString
        val time = reservationDTO.timeRange
        val freetime = teacher.freeTime.firstOrNull { it.range == time } ?: throw NotFoundException()

        teacher.reservation.find { it.date == date && it.workTime.range == time }?.run { throw AlreadyUsedException() }
        val receipt = YuReceipt(student, freetime, date, true)
        teacher.reservation += receipt
        student.orders += receipt
        teacherRepository.save(teacher)
        studentRepository.save(student)
    }
}

@Service
class ReservationService(
    private val teacherRepository: YuTeacherRepository
)

@Component
class UserAuthenticationProvider(
    private val yuTeacherRepository: YuTeacherRepository,
    private val yuStudentRepository: YuStudentRepository
) {

    fun authenticateTeacher(authentication: Authentication) =
        authenticate(authentication, "Teacher")

    fun authenticateStudent(authentication: Authentication) =
        authenticate(authentication, "Student")

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
