package com.langcenter.assetmanagement;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
    info = @Info(
        title = "Language Center Asset Management API",
        version = "1.0",
        description = "API quản lý tài sản trung tâm ngoại ngữ – Sprint 1 MVP",
        contact = @Contact(name = "Team N16", email = "team@langcenter.edu.vn")
    )
)
public class AssetManagementApplication {
    public static void main(String[] args) {
        SpringApplication.run(AssetManagementApplication.class, args);
    }
}
