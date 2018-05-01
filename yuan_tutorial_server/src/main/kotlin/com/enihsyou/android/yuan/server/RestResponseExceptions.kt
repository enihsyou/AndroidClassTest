package com.enihsyou.android.yuan.server

import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.time.LocalDateTime
import javax.servlet.http.HttpServletRequest

abstract class RestRuntimeException(
    val status: HttpStatus,
    message: String? = null
) : RuntimeException(message)

class UsernameNotFoundException(username: Username) : RestRuntimeException(BAD_REQUEST, "用户[$username]不存在")

class UsernameAlreadyExistException(username: Username) : RestRuntimeException(BAD_REQUEST, "用户名[$username]已存在")

class BadCredentialsException : RestRuntimeException(BAD_REQUEST, "用户名不存在或密码错误")

class PermissionNotGrantedException(username: Username) : RestRuntimeException(FORBIDDEN, "用户[$username]无此权限")

class NeedLoginException : RestRuntimeException(FORBIDDEN, "需要登录")

class NotFoundException : RestRuntimeException(NOT_FOUND, "没这玩意")

class AlreadyUsedException : RestRuntimeException(FORBIDDEN, "已经被预约")

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
