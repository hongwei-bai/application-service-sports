package com.hongwei.security.service

import org.apache.log4j.LogManager
import org.apache.log4j.Logger
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class MyUserDetailsService : UserDetailsService {
    private val logger: Logger = LogManager.getLogger(MyUserDetailsService::class.java)

    override fun loadUserByUsername(userName: String): UserDetails {
        return User(userName, "", emptyList())
    }
}
