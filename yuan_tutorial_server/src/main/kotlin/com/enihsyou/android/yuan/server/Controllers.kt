package com.enihsyou.android.yuan.server

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("token")
class TokenController(val accountService: AccountService) {


    @PostMapping("teacher")
    fun teacherLogin(@RequestBody loginDTO: LoginDTO) {
        accountService.loginTeacher(loginDTO)
    }

    @PostMapping("student")
    fun studentLogin(@RequestBody loginDTO: LoginDTO) {
        accountService.loginStudent(loginDTO)
    }
}

@RestController
@RequestMapping("account/teacher")
class TeacherAccountController(val accountService: AccountService) {


    @PostMapping
    fun teacherRegister(@RequestBody teacherRegistrationDTO: TeacherRegistrationDTO) {
        accountService.createTeacher(teacherRegistrationDTO)
    }

    @PutMapping("password")
    fun teacherPassword(@RequestBody passwordDTO: PasswordDTO) {
        PermissionUtil.needTeacher()
        accountService.passwordTeacher(passwordDTO)
    }
}

@RestController
@RequestMapping("account/student")
class StudentAccountController(val accountService: AccountService) {


    @PostMapping
    fun studentRegister(@RequestBody studentRegistrationDTO: StudentRegistrationDTO) {
        accountService.createStudent(studentRegistrationDTO)
    }

    @PutMapping("password")
    fun studentPassword(@RequestBody passwordDTO: PasswordDTO) {
        accountService.passwordStudent(passwordDTO)
    }
}

@RestController
@RequestMapping("teachers")
class TeacherController {

    @GetMapping
    fun queryTeachers(
        @RequestParam grade: String?,
        @RequestParam subject: String?,
        @RequestParam name: String?,
        @RequestParam age: String?,
        @RequestParam date: String?, // 2018-04-30
        @RequestParam time: String?, // 17-18
        @RequestParam price: String? // 100-200
    ) {
    }

    @GetMapping("{id}")
    fun detailTeacher(@PathVariable id: Long) {
    }

    @PostMapping("{id}/reservation")
    fun buyTeacher(@PathVariable id: Long, @RequestBody reservationDTO: ReservationDTO) {
    }
}
