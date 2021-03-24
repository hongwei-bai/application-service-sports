package com.hongwei

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer
import org.springframework.scheduling.annotation.EnableScheduling

@EntityScan
@SpringBootApplication
@EnableScheduling
open class SportsApplication : SpringBootServletInitializer() {
    override fun configure(builder: SpringApplicationBuilder): SpringApplicationBuilder {
        return builder.sources(SportsApplication::class.java)
    }
}

fun main(args: Array<String>) {
    SpringApplication.run(SportsApplication::class.java, *args)
}