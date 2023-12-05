package org.cynic.spring_stuff.controller.health;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health/status")
public class StatusController {

    @GetMapping
    public String index() {
        return "OK";
    }
}
