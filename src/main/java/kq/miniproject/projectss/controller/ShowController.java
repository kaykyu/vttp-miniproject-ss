package kq.miniproject.projectss.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import kq.miniproject.projectss.model.Person;
import kq.miniproject.projectss.service.ShowService;
import kq.miniproject.projectss.service.WishlistService;

@Controller
@RequestMapping(path = "/show")
public class ShowController {

    @Autowired
    ShowService shSvc;

    @Autowired
    WishlistService wlSvc;

    @GetMapping(path = "{groupId}/{personId}")
    public ModelAndView getUser(@PathVariable("groupId") String groupId, @PathVariable("personId") String personId) {

        ModelAndView mav = new ModelAndView("show");
        Person person = shSvc.findPerson(groupId, personId);

        if (person == null) {
            mav.setViewName("error");
            return mav;
        }

        Person santee = shSvc.findSantee(groupId, person);

        mav.addObject("groupId", groupId);
        mav.addObject("personId", personId);
        mav.addObject("santa", person.getName());
        mav.addObject("santee", santee.getName());

        return mav;
    }

    @PostMapping(path = "{groupId}/{personId}/reveal")
    public ModelAndView postReveal(@PathVariable("groupId") String groupId, @PathVariable("personId") String personId) {

        ModelAndView mav = new ModelAndView("show");
        Person person = shSvc.findPerson(groupId, personId);
        Person santee = shSvc.findSantee(groupId, person);

        mav.addObject("groupId", groupId);
        mav.addObject("personId", personId);
        mav.addObject("santa", person.getName());
        mav.addObject("santee", santee.getName());
        mav.addObject("reveal", true);
        mav.addObject("santeeList", wlSvc.accessWishlist(santee, null));

        return mav;
    }
}
