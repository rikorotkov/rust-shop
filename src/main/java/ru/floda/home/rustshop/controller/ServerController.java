package ru.floda.home.rustshop.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.floda.home.rustshop.service.ServerService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user/{userId}/servers")
public class ServerController {

    private final ServerService serverService;

    @GetMapping
    public String getServers(@PathVariable long userId, Model model) {
        model.addAttribute("servers", serverService.findAllServers(userId));
        return "server/index";
    }
}
