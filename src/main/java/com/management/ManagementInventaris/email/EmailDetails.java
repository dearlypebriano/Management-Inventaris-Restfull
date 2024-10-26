package com.management.ManagementInventaris.email;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmailDetails {
    private String recipient;
    private String msgBody;
    private String subject;
    private String attachment;

    public EmailDetails(String recipient, String subject, String msgBody) {
        this.recipient = recipient;
        this.subject = subject;
        this.msgBody = msgBody;
    }
}