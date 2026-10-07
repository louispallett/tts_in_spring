package com.example.tts_in_spring.emailer;

import com.example.tts_in_spring.emailer.dto.EmailRequest;
import com.example.tts_in_spring.emailer.dto.GenericHostEmail;
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

    @Async
    public void sendGenericHostEmail(GenericHostEmail info, EmailRequest request) {
        String html = """
                <p>Dear <b>%s</b>,</p>
                %s
                <p>This email was sent out by %s regarding the tournament <b>%s</b>.</p>
                <p>Please do not respond to this email.</p>
                <p><i>Tennis Tournament Creator</i> by <b>Louis Pallett</p> is licensed under the GNU Affero General Public License.</p>
                """
                .formatted(
                        info.firstName(),
                        info.from(),
                        info.tournamentName(),
                        request.text()
                );

        sendEmail(
                info.to(),
                "TTS: " + request.subject(),
                html
        );
    }
}
