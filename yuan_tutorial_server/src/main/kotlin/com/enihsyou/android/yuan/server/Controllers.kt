package com.enihsyou.android.yuan.server

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
@RequestMapping("token")
class TokenController(val accountService: AccountService) {

    /**使用[LoginDTO.username]为用户名，[LoginDTO.password]为密码，进行教师身份的登录*/
    @PostMapping("teacher")
    fun teacherLogin(@RequestBody loginDTO: LoginDTO): YuTeacher {
        return accountService.loginTeacher(loginDTO)
    }

    /**使用[LoginDTO.username]为用户名，[LoginDTO.password]为密码，进行学生身份的登录*/
    @PostMapping("student")
    fun studentLogin(@RequestBody loginDTO: LoginDTO): YuStudent {
        return accountService.loginStudent(loginDTO)
    }
}

@RestController
@RequestMapping("account/teacher")
class TeacherAccountController(
    private val accountService: AccountService,
    private val teacherService: TeacherService
) {

    @PostMapping
    fun teacherRegister(@RequestBody teacherRegistrationDTO: TeacherRegistrationDTO): YuTeacher {
        return accountService.createTeacher(teacherRegistrationDTO)
    }

    @GetMapping
    fun fetchDetail(): YuTeacher {
        val teacher = PermissionUtil.needTeacher().name
        return teacherService.loadTeacher(teacher)
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
@RequestMapping("account/student")
class StudentAccountController(
    private val accountService: AccountService,
    private val studentService: StudentService
) {

    @PostMapping
    fun studentRegister(@RequestBody studentRegistrationDTO: StudentRegistrationDTO): YuStudent {
        return accountService.createStudent(studentRegistrationDTO)
    }

    @GetMapping
    fun fetchDetail(): YuStudent {
        val student = PermissionUtil.needStudent().name
        return studentService.loadStudent(student)
    }

    @PutMapping("password")
    fun studentPassword(@RequestBody passwordDTO: PasswordDTO) {
        val student = PermissionUtil.needStudent().name
        accountService.passwordStudent(passwordDTO, student)
    }

    @PostMapping("orders")
    fun payOrder(@RequestParam id: Long): Boolean {
        val student = PermissionUtil.needStudent().name
        return studentService.payOrder(id, student)
    }
}

@RestController
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
        return queryService.queryTeachers(grade, subject, name, age, date_, timeInterval, priceInterval)
    }

    @GetMapping("{id}")
    fun detailTeacher(@PathVariable id: Long): YuTeacher {
        return queryService.detailTeacher(id)
    }

    @PostMapping("{id}/reservation")
    fun buyTeacher(@PathVariable id: Long, @RequestBody reservationDTO: ReservationDTO) {
        val student = PermissionUtil.needStudent().name
        queryService.reservation(id, reservationDTO, student)
    }
}
