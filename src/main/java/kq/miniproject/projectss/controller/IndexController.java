package kq.miniproject.projectss.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import kq.miniproject.projectss.model.Exchange;

@Controller
@RequestMapping(path = { "/", "/index" })
public class IndexController {

    @GetMapping(path = "/pick")
    public String getPick(Model model) {

        return "pick";
    }

    @GetMapping(path = "/santa")
    public String getSecretSanta(Model model) {

        model.addAttribute("exchange", new Exchange());
        return "exchange";
    }
}
