package fi.haagahelia.serverprog.todomanager.domain.Repository;

import fi.haagahelia.serverprog.todomanager.domain.Model.EmailMessage;

public interface EmailService {

    String sendSimpleMail(EmailMessage details);
}
