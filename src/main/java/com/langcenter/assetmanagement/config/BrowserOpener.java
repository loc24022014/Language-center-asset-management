package com.langcenter.assetmanagement.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class BrowserOpener implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) {
        try {
            String url = "http://localhost:8080/index.html";
            String os = System.getProperty("os.name").toLowerCase();
            
            if (os.contains("win")) {
                Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
            } else if (os.contains("mac")) {
                Runtime.getRuntime().exec("open " + url);
            } else {
                Runtime.getRuntime().exec("xdg-open " + url);
            }
            System.out.println("✅ Tự động mở trình duyệt tại: " + url);
        } catch (Exception e) {
            System.err.println("Không thể tự động mở trình duyệt: " + e.getMessage());
        }
    }
}
