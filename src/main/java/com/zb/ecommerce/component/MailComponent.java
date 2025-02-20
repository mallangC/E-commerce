package com.zb.ecommerce.component;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MailComponent {

  private final JavaMailSender mailSender;

  @Async
  public void sendMail(String email, String title, String text) {

    MimeMessagePreparator message = new MimeMessagePreparator() {
      @Override
      public void prepare(MimeMessage mimeMessage) throws Exception {
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true , "UTF-8");
        messageHelper.setTo(email);
        messageHelper.setSubject(title);
        messageHelper.setText(text, true);
      }
    };
    try {
      mailSender.send(message);
    }catch (Exception e) {
      e.printStackTrace();
      System.out.println(e.getMessage());
    }
  }
}
