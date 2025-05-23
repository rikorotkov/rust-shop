package ru.floda.home.rustshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.floda.home.rustshop.model.Server;
import ru.floda.home.rustshop.model.ServerItem;
import ru.floda.home.rustshop.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findById(Long id);

    List<User> findAll();
}
