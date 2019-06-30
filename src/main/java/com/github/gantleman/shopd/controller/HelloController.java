
package com.github.gantleman.shopd.controller;
 
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
 
@RestController
public class HelloController {
 
    @RequestMapping("/")
    String home() {
        return "redirect:/main";
    }
}