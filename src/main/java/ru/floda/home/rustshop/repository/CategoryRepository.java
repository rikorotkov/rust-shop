package ru.floda.home.rustshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.floda.home.rustshop.model.Category;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByName(String name);

    Optional<Category> findById(Long id);

    List<Category> findAll();

    List<Category> findByNameIn(Collection<String> categories);
}
