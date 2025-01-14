package com.traveltrove.betraveltrove.presentation.email;

import com.traveltrove.betraveltrove.business.email.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class EmailController {

    @Autowired
    private EmailService emailService;

    @GetMapping("/send-email")
    public Mono<String> sendTestEmail(@RequestParam String to,
                                      @RequestParam String subject,
                                      @RequestParam String body) {
        return Mono.fromRunnable(() -> emailService.sendEmail(to, subject, body))
                .then(Mono.just("Email sent successfully to " + to))
                .onErrorResume(e -> Mono.just("Error sending email: " + e.getMessage()));
    }
}

