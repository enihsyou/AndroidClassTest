package com.enihsyou.android.yuan.server

import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
@CrossOrigin
@RequestMapping("token")
class TokenController(val accountService: AccountService) {


    @PostMapping("teacher")
    fun teacherLogin(@RequestBody loginDTO: LoginDTO): YuTeacher {
        return accountService.loginTeacher(loginDTO)
    }

    @PostMapping("student")
    fun studentLogin(@RequestBody loginDTO: LoginDTO): YuStudent {
        return accountService.loginStudent(loginDTO)
    }
}

@RestController
@CrossOrigin
@RequestMapping("account/teacher")
class TeacherAccountController(
    private val accountService: AccountService,
    private val teacherService: TeacherService
) {

    @GetMapping
    fun fetchDetail(): YuTeacher {
        val teacher = PermissionUtil.needTeacher().name
        return teacherService.loadTeacher(teacher)
    }

    @PostMapping
    fun teacherRegister(@RequestBody teacherRegistrationDTO: TeacherRegistrationDTO): YuTeacher {
        val teacher = accountService.createTeacher(teacherRegistrationDTO)
        return teacher
    }

    @PutMapping("password")
    fun teacherPassword(@RequestBody passwordDTO: PasswordDTO) {
        val teacher = PermissionUtil.needTeacher().name
        accountService.passwordTeacher(passwordDTO, teacher)
    }

    @PutMapping("freetime")
    fun replaceFreeTime(@RequestBody teacherFreeTimeDTO: TeacherFreeTimeDTO) {
        val teacher = PermissionUtil.needTeacher().name
        teacherService.replaceFreetime(teacherFreeTimeDTO, teacher)
    }
}

@RestController
@CrossOrigin
@RequestMapping("account/student")
class StudentAccountController(
    private val accountService: AccountService,
    private val studentService: StudentService
) {

    @GetMapping
    fun fetchDetail(): YuStudent {
        val teacher = PermissionUtil.needStudent().name
        return studentService.loadStudent(teacher)
    }

    @PostMapping
    fun studentRegister(@RequestBody studentRegistrationDTO: StudentRegistrationDTO): YuStudent {
        val student = accountService.createStudent(studentRegistrationDTO)
        return student
    }

    @PutMapping("password")
    fun studentPassword(@RequestBody passwordDTO: PasswordDTO) {
        val student = PermissionUtil.needStudent().name
        accountService.passwordStudent(passwordDTO, student)
    }
}

@RestController
@CrossOrigin
@RequestMapping("teachers")
class TeacherController(
    private val queryService: QueryService
) {

    @GetMapping
    fun queryTeachers(
        @RequestParam grade: String?,
        @RequestParam subject: String?,
        @RequestParam name: String?,
        @RequestParam age: Int?,
        @RequestParam date: String?, // 2018-04-30
        @RequestParam time: String?, // 17-18
        @RequestParam price: String? // 100-200
    ): List<YuTeacher> {
        val date_ = date?.let { LocalDate.parse(it) }
        val timeInterval = time?.split("-")?.map(String::toInt)?.let { IntRange(it.first(), it.last()) }
        val priceInterval = price?.split("-")?.map(String::toInt)?.let { IntRange(it.first(), it.last()) }
        val resultList = queryService.queryTeachers(grade, subject, name, age, date_, timeInterval, priceInterval)
        return resultList
    }

    @GetMapping("{id}")
    fun detailTeacher(@PathVariable id: Long): YuTeacher {
        val teacher = queryService.detailTeacher(id)
        return teacher
    }

    @PostMapping("{id}/reservation")
    fun buyTeacher(@PathVariable id: Long, @RequestBody reservationDTO: ReservationDTO) {
        val student = PermissionUtil.needStudent().name
        queryService.reservation(id, reservationDTO, student)
    }
}
