package ru.floda.home.rustshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.floda.home.rustshop.model.Server;
import ru.floda.home.rustshop.model.ServerItem;

import java.util.List;
import java.util.Optional;

public interface ServerRepository extends JpaRepository<Server, Long> {

    Optional<Server> findById(Long id);

    List<Server> findAllByUserId(Long userId);

}