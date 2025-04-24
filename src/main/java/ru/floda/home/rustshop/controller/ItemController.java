package ru.floda.home.rustshop.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.floda.home.rustshop.model.Item;
import ru.floda.home.rustshop.service.ItemService;

@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public String findAllItems(Model model) {
        model.addAttribute("items", itemService.findAll());
        return "item/index";
    }

    @GetMapping("/{id}")
    public String findItemById(@PathVariable long id, Model model) {
        Item item = itemService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Предмет не найден"));
        model.addAttribute("item", item);
        return "item/show-item";
    }
}
