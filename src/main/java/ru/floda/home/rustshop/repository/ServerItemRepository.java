package ru.floda.home.rustshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.floda.home.rustshop.model.ServerItem;

public interface ServerItemRepository extends JpaRepository<ServerItem, Long> {
}