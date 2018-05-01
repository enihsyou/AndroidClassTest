package com.enihsyou.android.yuan.server

import com.fasterxml.jackson.annotation.JsonBackReference
import org.hibernate.annotations.NaturalId
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import javax.persistence.CascadeType.ALL
import javax.persistence.Entity
import javax.persistence.FetchType.LAZY
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.MappedSuperclass
import javax.persistence.OneToMany

@MappedSuperclass
open class DomainClass {

    @Id
    @GeneratedValue
    val id: Long = 0

    val createdTime: LocalDateTime = LocalDateTime.now()

    @Suppress("ConstantConditionIf")
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DomainClass

        if (id == 0L || other.id == 0L) return false
        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}

@Entity
class YuTeacher(
    @NaturalId
    val username: String,
    var password: String,
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
    /**辅导价格*/
    val price: BigDecimal,
    /**简介*/
    val introduction: String
) : DomainClass() {

    /**年龄*/
    val age get() = LocalDate.now().year - birth.year

    /**有空的时间，教师的每个时间片*/
    @OneToMany(cascade = [ALL], fetch = LAZY, orphanRemoval = true, mappedBy = "teacher")
    val freeTime: MutableSet<YuWorkStatus> = mutableSetOf()

    /**关联到教师的订单*/
    @OneToMany(cascade = [ALL], fetch = LAZY, orphanRemoval = true, mappedBy = "workTime")
    val reservation: MutableSet<YuReceipt> = mutableSetOf()

}

@Entity
class YuStudent(
    @NaturalId
    val username: String,
    var password: String
) : DomainClass(){

    @OneToMany(cascade = [ALL], fetch = LAZY, orphanRemoval = true, mappedBy = "student")
    val orders :MutableSet<YuReceipt> = mutableSetOf()
}

@Entity
class YuWorkStatus(
    @JsonBackReference
    @ManyToOne(fetch = LAZY)
    val teacher: YuTeacher,
    /**从几点开始*/
    val start: Int,
    /**到几点结束*/
    val end: Int
) : DomainClass() {

    val range
        get() = start..end
}

@Entity
class YuReceipt(
    /**被辅导的学生*/
    @JsonBackReference
    @ManyToOne(fetch = LAZY)
    val student: YuStudent,
    /**预约的辅导时间*/
    @ManyToOne(fetch = LAZY)
    val workTime: YuWorkStatus,
    /**预约的辅导日期*/
    val date: LocalDate,
    /**是否支付成功*/
    val isPaid: Boolean
) : DomainClass()
