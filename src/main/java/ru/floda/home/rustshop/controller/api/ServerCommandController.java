package ru.floda.home.rustshop.controller.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.floda.home.rustshop.exceptions.ResourceNotFoundException;
import ru.floda.home.rustshop.model.Server;
import ru.floda.home.rustshop.repository.ServerRepository;
import ru.floda.home.rustshop.service.RconService;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/servers/{serverId}/rcon")
public class ServerCommandController {

    private final RconService rconService;
    private final ServerRepository serverRepository;

    @GetMapping("/online")
    public void getOnlineServers(@PathVariable Long serverId) throws IOException {
        Server server = serverRepository.findById(serverId).orElseThrow(() -> new ResourceNotFoundException("Server not found"));
        rconService.test(server);
    }

//    @PostMapping("/command")
//    public ResponseEntity<RconService.RconResponse> executeCommand(
//            @PathVariable Long serverId,
//            @RequestBody RconCommandRequest request) {
//
//        Server server = serverRepository.findById(serverId)
//                .orElseThrow(() -> new ResourceNotFoundException("Server not found"));
//
//        RconService.RconResponse response = rconService.executeCommand(server, request.getCommand());
//
//        return ResponseEntity.ok(response);
//    }
//
//    @GetMapping("/players")
//    public ResponseEntity<?> getOnlinePlayers(@PathVariable Long serverId) {
//        try {
//            Server server = serverRepository.findById(serverId)
//                    .orElseThrow(() -> new ResourceNotFoundException("Server not found"));
//
//            log.info("Fetching online players for server {} ({}:{})",
//                    serverId, server.getRconIp(), server.getRconPort());
//
//            Integer online = rconService.getOnlinePlayers(server);
//            if (online == null) {
//                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
//                        .body("Failed to get player count");
//            }
//            return ResponseEntity.ok(online);
//        } catch (Exception e) {
//            log.error("Error in /players endpoint", e);
//            return ResponseEntity.internalServerError()
//                    .body("Server error: " + e.getMessage());
//        }
//    }
//
//    @Getter
//    @NoArgsConstructor
//    public static class RconCommandRequest {
//        private String command;
//    }

}
