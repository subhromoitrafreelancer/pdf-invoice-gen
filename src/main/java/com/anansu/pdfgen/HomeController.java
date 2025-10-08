package com.anansu.pdfgen;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    /**
     * Serve the invoice generation form as the default landing page
     *
     * @return the index view
     */
    @GetMapping("/")
    public String index() {
        return "index";
    }
}
