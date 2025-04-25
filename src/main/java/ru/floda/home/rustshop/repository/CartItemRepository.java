package ru.floda.home.rustshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.floda.home.rustshop.model.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
}