package com.mg.lpcalc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HtmlPageController {
    @GetMapping("/simplex")
    public String specificPath() {
        return "forward:/html/simplex-method.html";
    }

    @GetMapping("/graphical")
    public String specificPath2() {
        return "forward:/html/graph-method.html";
    }
}
