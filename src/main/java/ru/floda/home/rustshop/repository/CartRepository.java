package ru.floda.home.rustshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.floda.home.rustshop.model.Cart;

public interface CartRepository extends JpaRepository<Cart, Long> {
}