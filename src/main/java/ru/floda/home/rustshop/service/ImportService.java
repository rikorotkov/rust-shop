package ru.floda.home.rustshop.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import ru.floda.home.rustshop.dto.ItemDtoImport;
import ru.floda.home.rustshop.model.Category;
import ru.floda.home.rustshop.model.Item;
import ru.floda.home.rustshop.repository.CategoryRepository;
import ru.floda.home.rustshop.repository.ItemRepository;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ImportService {

    private final EntityManager em;
    private final ObjectMapper objectMapper;
    private final CategoryRepository categoryRepo;
    private final ItemRepository itemRepo;

    private static final int BATCH_SIZE = 50; // Оптимальный размер батча

    public ImportService(EntityManager em, ObjectMapper objectMapper, CategoryRepository categoryRepo, ItemRepository itemRepo) {
        this.em = em;
        this.objectMapper = objectMapper;
        this.categoryRepo = categoryRepo;
        this.itemRepo = itemRepo;
    }

    @Transactional
    public void importItemsFromFile(String filePath) throws IOException {
        // Чтение и парсинг JSON
        ItemDtoImport[] itemsArray = objectMapper.readValue(new File(filePath), ItemDtoImport[].class);
        List<ItemDtoImport> items = Arrays.asList(itemsArray);

        // Получаем все уникальные категории из JSON
        Set<String> categoryNames = items.stream()
                .map(ItemDtoImport::getCategory)
                .collect(Collectors.toSet());

        // Получаем существующие категории из БД
        Map<String, Category> existingCategories = categoryRepo.findByNameIn(categoryNames)
                .stream()
                .collect(Collectors.toMap(Category::getName, c -> c));

        // Создаем новые категории
        Set<Category> newCategories = categoryNames.stream()
                .filter(name -> !existingCategories.containsKey(name))
                .map(name -> {
                    Category cat = new Category();
                    cat.setName(name);
                    return cat;
                })
                .collect(Collectors.toSet());

        // Сохраняем новые категории батчами
        if (!newCategories.isEmpty()) {
            int count = 0;
            for (Category cat : newCategories) {
                em.persist(cat);
                if (++count % BATCH_SIZE == 0) {
                    em.flush();
                    em.clear();
                }
            }
            em.flush();
            em.clear();

            // Добавляем новые категории в existingCategories
            categoryRepo.findByNameIn(newCategories.stream()
                            .map(Category::getName)
                            .collect(Collectors.toSet()))
                    .forEach(c -> existingCategories.put(c.getName(), c));
        }

        // Подготавливаем и сохраняем Items
        List<Item> entitiesToSave = items.stream()
                .map(itemDto -> {
                    Item item = new Item();
                    item.setShortName(itemDto.getShortName());
                    item.setName(itemDto.getName());
                    item.setCategory(existingCategories.get(itemDto.getCategory()));
                    item.setGameId(Long.valueOf(itemDto.getGameId()));
                    item.setIcon(itemDto.getIcon());
                    return item;
                }).collect(Collectors.toList());
        // Сохраняем Items батчами
        for (int i = 0; i < entitiesToSave.size(); i++) {
            em.persist(entitiesToSave.get(i));
            if (i % BATCH_SIZE == 0 && i > 0) {
                em.flush();
                em.clear();
            }
        }
        em.flush();
    }
}
