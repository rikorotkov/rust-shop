package ru.floda.home.rustshop.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.floda.home.rustshop.exceptions.UserNotFoundException;
import ru.floda.home.rustshop.model.Server;
import ru.floda.home.rustshop.model.ServerItem;
import ru.floda.home.rustshop.model.User;
import ru.floda.home.rustshop.repository.ItemRepository;
import ru.floda.home.rustshop.repository.ServerItemRepository;
import ru.floda.home.rustshop.repository.ServerRepository;
import ru.floda.home.rustshop.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServerService {

    private final UserRepository userRepository;
    private final ServerRepository serverRepository;
    private final ItemRepository itemRepository;
    private final ServerItemRepository serverItemRepository;

    public void addNewServer(Server server, long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        server.setUser(user);
        serverRepository.save(server);
        log.info("Server added: " + server);
    }

    public void addNewItem(ServerItem item, Long serverId) {
        Server server = serverRepository.findById(serverId).get();
        item.setServer(server);
        log.info("Item added: " + item);
    }

    @Transactional
    public List<Server> findAllServers(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("Пользователь не найден");
        }
        return serverRepository.findAllByUserId(userId);
    }
}
