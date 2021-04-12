package com.hongwei.security.filters

import com.hongwei.constants.Constants.Security.PUBLIC_ACCESS_STUB_USER
import com.hongwei.constants.SecurityConfigurations
import com.hongwei.security.PublicTokenService
import com.hongwei.security.service.MyUserDetailsService
import org.apache.log4j.LogManager
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders.*
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class JwtRequestFilter : OncePerRequestFilter() {
    private val _logger: Logger = LogManager.getLogger(JwtRequestFilter::class.java)

    @Autowired
    private lateinit var userDetailsService: MyUserDetailsService

    @Autowired
    private lateinit var securityConfigurations: SecurityConfigurations

    @Autowired
    private lateinit var publicTokenService: PublicTokenService

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        val authorizationHeader = request.getHeader(securityConfigurations.authorizationHeader)
        if (request.method == HttpMethod.OPTIONS.name) {
            grantAccess(request)
        } else if (authorizationHeader != null && authorizationHeader.startsWith(securityConfigurations.authorizationBearer)) {
            val jwt = authorizationHeader.substring(securityConfigurations.authorizationBearer.length + 1)
            publicTokenService.validateToken(jwt)

            if (SecurityContextHolder.getContext().authentication == null) {
                grantAccess(request)
            }
        }

        appendCORSHeaders(response)
        chain.doFilter(request, response)
    }

    private fun grantAccess(request: HttpServletRequest) {
        val userDetails = userDetailsService.loadUserByUsername(PUBLIC_ACCESS_STUB_USER)

        // Grant access
        val usernamePasswordAuthenticationToken = UsernamePasswordAuthenticationToken(
                userDetails, userDetails.password, userDetails.authorities)
        usernamePasswordAuthenticationToken.details = WebAuthenticationDetailsSource().buildDetails(request)
        SecurityContextHolder.getContext().authentication = usernamePasswordAuthenticationToken
    }

    private fun appendCORSHeaders(response: HttpServletResponse) {
        response.setHeader(ACCESS_CONTROL_ALLOW_CREDENTIALS, true.toString())
        response.addHeader(ACCESS_CONTROL_ALLOW_HEADERS, "$CONTENT_TYPE,${securityConfigurations.authorizationHeader}")
        response.addHeader(ACCESS_CONTROL_ALLOW_ORIGIN, securityConfigurations.corsAllowDomain)
    }
}