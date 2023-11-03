package com.kai.ticketsystemformutithreadspractice.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(
                title = "A ticket system for muti threads practice",
                version = "0.0",
                description = "版本: \n\n " +
                        "spring boot : 3.1.5\n\n" +
                        "springdoc-openapi-starter-webmvc-ui : 2.1.0\n\n" +
                        "Java : 17\n\n" +
                        "主要會練習使用多執行緒的技巧，所以會有一些不合理的地方，請見諒。",
//                license = @License(name = "No", url = "License url"),
                contact = @Contact(url = "https://mister33221.github.io/index.html", name = "Github page", email = "mister33221@gmail.com")
        )
)
@Configuration
public class SpringDicConfig {
}
