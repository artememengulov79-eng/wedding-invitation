package com.wedding.invitation;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${wedding.notification.email}")
    private String notificationEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendGuestNotification(Guest guest) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(notificationEmail);
        message.setTo(notificationEmail);
        message.setSubject("🎉 Новая анкета на свадьбу!");

        String alcoholText = switch (guest.getAlcoholPreference()) {
            case "wine" -> "🍷 Вино";
            case "champagne" -> "🥂 Шампанское";
            case "whiskey" -> "🥃 Крепкие напитки";
            case "none" -> "🥤 Безалкогольное";
            default -> "Не указано";
        };

        String dateTime = LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
        );

        message.setText("""
            Новая анкета на свадьбу Артёма и Виктории!
            
            👤 Имя: %s
            👥 Количество гостей: %d
            🍾 Алкоголь: %s
            ⏰ Время заполнения: %s
            
            ---
            Это автоматическое уведомление.
            """.formatted(
                guest.getName(),
                guest.getGuestCount(),
                alcoholText,
                dateTime
        ));

        mailSender.send(message);
    }
}