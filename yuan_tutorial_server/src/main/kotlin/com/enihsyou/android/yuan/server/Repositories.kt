package com.enihsyou.android.yuan.server

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface YuTeacherRepository : JpaRepository<YuTeacher, Long> {

    fun findByUsername(username: Username): YuTeacher?
}

fun YuTeacherRepository.loadByUsername(username: Username): YuTeacher =
    findByUsername(username) ?: throw UsernameNotFoundException(username)

fun YuTeacherRepository.loadById(id: Long): YuTeacher =
    findById(id).orElseThrow { UsernameNotFoundException("") }

@Repository
interface YuStudentRepository : JpaRepository<YuStudent, Long> {

    fun findByUsername(username: Username): YuStudent?
}

fun YuStudentRepository.loadByUsername(username: Username): YuStudent =
    findByUsername(username) ?: throw UsernameNotFoundException(username)

fun YuStudentRepository.loadById(id: Long): YuStudent =
    findById(id).orElseThrow { UsernameNotFoundException("") }

@Repository
interface YuWorkStatusRepository : JpaRepository<YuWorkStatus, Long> {

    fun findByTeacher(teacher: YuTeacher): List<YuWorkStatus>
}

@Repository
interface YuReceiptRepository : JpaRepository<YuReceipt, Long>
