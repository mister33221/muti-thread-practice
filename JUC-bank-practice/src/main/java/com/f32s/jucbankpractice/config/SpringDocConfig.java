package com.F32S.JUCbankpractice.config;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(
                title = "A Bank system for muti threads practice",
                version = "0.0",
                description = "版本: \n\n " +
                        "spring boot : 3.1.5\n\n" +
                        "springdoc-openapi-starter-webmvc-ui : 2.1.0\n\n" +
                        "Java : 17\n\n" +
                        "主要會練習使用多執行緒的技巧來完成銀行轉帳的各種功能。"
        )
)
@Configuration
public class SpringDocConfig {
}
