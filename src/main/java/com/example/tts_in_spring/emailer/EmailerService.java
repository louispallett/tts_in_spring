package com.example.tts_in_spring.emailer;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailerService {
    private final Resend resend;
	private final ResendProperties properties;

    public EmailerService(Resend resend, ResendProperties properties) {
		this.resend = resend;
		this.properties = properties;
    }

    @Async
    public void sendEmail(String to, String subject, String body) {
		CreateEmailOptions params = CreateEmailOptions.builder()
			    .from(properties.from())
			    .to(to)
				.replyTo(properties.replyTo())
			    .subject(subject)
			    .html(body)
			    .build();

        try {
            resend.emails().send(params);
        } catch (ResendException e) {
            log.error("Failed to send notification email to user{}", to, e);
        }
    }
}
