package de.propra.chicken.services.auditlog;

import org.springframework.context.ApplicationEvent;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

public class AuditEvent extends ApplicationEvent {
    private String message;

    public AuditEvent(Object source, String message) {
        super(source);
        this.message = formatLogMessage(message);
    }

    private String formatLogMessage(String message) {
        LocalDateTime convertedTimestamp = LocalDateTime.ofInstant(Instant.ofEpochMilli(this.getTimestamp()), TimeZone.getDefault().toZoneId());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
        return "[ " + convertedTimestamp.format(formatter) + " ] " + message + "\n";
    }

    public String getMessage() {
        return message;
    }
}
