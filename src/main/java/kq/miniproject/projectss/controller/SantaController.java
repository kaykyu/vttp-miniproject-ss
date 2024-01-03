package kq.miniproject.projectss.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import kq.miniproject.projectss.model.Person;
import kq.miniproject.projectss.model.Exchange;
import kq.miniproject.projectss.service.SantaService;

@Controller
@RequestMapping(path = "/santa")
public class SantaController {

    @Autowired
    SantaService stSvc;

    @GetMapping(path = "/addNew")
    public ModelAndView postNum(HttpSession session) {

        ModelAndView mav = new ModelAndView("addPerson");

        mav.addObject("person", new Person());
        mav.addObject("i", stSvc.groupSize(session.getAttribute("id").toString()) + 1);
        return mav;
    }

    @PostMapping(path = "/generate")
    public ModelAndView postExchange(@Valid @ModelAttribute("exchange") Exchange exchange, BindingResult binding,
            HttpSession session) {

        ModelAndView mav = new ModelAndView("santa");
        if (binding.hasErrors()) {
            mav.setViewName("exchange");
            return mav;
        }
        if (session.getAttribute("id") == null) {
            session.setAttribute("id", stSvc.addExchange(null, exchange));
        } else {
            stSvc.addExchange(session.getAttribute("id").toString(), exchange);
        }

        mav.setViewName("confirm");
        mav.addObject("people", stSvc.getPeople(session.getAttribute("id").toString()));
        mav.addObject("exchange", exchange);
        return mav;
    }

    @PostMapping(path = "/generate/add")
    public ModelAndView postNames(@Valid @ModelAttribute("person") Person person, BindingResult binding,
            HttpSession session) {

        ModelAndView mav = new ModelAndView("confirm");

        if (binding.hasErrors()) {
            mav.setViewName("addPerson");
            mav.addObject("i", stSvc.groupSize(session.getAttribute("id").toString()) + 1);
            return mav;
        }

        String id = session.getAttribute("id").toString();
        stSvc.addPerson(id, person);
        mav.addObject("people", stSvc.getPeople(id));
        mav.addObject("exchange", stSvc.getExchange(id));

        return mav;
    }

    @PostMapping(path = "/generate/edit")
    public ModelAndView postEdit(@RequestBody MultiValueMap<String, String> payload, HttpSession session) {

        ModelAndView mav = new ModelAndView("editPerson");
        Integer index = Integer.parseInt(payload.getFirst("index"));
        mav.addObject("person", stSvc.findPerson(session.getAttribute("id").toString(), index));
        mav.addObject("i", index + 1);
        return mav;
    }

    @PostMapping(path = "/generate/update")
    public ModelAndView postUpdate(@Valid @ModelAttribute Person person, BindingResult binding, HttpSession session,
            @RequestBody MultiValueMap<String, String> payload) {

        ModelAndView mav = new ModelAndView("confirm");

        if (binding.hasErrors()) {
            mav.setViewName("editPerson");
            Integer index = Integer.parseInt(payload.getFirst("index"));
            mav.addObject("i", index + 1);
            return mav;
        }

        String id = session.getAttribute("id").toString();
        stSvc.updatePerson(id, person, payload.getFirst("index"));

        mav.addObject("people", stSvc.getPeople(id));
        mav.addObject("exchange", stSvc.getExchange(id));
        return mav;
    }

    @PostMapping(path = "generate/remove")
    public ModelAndView postRemove(HttpSession session, @RequestBody MultiValueMap<String, String> payload) {

        ModelAndView mav = new ModelAndView("confirm");
        String id = session.getAttribute("id").toString();
        stSvc.removePerson(id, payload.getFirst("index"));

        mav.addObject("people", stSvc.getPeople(id));
        mav.addObject("exchange", stSvc.getExchange(id));
        return mav;

    }
}
