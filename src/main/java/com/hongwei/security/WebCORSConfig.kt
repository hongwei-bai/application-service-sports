package com.hongwei.security

import com.hongwei.constants.SecurityConfigurations
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders.CONTENT_TYPE
import org.springframework.http.HttpMethod
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
@EnableWebMvc
open class WebCORSConfig : WebMvcConfigurer {
    @Autowired
    private lateinit var securityConfigurations: SecurityConfigurations

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
                .allowedMethods(
                        HttpMethod.GET.name,
                        HttpMethod.POST.name,
                        HttpMethod.OPTIONS.name,
                        HttpMethod.PUT.name
                )
                .allowedHeaders(CONTENT_TYPE, securityConfigurations.authorizationHeader)
                .allowCredentials(true)
                .allowedOrigins(securityConfigurations.corsAllowDomain)
    }
}