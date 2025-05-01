package ru.floda.home.rustshop.rcon;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ru.floda.home.rustshop.exceptions.AuthenticationException;
import ru.floda.home.rustshop.model.Server;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Random;

@Slf4j
@Getter
public class Rcon {

    private final Object sync = new Object();
    private final Random rand = new Random();

    private int requestId;
    private Socket socket;

    private final Charset charset;

    public Rcon(Server server) throws IOException {
        log.info("Create connecting");
        this.charset = StandardCharsets.UTF_8;
        this.connect(
                server.getRconIp(),
                Integer.parseInt(server.getRconPort()),
                server.getRconPassword().getBytes()
                );
    }

    public void connect(String host, int port, byte[] password) throws IOException, AuthenticationException {
        log.info("Connecting to " + host + ":" + port);
        if(host == null || host.trim().isEmpty()) {
            throw new IllegalArgumentException("Host can't be null or empty");
        }

        if(port < 1 || port > 65535) {
            throw new IllegalArgumentException("Port is out of range");
        }

        // Connect to the rcon server
        synchronized(sync) {
            // New random request id
            this.requestId = rand.nextInt();

            // We can't reuse a socket, so we need a new one
            this.socket = new Socket(host, port);
        }

        // Send the auth packet
        log.info("Send auth packet");
        RconPacket res = this.send(RconPacket.SERVERDATA_AUTH, password);
        log.info(res.toString());

        // Auth failed
        if(res.getRequestId() == -1) {
            log.error("Auth packet failed");
            throw new AuthenticationException("Password rejected by server");
        }
    }

    public void disconnect() throws IOException {
        synchronized(sync) {
            this.socket.close();
        }
    }

    public String command(String payload) throws IOException {
        if(payload == null || payload.trim().isEmpty()) {
            throw new IllegalArgumentException("Payload can't be null or empty");
        }

        RconPacket response = this.send(RconPacket.SERVERDATA_EXECCOMMAND, payload.getBytes());

        return new String(response.getPayload(), this.getCharset());
    }

    private RconPacket send(int type, byte[] payload) throws IOException {
        synchronized(sync) {
            return RconPacket.send(this, type, payload);
        }
    }

}
