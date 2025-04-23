package ru.floda.home.rustshop.service;

import org.springframework.stereotype.Service;
import ru.floda.home.rustshop.exceptions.UserNotFoundException;
import ru.floda.home.rustshop.model.User;
import ru.floda.home.rustshop.repository.UserRepository;

import java.util.List;

@Service
public class UserService {

    private final UserRepository UserRepository;
    private final UserRepository userRepository;

    public UserService(ru.floda.home.rustshop.repository.UserRepository userRepository, ru.floda.home.rustshop.repository.UserRepository userRepository1) {
        UserRepository = userRepository;
        this.userRepository = userRepository1;
    }

    public List<User> findAll() {
        return UserRepository.findAll();
    }

    public User findById(int id) {
        return userRepository.findUserById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден id: " + id));
    }
}
