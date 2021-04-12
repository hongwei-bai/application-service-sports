package com.hongwei.security.service

import com.hongwei.constants.Constants.Security.PUBLIC_ACCESS_STUB_USER
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class MyUserDetailsService : UserDetailsService {
    override fun loadUserByUsername(userName: String): UserDetails {
        return User(PUBLIC_ACCESS_STUB_USER, "", emptyList())
    }
}
