package ru.floda.home.rustshop.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.floda.home.rustshop.exceptions.AuthenticationException;
import ru.floda.home.rustshop.model.Server;
import ru.floda.home.rustshop.rcon.Rcon;
import ru.floda.home.rustshop.repository.ServerRepository;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class RconService {

    private static final Logger log = LoggerFactory.getLogger(RconService.class);
    private final ServerRepository serverRepository;

    public void test(Server server) {
        try {
            Rcon rcon = new Rcon(server);
            String res = rcon.command("list");
            log.info(res);
        } catch (AuthenticationException e) {
            log.error("Authentication failed", e);
        } catch (IOException e) {
            log.error("Connection error", e);
        } catch (Exception e) {
            log.error("Unexpected error", e);
        }
    }
}
