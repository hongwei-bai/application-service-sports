package com.hongwei.constants

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "appdata")
open class AppDataConfigurations {
    lateinit var nbaPath: String
}