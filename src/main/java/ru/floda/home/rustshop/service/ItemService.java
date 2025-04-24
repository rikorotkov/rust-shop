package ru.floda.home.rustshop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.floda.home.rustshop.model.Item;
import ru.floda.home.rustshop.repository.CategoryRepository;
import ru.floda.home.rustshop.repository.ItemRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final CategoryRepository categoryRepository;

    public List<Item> findAll() {
        return itemRepository.findAll();
    }

    public Optional<Item> findById(Long id) {
        return itemRepository.findById(id);
    }
}
