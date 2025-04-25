package ru.floda.home.rustshop.controller.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.floda.home.rustshop.service.ImageDownloadService;
import ru.floda.home.rustshop.service.ImportService;

@RestController
@RequestMapping("api/admin-service-helper")
@RequiredArgsConstructor
public class DataRustServiceController {

    private final ImportService importService;
    private final ImageDownloadService imageDownloadService;

    @PostMapping("/importallfromjsonfile")
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

    @PostMapping("/downloadimages")
    public ResponseEntity<String> downloadImages() {
        try {
            imageDownloadService.downloadImage();
            return ResponseEntity.ok("Успешно загружено");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Ошибка загрузки " + e.getMessage());
        }
    }
}

