package kq.miniproject.projectss.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import kq.miniproject.projectss.service.PickService;

@Controller
@RequestMapping(path = "/pick")
public class PickController {

    @Autowired
    PickService pkSvc;

    @PostMapping(path = "/random")
    public String getPax(@RequestBody MultiValueMap<String, String> payload, Model model) {

        try {
            Integer pax = Integer.parseInt(payload.getFirst("pax"));

            if (pax < 3 || pax > 20) {                
                model.addAttribute("hasErrors", true);
                model.addAttribute("error", "Number of gifts must be between 3 and 20");
                return "pick";
            }

            pkSvc.setNumbers(pax);
            model.addAttribute("num", pkSvc.getRandInt(null));

        } catch (Exception e) {

            if (pkSvc.checkLast()) {
                model.addAttribute("last", true);
            }

            model.addAttribute("num", pkSvc.getRandInt(Integer.parseInt(payload.getFirst("remove"))));
        }

        return "pick";
    }

    @PostMapping(path = "/new")
    public String getNew(Model model) {

        model.addAttribute("num", pkSvc.getRandInt(null));
        return "pick";
    }
}
