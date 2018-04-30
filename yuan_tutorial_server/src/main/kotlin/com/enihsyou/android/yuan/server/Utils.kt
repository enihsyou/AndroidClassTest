package com.enihsyou.android.yuan.server

import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.client.RestTemplate
import kotlin.reflect.KClass

/**密码相关的帮助类*/
object PasswordUtil {

    private val passwordEncoder = BCryptPasswordEncoder()

    /**
     * 对比纯文本未加密形式的[rawPassword]和数据库中已编码的[encodedPassword]
     * @param rawPassword 密码的原始格式
     * @param encodedPassword 数据库中加密过的密码
     * @exception BadCredentialsException 如果未通过验证则抛出异常
     */
    fun checkEquality(rawPassword: UserPassword, encodedPassword: UserPassword) {
        if (!passwordEncoder.matches(rawPassword, encodedPassword))
            throw BadCredentialsException()
    }

    fun encodePassword(password: UserPassword): String {
        return passwordEncoder.encode(password)
    }
}


object HttpUtil {
    private val restTemplate = RestTemplate()

    fun <T : Any> get(url: String, `object`: KClass<T>): T? {
        return restTemplate.getForObject(url, `object`.java)
    }

    fun <T : Any> post(url: String, content: String, headers: HttpHeaders, `object`: KClass<T>): T? {
        val entity = HttpEntity(content, headers)
        return restTemplate.postForObject(url, entity, `object`.java)
    }
}

///**和URL生成相关的帮助类*/
//internal object UrlUtil {
//
//    private val baseUiUrl = UriComponentsBuilder.fromHttpUrl(URL_UI_BASE)
//
//    fun urlForInvitation(token: TokenType, invitee: Username): URI {
//        return baseUiUrl.cloneBuilder()
//            .path("register")
//            .queryParam("token", token)
//            .queryParam("username", invitee)
//            .build().encode().toUri()
//    }
//
//    fun urlForPasswordReset(token: TokenType): URI {
//        return baseUiUrl.cloneBuilder()
//            .path("retrieve")
//            .queryParam("token", token)
//            .build().encode().toUri()
//    }
//
//    @Deprecated("", level = ERROR)
//    fun urlForCourse(course: Course): URI {
//        return baseUiUrl.cloneBuilder()
//            .path("courses")
//            .path(course.id.toString())
//            .build().encode().toUri()
//    }
//
//    @Deprecated("", level = ERROR)
//    fun urlForSegment(segment: Segment): URI {
//        return baseUiUrl.cloneBuilder()
//            .path("segments")
//            .path(segment.id.toString())
//            .build().encode().toUri()
//    }
//}

/**和权限检测相关的帮助类*/
object PermissionUtil {

    private fun checkPermission(authentication: Authentication, role: String) {
        if (authentication.authorities?.none { it.authority in role } != false)
            throw PermissionNotGrantedException(authentication.name)
    }

    fun needTeacher() = currentUser().apply { checkPermission(this, "Teacher")  }
    fun needStudent() = currentUser().apply { checkPermission(this, "Student")  }
    fun currentUser(): Authentication =
        SecurityContextHolder.getContext().authentication ?: throw NeedLoginException()
}
