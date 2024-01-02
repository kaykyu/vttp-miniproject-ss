package kq.miniproject.projectss.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Personalization;

import kq.miniproject.projectss.model.Email;
import kq.miniproject.projectss.model.Person;
import kq.miniproject.projectss.repository.SantaRepo;

@Service
public class EmailService {

    @Autowired
    SantaRepo repo;

    @Value("${webapp.host.url}")
    String hostUrl;

    @Value("${sendgrid.email.apiKey}")
    String sendgridKey;

    private final static String fromEmail = "kaykyu8080@gmail.com";
    private final static List<String> groupTemplateVariables = new ArrayList<>(List.of("__date__", "__budget__"));
    private final static List<String> emailTemplateVariables = new ArrayList<>(
            List.of("__name__", "__santee__", "__link__"));

    private File loadEmailTemplate() {

        File f = new File("/app/email/emailTemplate.txt");
        return f;

    }

    private File loadGroupTemplate() {

        File f = new File("/app/email/emailGroupTemplate.txt");
        return f;
    }

    public void sendGroupMail(String id) throws Exception {

        Mail mail = new Mail();
        mail.setFrom(new com.sendgrid.helpers.mail.objects.Email(fromEmail, "Secret Santa"));
        mail.setSubject("Christmas Gift Exchange");

        List<Person> people = repo.getGroup(id);
        Personalization toMailAdds = new Personalization();
        for (int i = 0; i < people.size(); i++) {
            toMailAdds.addTo(new com.sendgrid.helpers.mail.objects.Email(people.get(i).getEmail()));
        }
        mail.addPersonalization(toMailAdds);
        mail.addContent(new Content("text/html", generateGroupMail(id)));

        SendGrid sg = new SendGrid(sendgridKey);
        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());
        Response response = sg.api(request);

        switch (response.getStatusCode()) {
            case 202:
                break;
            default:
                throw new Exception("SendGrid Email API error");
        }
    }

    public void sendEmail(List<Email> emails) throws Exception {

        for (Email email : emails) {

            com.sendgrid.helpers.mail.objects.Email from = new com.sendgrid.helpers.mail.objects.Email(fromEmail,
                    "Secret Santa");
            com.sendgrid.helpers.mail.objects.Email to = new com.sendgrid.helpers.mail.objects.Email(
                    email.getSendTo().getEmail());
            Content content = new Content("text/html", email.getMessage());
            Mail mail = new Mail(from, "Christmas Gift Exchange - Secret Santa", to, content);

            SendGrid sg = new SendGrid(sendgridKey);
            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);

            switch (response.getStatusCode()) {
                case 202:
                    break;
                default:
                    throw new Exception("SendGrid Email API error");
            }
        }
    }

    public List<Email> generateEmails(String id) {

        List<Email> emails = new ArrayList<>();
        Map<Person, Person> santa = repo.getSanta(id);

        for (Person person : santa.keySet()) {

            String link = "%s/show/%s/%s".formatted(hostUrl, id, person.getUniqueId());
            String[] replace = { person.getName(), santa.get(person).getName(), link };

            try (FileReader fr = new FileReader(loadEmailTemplate())) {

                BufferedReader br = new BufferedReader(fr);
                String message = br.lines()
                        .map(line -> {
                            while (line.contains("_")) {
                                for (String var : emailTemplateVariables) {
                                    line = line.replace(var, replace[emailTemplateVariables.indexOf(var)]);
                                }
                            }
                            return line;
                        })
                        .collect(Collectors.joining());

                emails.add(new Email(person, message));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return emails;
    }

    public String generateGroupMail(String id) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
        Date date = repo.getExchange(id).getDate();
        String dateString = dateFormat.format(date);

        String budget = "a $%.2f".formatted(repo.getExchange(id).getBudget());
        if (budget.equals("a $0.00")) {
            budget = "no";
        }

        String[] replace = { dateString, budget };
        String result = "";

        try (FileReader fr = new FileReader(loadGroupTemplate())) {

            BufferedReader br = new BufferedReader(fr);
            result = br.lines()
                    .map(line -> {
                        while (line.contains("_")) {
                            for (String var : groupTemplateVariables) {
                                line = line.replace(var, replace[groupTemplateVariables.indexOf(var)]);
                            }
                        }
                        return line;
                    })
                    .collect(Collectors.joining());

        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }
}
