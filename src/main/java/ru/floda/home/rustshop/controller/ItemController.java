package ru.floda.home.rustshop.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.floda.home.rustshop.service.DonationService;
import ru.floda.home.rustshop.service.ItemService;
import ru.floda.home.rustshop.service.UserService;

@Controller
@RequestMapping("/item")
@RequiredArgsConstructor
public class ItemController {
    private final UserService userService;
    private final DonationService donationService;
    private final ItemService itemService;

    @GetMapping("/item")
    public String findAllItems(Model model) {
        model.addAttribute("items", itemService.findAll());
        return "index";
    }
}
