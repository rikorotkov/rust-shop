package ru.floda.home.rustshop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.floda.home.rustshop.service.DonationService;
import ru.floda.home.rustshop.service.UserService;

@Controller
@RequestMapping("/")
public class MainController {

    private final UserService userService;
    private final DonationService donationService;

    public MainController(UserService userService, DonationService donationService) {
        this.userService = userService;
        this.donationService = donationService;
    }
}
