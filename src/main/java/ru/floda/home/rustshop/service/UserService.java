package ru.floda.home.rustshop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.floda.home.rustshop.exceptions.UserNotFoundException;
import ru.floda.home.rustshop.model.User;
import ru.floda.home.rustshop.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository UserRepository;
    private final UserRepository userRepository;

    public List<User> findAll() {
        return UserRepository.findAll();
    }

    public User findById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден id: " + id));
    }
}
