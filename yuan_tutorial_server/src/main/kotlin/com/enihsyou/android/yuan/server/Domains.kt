package com.enihsyou.android.yuan.server

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.annotations.NaturalId
import org.springframework.data.jpa.domain.AbstractPersistable
import java.math.BigDecimal
import java.time.LocalDate
import javax.persistence.CascadeType.ALL
import javax.persistence.Entity
import javax.persistence.FetchType.LAZY
import javax.persistence.ManyToOne
import javax.persistence.OneToMany

@Entity
data class YuTeacher(
    @NaturalId
    val username: String,
    @JsonIgnore
    var password: String,
    /**年级*/
    val grade: String,
    /**学科*/
    val subject: String,
    /**姓名*/
    val name: String,
    /**出生日期（年月日）*/
    val birth: LocalDate,
    /**手机号*/
    val phone: String,
    /**辅导价格*/
    val price: BigDecimal,
    /**简介*/
    var introduction: String?
) : AbstractPersistable<Long>() {

    /**年龄*/
    val age get() = LocalDate.now().year - birth.year

    /**有空的时间，教师的每个时间片*/
    @OneToMany(cascade = [ALL], mappedBy = "teacher", orphanRemoval = true)
    val freeTime: MutableSet<YuWorkStatus> = mutableSetOf()

    /**关联到教师的订单*/
    @OneToMany(cascade = [ALL], mappedBy = "teacher")
    val reservation: MutableSet<YuReceipt> = mutableSetOf()
}

@Entity
class YuStudent(
    @NaturalId
    val username: String,
    @JsonIgnore
    var password: String
) : AbstractPersistable<Long>() {

    @OneToMany(cascade = [ALL], mappedBy = "student")
    val orders: MutableSet<YuReceipt> = mutableSetOf()
}

@Entity
class YuWorkStatus(
    @JsonBackReference
    @ManyToOne(fetch = LAZY)
    val teacher: YuTeacher,
    /**从几点开始*/
    private val start: Int,
    /**到几点结束*/
    private val end: Int
) : AbstractPersistable<Long>() {

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
    var isPaid: Boolean
) : AbstractPersistable<Long>() {

    @ManyToOne(fetch = LAZY)
    @JsonBackReference
    val teacher = workTime.teacher

    val teacherJson get() = teacher.copy().apply { reservation.clear() }
}
