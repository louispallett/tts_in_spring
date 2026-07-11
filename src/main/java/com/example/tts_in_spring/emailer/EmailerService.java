package com.example.tts_in_spring.emailer;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import org.springframework.stereotype.Service;

@Service
public class EmailerService {
    private final Resend resend;
	private final ResendProperties properties;

    public EmailerService(Resend resend, ResendProperties properties) {
		this.resend = resend;
		this.properties = properties;
    }

    public void sendEmail(String to, String subject, String body) throws ResendException {
		CreateEmailOptions params = CreateEmailOptions.builder()
			    .from(properties.from())
			    .to(to)
				.replyTo(properties.replyTo())
			    .subject(subject)
			    .html(body)
			    .build();

		resend.emails().send(params);
    }
}
