package com.traveltrove.betraveltrove.business.email;

public interface EmailService {
    void sendEmail(String to, String subject, String text);
}
