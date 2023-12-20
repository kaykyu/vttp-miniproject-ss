package kq.miniproject.projectss.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import kq.miniproject.projectss.model.Person;
import kq.miniproject.projectss.model.Product;
import kq.miniproject.projectss.service.ShowService;
import kq.miniproject.projectss.service.WishlistService;

@RestController
@RequestMapping("/wishlist")
public class WishlistController {

    @Autowired
    ShowService shSvc;

    @Autowired
    WishlistService wlSvc;

    @GetMapping(path = "/{groupId}/{personId}")
    public ModelAndView getWishlist(@PathVariable("groupId") String groupId,
            @PathVariable("personId") String personId) {

        ModelAndView mav = new ModelAndView("wishlist");
        Person person = shSvc.findPerson(groupId, personId);

        if (person == null) {
            mav.setViewName("error");
            return mav;
        }

        mav.addObject("santa", person.getName());
        mav.addObject("groupId", groupId);
        mav.addObject("personId", personId);
        mav.addObject("wishlist", wlSvc.accessWishlist(person, null));

        return mav;
    }

    @GetMapping(path = "/{groupId}/{personId}/edit")
    public ModelAndView getEdit(@PathVariable("groupId") String groupId, @PathVariable("personId") String personId) {

        ModelAndView mav = new ModelAndView("wishlist");
        Person person = shSvc.findPerson(groupId, personId);

        if (person == null) {
            mav.setViewName("error");
            return mav;
        }

        mav.addObject("santa", person.getName());
        mav.addObject("groupId", groupId);
        mav.addObject("personId", personId);
        mav.addObject("wishlist", wlSvc.accessWishlist(person, null));
        mav.addObject("edit", true);

        return mav;
    }

    @PostMapping(path = "/{groupId}/{personId}")
    public ModelAndView postWishlist(@PathVariable("groupId") String groupId, @PathVariable("personId") String personId,
            @RequestBody MultiValueMap<String, String> payload) {

        ModelAndView mav = new ModelAndView("wishlist");
        Person person = shSvc.findPerson(groupId, personId);
        String wish = payload.getFirst("wish");

        mav.addObject("santa", person.getName());
        mav.addObject("groupId", groupId);
        mav.addObject("personId", personId);
        mav.addObject("edit", true);

        if (wish.length() < 0) {

            mav.addObject("wishlist", wlSvc.accessWishlist(person, null));
            return mav;
        }

        mav.addObject("wishlist", wlSvc.accessWishlist(shSvc.findPerson(groupId, personId), wish));

        return mav;
    }

    @PostMapping(path = "/{groupId}/{personId}/delete")
    public ModelAndView deleteWishlist(@PathVariable("groupId") String groupId,
            @PathVariable("personId") String personId,
            @RequestBody MultiValueMap<String, String> payload) {

        Person person = shSvc.findPerson(groupId, personId);
        wlSvc.removeWish(person, payload.getFirst("wish"));
        ModelAndView mav = new ModelAndView("wishlist");

        mav.addObject("santa", person.getName());
        mav.addObject("groupId", groupId);
        mav.addObject("personId", personId);
        mav.addObject("wishlist", wlSvc.accessWishlist(person, null));
        mav.addObject("edit", true);

        return mav;
    }

    @GetMapping(path = "/{groupId}/{personId}/search")
    public ModelAndView getSearch(@PathVariable("groupId") String groupId, @PathVariable("personId") String personId,
            @RequestParam("q") String search) {

        ModelAndView mav = new ModelAndView("wishlist");
        Person person = shSvc.findPerson(groupId, personId);

        mav.addObject("santa", person.getName());
        mav.addObject("groupId", groupId);
        mav.addObject("personId", personId);
        mav.addObject("wishlist", wlSvc.accessWishlist(person, null));
        mav.addObject("edit", true);

        List<Product> products = wlSvc.getProducts(search);
        if (products == null) {
            mav.addObject("searchError", "No products found. Please try again.");
            return mav;
        }

        mav.addObject("products", products);
        return mav;
    }

    @PostMapping(path = "/{groupId}/{personId}/search/add")
    public ModelAndView postSearch(@PathVariable("groupId") String groupId, @PathVariable("personId") String personId,
            @RequestBody MultiValueMap<String, String> payload) {

        ModelAndView mav = new ModelAndView("wishlist");
        Person person = shSvc.findPerson(groupId, personId);

        mav.addObject("santa", person.getName());
        mav.addObject("groupId", groupId);
        mav.addObject("personId", personId);
        mav.addObject("edit", true);

        Product product = new Product(
                payload.getFirst("title"),
                payload.getFirst("itemUrl"),
                payload.getFirst("image"));

        mav.addObject("wishlist", wlSvc.accessWishlist(person, product));

        return mav;
    }

}
