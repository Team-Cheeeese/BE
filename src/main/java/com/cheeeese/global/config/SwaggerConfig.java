package com.cheeeese.global.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                title = "Cheeeese API 명세서",
                description = "Cheeeese API 명세서",
                version = "v1"
        ),
        servers = @Server(
                url = "/",
                description = "Default Server URL"
        )
)
public class SwaggerConfig {
}
