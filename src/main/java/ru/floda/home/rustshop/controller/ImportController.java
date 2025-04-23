package ru.floda.home.rustshop.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.floda.home.rustshop.service.ImportService;

@RestController
public class ImportController {

    private final ImportService importService;

    public ImportController(ImportService importService) {
        this.importService = importService;
    }

    @PostMapping("/import")
    public ResponseEntity<String> importData() {
        try {
            String filePath = "/resources/rust_items.json";
            importService.importItemsFromFile(filePath);
            return ResponseEntity.ok("Успешно импортировано");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Ошибка импорта: " + e.getMessage());
        }
    }
}
