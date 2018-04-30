package com.enihsyou.android.yuan.server

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface YuTeacherRepository : JpaRepository<YuTeacher, Long> {

    fun findByUsername(username: Username): YuTeacher?
}

fun YuTeacherRepository.loadByUsername(username: Username): YuTeacher =
    findByUsername(username) ?: throw UsernameNotFoundException(username)

@Repository
interface YuStudentRepository : JpaRepository<YuStudent, Long> {

    fun findByUsername(username: Username): YuStudent?
}

fun YuStudentRepository.loadByUsername(username: Username): YuStudent =
    findByUsername(username) ?: throw UsernameNotFoundException(username)

@Repository
interface YuWorkStatusRepository : JpaRepository<YuWorkStatus, Long>

@Repository
interface YuReceiptRepository : JpaRepository<YuReceipt, Long>
