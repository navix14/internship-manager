package de.propra.chicken.services.auditlog;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;

@Component
public class AuditEventListener implements ApplicationListener<AuditEvent> {

    @Override
    public void onApplicationEvent(AuditEvent event)  {
        File auditLog = new File("./log/log.txt");

        if (!auditLog.getParentFile().mkdirs())
            return;

        FileOutputStream oFile = null;

        try {
            if (auditLog.createNewFile()) {
                oFile = new FileOutputStream(auditLog, true);
                oFile.write(event.getMessage().getBytes(StandardCharsets.UTF_8));
                oFile.close();
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
