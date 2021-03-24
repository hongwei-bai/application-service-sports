package com.hongwei.security.filters

import com.hongwei.constants.SecurityConfigurations
import com.hongwei.security.service.AuthorisationService
import com.hongwei.security.service.MyUserDetailsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class JwtRequestFilter : OncePerRequestFilter() {
    @Autowired
    private lateinit var userDetailsService: MyUserDetailsService

    @Autowired
    private lateinit var authorisationService: AuthorisationService

    @Autowired
    private lateinit var securityConfigurations: SecurityConfigurations

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        val authorizationHeader = request.getHeader(securityConfigurations.authorizationHeader)
        if (authorizationHeader != null && authorizationHeader.startsWith(securityConfigurations.authorizationBearer)) {
            val jwt = authorizationHeader.substring(securityConfigurations.authorizationBearer.length + 1)
            val authorisationResponse = authorisationService.authorise(jwt)

            if (SecurityContextHolder.getContext().authentication == null
                    && authorisationResponse.validated == true) {
                val userDetails = userDetailsService.loadUserByUsername("stub")

                // Grant access
                val usernamePasswordAuthenticationToken = UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.authorities)
                usernamePasswordAuthenticationToken.details = WebAuthenticationDetailsSource().buildDetails(request)
                SecurityContextHolder.getContext().authentication = usernamePasswordAuthenticationToken
            }
        }

        chain.doFilter(request, response)
    }
}