package com.truong.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.truong.entities.User;
import com.truong.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping
public class HomeController {

    private final UserService userService;

    public HomeController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login() {
        return "login"; // Trả về trang login
    }

    @PostMapping("/api/auth/login")
    public ModelAndView login(@RequestParam("userName") String userName,
                              @RequestParam("password") String password,
                              HttpSession session) {
        boolean isAuthenticated = userService.login(userName, password);
        if (isAuthenticated) {
//            User user = userService.findByUserName(userName);
            
//            session.setAttribute("fullName", user.getFullName());

            return new ModelAndView("redirect:/home");
        }
        return new ModelAndView("login", "error", "Invalid credentials");
    }



    @GetMapping("/home")
    public ModelAndView home(HttpSession session) {
        String fullName = (String) session.getAttribute("fullName");

        if (fullName == null) {
            fullName = "Guest";
        }

        ModelAndView modelAndView = new ModelAndView("home");
        modelAndView.addObject("fullName", fullName);
        return modelAndView;
    }

//    @GetMapping("/departmentlist")

}
