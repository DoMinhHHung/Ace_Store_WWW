package iuh.fit.se.ace_store_www.service;

public interface EmailService {
    void sendSimpleMessage(String to, String subject, String text);
}
