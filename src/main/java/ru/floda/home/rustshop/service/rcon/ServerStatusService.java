package ru.floda.home.rustshop.service.rcon;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.core.config.Scheduled;
import org.springframework.stereotype.Service;
import ru.floda.home.rustshop.model.Server;
import ru.floda.home.rustshop.model.ServerStatus;
import ru.floda.home.rustshop.repository.ServerRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServerStatusService {

    private final ServerRepository serverRepository;
    private final RconService rconService;

    @Scheduled(fixedRate = 30000)
    @Transactional
    public void updateServerStatus() {
        List<Server> servers = serverRepository.findAll();
        for (Server server : servers) {
            try {
                ServerStatus status = rconService.checkServerStatus(server);
                server.setStatus(status);
            }
        }
    }

}

