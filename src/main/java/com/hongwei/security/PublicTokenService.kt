package com.hongwei.security

import com.hongwei.constants.SecurityConfigurations
import com.hongwei.security.service.MyUserDetailsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class PublicTokenService {
    @Autowired
    private lateinit var jwtUtil: JwtUtil

    @Autowired
    private lateinit var securityConfigurations: SecurityConfigurations

    @Autowired
    private lateinit var myUserDetailsService: MyUserDetailsService

    fun validateToken(token: String?): Boolean {
        val secret = securityConfigurations.secretPublic
        val userName = jwtUtil.extractUsername(token, secret)
        val userDetail = myUserDetailsService.loadUserByUsername(userName)
        return jwtUtil.validateToken(token, userDetail, secret)
    }
}