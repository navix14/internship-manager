package de.propra.chicken.services.auditlog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class AuditLogPublisher {

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    public void publishAuditEvent(final String message) {
        AuditEvent customSpringEvent = new AuditEvent(this, message);
        applicationEventPublisher.publishEvent(customSpringEvent);
    }
}
