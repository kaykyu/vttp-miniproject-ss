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

import com.resend.Resend;
import com.resend.services.emails.model.SendEmailRequest;
import com.resend.services.emails.model.SendEmailResponse;

import kq.miniproject.projectss.model.Email;
import kq.miniproject.projectss.model.Person;
import kq.miniproject.projectss.repository.SantaRepo;

@Service
public class EmailService {

    @Autowired
    SantaRepo repo;

    @Value("${resend.email.apiKey}")
    String apiKey;

    @Value("${webapp.host.url}")
    String hostUrl;

    private final static String fromEmail = "Secret Santa <noreply@resend.dev>";
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

        String message = generateGroupMail(id);
        List<Person> people = repo.getGroup(id);
        String[] toMailAdds = new String[people.size()];

        for (int i = 0; i < people.size(); i++) {
            toMailAdds[i] = people.get(i).getEmail();
        }

        Resend resend = new Resend(apiKey);
        SendEmailRequest sendEmailRequest = SendEmailRequest.builder()
                .from(fromEmail)
                .to(toMailAdds)
                .subject("Christmas Gift Exchange")
                .html(message)
                .build();

        SendEmailResponse data = resend.emails().send(sendEmailRequest);
        System.out.println(data.getId());
    }

    public void sendEmail(List<Email> emails) throws Exception {

        Resend resend = new Resend(apiKey);

        for (Email mail : emails) {

            SendEmailRequest sendEmailRequest = SendEmailRequest.builder()
                    .from(fromEmail)
                    .to(mail.getSendTo().getEmail())
                    .subject("Christmas Gift Exchange - Secret Santa")
                    .html(mail.getMessage())
                    .build();

            SendEmailResponse data = resend.emails().send(sendEmailRequest);
            System.out.println(data.getId());
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

                emails.add(new Email(santa.get(person), message));

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

        String budget = "$%.2f".formatted(repo.getExchange(id).getBudget());
        if (budget.equals("$0.00")) {
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

    public String generateError(String message) {
        return repo.saveError(message);
    }
}
