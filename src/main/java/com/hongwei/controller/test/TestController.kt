package com.hongwei.controller.test

import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
@CrossOrigin
class TestController {
    @Value("\${spring.jmx.default-domain}")
    private lateinit var applicationDomain: String

    @RequestMapping(path = ["/index.do", "/"])
    @ResponseBody
    fun index(): String {
        return "Hello My SpringBoot! - $applicationDomain"
    }

    @RequestMapping(path = ["/testAuthorise.do"])
    @ResponseBody
    fun testAuthorise(): String {
        return "Authorization test success! Your token is validated! - $applicationDomain"
    }
}