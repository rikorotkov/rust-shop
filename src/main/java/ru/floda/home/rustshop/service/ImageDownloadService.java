package ru.floda.home.rustshop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.floda.home.rustshop.model.Item;
import ru.floda.home.rustshop.repository.ItemRepository;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageDownloadService {

    private final ItemRepository itemRepository;

    public void downloadImage() throws Exception {
        Path path = Paths.get("C:\\Users\\admin\\Documents\\Java\\rust-shop\\src\\main\\resources\\images");
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
        List<Item> items = itemRepository.findAll();

        int downloaded = 0;
        int failed = 0;

        for (Item item : items) {
            if (item.getIcon() != null && !item.getIcon().isEmpty()) {
                try {
                    String imageUrl = item.getIcon();
                    String fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
                    Path filePath = path.resolve(fileName);

                    downloadImage(imageUrl, filePath);
                    downloaded++;

                    log.info("Downloaded {} ({}/{})", fileName, downloaded, items.size());
                } catch (Exception e) {
                    failed++;
                    log.error("Failed to download {}: {}", item.getName(), e.getMessage());
                }
            }
        }
        log.info("Download completed. Success: {}, Failed: {}", downloaded, failed);
    }


    private void downloadImage(String imageUrl, Path targetPath) throws IOException {
        URL url = new URL(imageUrl);
        try (InputStream in = url.openStream()) {
            Files.copy(in, targetPath, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
