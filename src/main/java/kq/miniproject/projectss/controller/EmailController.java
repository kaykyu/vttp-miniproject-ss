package kq.miniproject.projectss.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpSession;
import kq.miniproject.projectss.service.EmailService;
import kq.miniproject.projectss.service.SantaService;

@RestController
@RequestMapping(path = "/email")
public class EmailController {

    @Autowired
    SantaService stSvc;

    @Autowired
    EmailService mailSvc;
    
    @GetMapping(path = "/send")
    public ModelAndView generateSanta(HttpSession session) {

        ModelAndView mav = new ModelAndView("thankyou");
        String id = session.getAttribute("id").toString();

        if (stSvc.groupSize(id) < 3 || stSvc.groupSize(id) > 20) {
            mav.setViewName("confirm");
            mav.addObject("people", stSvc.getPeople(id));
            mav.addObject("exchange", stSvc.getExchange(id));
            mav.addObject("groupError", "Number of people must be between 3 and 20");
            return mav;
        }

        stSvc.generateSanta(id);
        
        try {
            mailSvc.sendGroupMail(id);
            mailSvc.sendEmail(mailSvc.generateEmails(id));

        } catch (Exception e) {
            mav.setViewName("emailError");
            stSvc.removeGroup(id);
            mav.addObject("error", mailSvc.generateError(e.getMessage()));
            return mav;
        }
        
        
        return mav;
    }
}
