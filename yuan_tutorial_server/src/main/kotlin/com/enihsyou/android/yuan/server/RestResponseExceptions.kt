package com.enihsyou.android.yuan.server

import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.time.LocalDateTime
import javax.persistence.EntityNotFoundException
import javax.servlet.http.HttpServletRequest

abstract class RestRuntimeException(
    val status: HttpStatus,
    message: String? = null
) : RuntimeException(message)

/*用户相关*/
class UsernameNotFoundException(username: Username) : RestRuntimeException(BAD_REQUEST, "用户[$username]不存在")

class UsernameAlreadyExistException(username: Username) : RestRuntimeException(BAD_REQUEST, "用户名[$username]已存在")

class BadCredentialsException : RestRuntimeException(BAD_REQUEST, "用户名不存在或密码错误")

class PermissionNotGrantedException(username: Username) : RestRuntimeException(FORBIDDEN, "用户[$username]无此权限")

class SessionHasExpiredException : RestRuntimeException(FORBIDDEN, "当前账号已在另一处登陆")

class NeedLoginException : RestRuntimeException(FORBIDDEN, "需要登录")

class UserBannedException(username: Username) : RestRuntimeException(FORBIDDEN, "用户${username}已被禁用")

class UserDisabledException(username: Username) : RestRuntimeException(FORBIDDEN, "用户${username}未激活")

/*学校相关*/
class SchoolNotFoundException(schoolId: Long) : RestRuntimeException(BAD_REQUEST, "不存在id为[$schoolId]的学校记录")

class SchoolAlreadyExistException(name: String) : RestRuntimeException(BAD_REQUEST, "已存在名为[$name]的学校记录")

/*学期相关*/
class SemesterNotFoundException(semesterId: Long) : RestRuntimeException(BAD_REQUEST, "[$semesterId]号学期不存在")

class SemesterAlreadyExistException(year: Int, month: Int, name: String) :
    RestRuntimeException(BAD_REQUEST, "已存在[$year]年[$month]月名为[$name]的学期")

class SemesterNameFormatException(semesterName: String) :
    RestRuntimeException(BAD_REQUEST, "提供的学期名字[$semesterName]格式不正确")

/*课程相关*/
class CourseNotFoundException(courseId: Long) : RestRuntimeException(BAD_REQUEST, "[$courseId]号课程不存在")

class CourseAlreadyExistException(name: String) : RestRuntimeException(BAD_REQUEST, "名字为[$name]的课程已存在")

/*学生相关*/
class OpenIdStudentNotFoundException(openID: OpenID) : RestRuntimeException(BAD_REQUEST, "OpenId[$openID]对应的学生不存在")

class NamedStudentNotFoundException(studentNumber: StudentNumber) : RestRuntimeException(
    BAD_REQUEST,
    "学号[$studentNumber]对应的学生不存在"
)

/*签到单相关*/
class SegmentNotFoundException(segmentId: Long) : RestRuntimeException(BAD_REQUEST, "[$segmentId]号签到单不存在")

/*Token相关*/
class TokenFormatException(token: TokenType) : RestRuntimeException(BAD_REQUEST, "未知的令牌[$token]")

class TokenNotFoundException(token: TokenType) : RestRuntimeException(BAD_REQUEST, "未知的令牌[$token]")

class TokenHasExpiredException(token: TokenType) : RestRuntimeException(BAD_REQUEST, "失效过期的令牌[$token]")

//class TokenHasUsedException(token: TokenType) : RestRuntimeException(BAD_REQUEST, "令牌[$token]已被使用")

/*验证相关*/
class InvalidEmailAddressException(email: String) : RestRuntimeException(BAD_REQUEST, "邮箱[$email]格式不合规范")

class YearFormatException(year: String?) : RestRuntimeException(BAD_REQUEST, "year: [$year]不正确")

class MonthFormatException(month: String?) : RestRuntimeException(BAD_REQUEST, "month: [$month]不正确")

class RGBColorFormatException(R: Int, G: Int, B: Int) :
    RestRuntimeException(BAD_REQUEST, "RGB颜色格式<$R, $G, $B>应在0~255之间")

class StudentIdNumberShouldNotEmptyException : RestRuntimeException(BAD_REQUEST, "输入的学生学号不能为空")

class StudentNameShouldNotEmptyException : RestRuntimeException(BAD_REQUEST, "输入的学生名字不能为空")

class NullException(`object`: Any? = null) : RestRuntimeException(INTERNAL_SERVER_ERROR, "非空项为空, message: $`object`")

/*微信相关*/
class CantGetAccessTokenException(errmsg: String?) :
    RestRuntimeException(SERVICE_UNAVAILABLE, "无法获取微信AccessToken $errmsg")

class WeixinApiDecodeFailed(errcode: String?, errmsg: String?) :
    RestRuntimeException(SERVICE_UNAVAILABLE, "微信API调用失败，错误码: $errcode，错误信息: $errmsg")

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "用户名不存在")
class UserDoesNotExistException(email: String) : EntityNotFoundException("用户名${email}不存在")

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "班级不存在")
class ClassDoesNotExistException(classId: Long) : EntityNotFoundException("${classId}号班级不存在")

//@ResponseStatus(value = HttpStatus.CONFLICT, reason = "班级已存在")
//class value-already-exist-exception(name: String, courseId: Long) :
//    EntityExistsException("已存在${courseId}学科的${name}班级")


@ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "帐号或密码错误")
class PasswordDoesNotMatchException : RuntimeException("帐号或密码错误")

@ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "用户未创建过这个学期")
class DoesntOwnThisSemesterException(email: String, semesterId: Long) :
    RuntimeException("用户${email}不拥有${semesterId}号学期")

@ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "用户未创建过这个课程")
class DoesntOwnThisCourseException(email: String, courseId: Long) : RuntimeException("用户${email}不拥有${courseId}号课程")

@ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "用户未创建过这个班级")
class DoesntOwnThisClassException(email: String, classId: Long) : RuntimeException("用户${email}不拥有${classId}号班级")

@ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "用户未创建过这个签到段")
class DoesntOwnThisSegmentException(email: String, segmentId: Long) : RuntimeException("用户${email}不拥有${segmentId}号签到")

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "不支持这个搜索方式")
class FormatNotSupportedException(method: String) : RuntimeException("不支持${method}格式")

@ResponseStatus(value = HttpStatus.CONFLICT, reason = "学生已经在班级列表中")
class StudentAlreadyInClassException : RuntimeException("学生已经在班级列表中")

/**
{
"url": "/dashboard/classes/semesters/50",
"reason": "日期时间格式不合规范",
"message": "Invalid value for MonthOfYear: 33"
}
 */
@RestControllerAdvice
class GlobalControllerExceptionHandler {

    //    @ExceptionHandler(DateTimeException::class)
    //    @ResponseStatus(HttpStatus.BAD_REQUEST)
    //    fun handleDataTimeOutRange(req: HttpServletRequest, e: Exception): RestErrorInfo {
    //        return RestErrorInfo(req.requestURI, e, "日期时间格式不合规范")
    //    }

    @ExceptionHandler(RestRuntimeException::class)
    fun handleRuntimeRestException(req: HttpServletRequest, e: RestRuntimeException): ResponseEntity<RestErrorInfo> {
        return ResponseEntity.status(e.status).body(
            RestErrorInfo(
                req.userPrincipal?.name ?: "Anonymous",
                LocalDateTime.now(),
                e.status.value(),
                e.status.reasonPhrase,
                e.message,
                req.requestURI
            )
        )
    }
}

data class RestErrorInfo(
    val made_by: String,
    val timestamp: LocalDateTime,
    val status: Int,
    val error: String,
    val message: String? = null,
    val path: String
)
