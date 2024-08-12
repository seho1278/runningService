package com.example.runningservice.client;

import com.example.runningservice.config.FeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "mailgun", url = "https://api.mailgun.net/v3", configuration = FeignConfiguration.class)
@Component
public interface MailgunClient {

    @PostMapping("/${mailgun.domain}/messages")
    ResponseEntity<String> sendEmail(@RequestParam("from") String from,
        @RequestParam("to") String to, @RequestParam("subject") String subject,
        @RequestParam("text") String text);
}
