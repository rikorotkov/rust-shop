package ru.floda.home.rustshop.rcon;

import lombok.Getter;
import ru.floda.home.rustshop.exceptions.MalformedPacketException;

import java.io.*;
import java.net.SocketException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

@Getter
public class RconPacket {

    public static final int SERVERDATA_EXECCOMMAND = 2;
    public static final int SERVERDATA_AUTH = 3;

    private int requestId;
    private int type;
    private byte[] payload;

    private RconPacket(int requestId, int type, byte[] payload) {
        this.requestId = requestId;
        this.type = type;
        this.payload = payload;
    }

    protected static RconPacket send(Rcon rcon, int type, byte[] payload) throws IOException {
        try {
            RconPacket.write(rcon.getSocket().getOutputStream(), rcon.getRequestId(), type, payload);
        }
        catch(SocketException se) {
            // Close the socket if something happens
            rcon.getSocket().close();

            // Rethrow the exception
            throw se;
        }

        return RconPacket.read(rcon.getSocket().getInputStream());
    }

    private static void write(OutputStream out, int requestId, int type, byte[] payload) throws IOException {
        int bodyLength = RconPacket.getBodyLength(payload.length);
        int packetLength = RconPacket.getPacketLength(bodyLength);

        ByteBuffer buffer = ByteBuffer.allocate(packetLength);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        buffer.putInt(bodyLength);
        buffer.putInt(requestId);
        buffer.putInt(type);
        buffer.put(payload);

        // Null bytes terminators
        buffer.put((byte)0);
        buffer.put((byte)0);

        // Woosh!
        out.write(buffer.array());
        out.flush();
    }

    private static RconPacket read(InputStream in) throws IOException {
        // Header is 3 4-bytes ints
        byte[] header = new byte[4 * 3];

        System.out.println("Trying to read header..." + Arrays.toString(header));

        // Read the 3 ints
        int bytesRead = in.read(header);
        System.out.println("Read " + bytesRead + " header bytes");

        try {
            ByteBuffer buffer = ByteBuffer.wrap(header);
            buffer.order(ByteOrder.LITTLE_ENDIAN);

            int length = buffer.getInt();
            int requestId = buffer.getInt();
            int type = buffer.getInt();

            System.out.println("Packet length: " + length);
            System.out.println("Request ID: " + requestId);
            System.out.println("Type: " + type);

            byte[] payload = new byte[length - 4 - 4 - 2];
            System.out.println("Trying to read payload of size: " + payload.length);

            DataInputStream dis = new DataInputStream(in);
            dis.readFully(payload);
            System.out.println("Payload read successfully");

            // Read the null bytes
            dis.read(new byte[2]);
            System.out.println("Null bytes read");

            return new RconPacket(requestId, type, payload);
        } catch(BufferUnderflowException | EOFException e) {
            System.err.println("Error reading packet: " + e.getMessage());
            throw new MalformedPacketException("Cannot read the whole packet");
        }
    }

    private static int getPacketLength(int bodyLength) {
        // 4 bytes for length + x bytes for body length
        return 4 + bodyLength;
    }

    private static int getBodyLength(int payloadLength) {
        // 4 bytes for requestId, 4 bytes for type, x bytes for payload, 2 bytes for two null bytes
        return 4 + 4 + payloadLength + 2;
    }

}
