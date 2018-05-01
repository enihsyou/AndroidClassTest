package com.enihsyou.android.yuan.server

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.session.FindByIndexNameSessionRepository
import org.springframework.session.Session
import org.springframework.session.jdbc.JdbcOperationsSessionRepository
import org.springframework.session.jdbc.config.annotation.web.http.EnableJdbcHttpSession
import org.springframework.session.security.SpringSessionBackedSessionRegistry
import org.springframework.session.web.http.CookieHttpSessionIdResolver
import org.springframework.session.web.http.HttpSessionIdResolver
import org.springframework.web.filter.CommonsRequestLoggingFilter
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@EnableWebSecurity
class SecurityConfig : WebSecurityConfigurerAdapter() {

    @Autowired
    private lateinit var sessionRepository: JdbcOperationsSessionRepository

    @Bean
    fun sessionRegistry(): SpringSessionBackedSessionRegistry<out Session> {
        return SpringSessionBackedSessionRegistry(sessionRepository as FindByIndexNameSessionRepository<*>)
    }

    override fun configure(http: HttpSecurity) {
        /*认证范围*/
        http
            .authorizeRequests()
            .antMatchers(HttpMethod.OPTIONS).permitAll()
            .anyRequest().permitAll()
        /*关闭CSRF检测*/
        http
            .csrf().disable()
            .cors()
    }
}

@Configuration
class MvcConfig : WebMvcConfigurer {

    /*添加请求详情的log*/
    @Bean
    fun requestLoggingFilter(): CommonsRequestLoggingFilter {
        return CommonsRequestLoggingFilter().apply {
            setIncludeClientInfo(true)
            setIncludeQueryString(true)
            setIncludePayload(true)
        }
    }
}

@EnableJdbcHttpSession // 30min有效期
class HttpSessionConfig {

    @Bean
    fun httpSessionIdResolver(): HttpSessionIdResolver {
        return CookieHttpSessionIdResolver()
    }
}
