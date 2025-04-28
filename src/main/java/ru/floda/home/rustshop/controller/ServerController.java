    package ru.floda.home.rustshop.controller;

    import lombok.RequiredArgsConstructor;
    import org.springframework.stereotype.Controller;
    import org.springframework.ui.Model;
    import org.springframework.web.bind.annotation.*;
    import ru.floda.home.rustshop.model.Server;
    import ru.floda.home.rustshop.service.ServerService;

    @Controller
    @RequiredArgsConstructor
    @RequestMapping("/user/{userId}/servers")
    public class ServerController {

        private final ServerService serverService;

        @GetMapping
        public String getServers(@PathVariable long userId, Model model) {
            model.addAttribute("servers", serverService.findAllServers(userId));
            model.addAttribute("userId", userId);
            return "server/index";
        }

        @GetMapping("/new")
        public String showFormNewServer(@PathVariable long userId, Model model) {
            model.addAttribute("server", new Server());
            model.addAttribute("userId", userId);
            return "server/new";
        }

        @PostMapping("/new")
        public String processNewServer(@ModelAttribute("server") Server server, @ModelAttribute("userId") long userId) {
            serverService.addNewServer(server, userId);
            return "redirect:/user/" + userId + "/servers";
        }
    }
