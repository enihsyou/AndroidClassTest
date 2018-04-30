package com.enihsyou.android.yuan.server

import org.hibernate.annotations.NaturalId
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import javax.persistence.CascadeType.ALL
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.OneToMany

@Entity
class YuTeacher(
    @NaturalId
    val username: String,
    val password: String,
    /**年级*/
    val grade: String,
    /**学科*/
    val subject: String,
    /**姓名*/
    val name: String,
    /**出生日期*/
    val birth: LocalDate,
    /**手机号*/
    val phone: String,
    /**简介*/
    val introduction: String
) {

    @Id
    @GeneratedValue
    val id: Long = 0

    /**年龄*/
    val age get() = LocalDate.now().year - birth.year

    @OneToMany(cascade = [ALL])
    val freetime: Map<String, YuWorkStatus> = mapOf()

    @OneToMany
    val reservation: List<YuReceipt> = listOf()
}

@Entity
class YuStudent(
    @NaturalId
    val username: String,
    val password: String
) {

    @Id
    @GeneratedValue
    val id: Long = 0
}

@Entity
class YuWorkStatus(
    @ManyToOne
    val teacher: YuTeacher,
    val date: LocalDate,
    val start: Int,
    val end: Int,
    val isFree: Boolean,
    val price: BigDecimal
) {

    @Id
    @GeneratedValue
    val id: Long = 0
}

@Entity
class YuReceipt(
    @ManyToOne
    val student: YuStudent,
    @ManyToOne
    val workTime: YuWorkStatus,
    val isPaid: Boolean,
    val createdTime: LocalDateTime
) {

    val price get() = workTime.price

    @Id
    @GeneratedValue
    val id: Long = 0
}
