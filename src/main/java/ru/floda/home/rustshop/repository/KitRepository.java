package ru.floda.home.rustshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.floda.home.rustshop.model.Kit;

public interface KitRepository extends JpaRepository<Kit, Long> {
}