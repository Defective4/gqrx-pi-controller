package io.github.defective4.raspberrypi.gqctl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

public class GqrxController implements AutoCloseable {

    public enum Modulation {
        AM, AMS, CW, CWL, FM, LSB, USB, WFM
    }

    private final String host;
    private final int port;
    private BufferedReader reader;
    private final Socket socket = new Socket();
    private PrintWriter writer;

    public GqrxController(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public void close() throws IOException {
        try {
            writer.write("c");
        } catch (Exception e) {}
        socket.close();
    }

    public void connect() throws IOException {
        socket.connect(new InetSocketAddress(host, port));
        writer = new PrintWriter(socket.getOutputStream(), true);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public int getFrequency() throws NumberFormatException, IOException {
        writer.println("f");
        return Integer.parseInt(reader.readLine());
    }

    public void setFrequency(int freq) throws IOException {
        writer.println("F " + freq);
        reader.readLine();
    }

    public void setModulation(Modulation mod) throws IOException {
        writer.println("M " + mod.name());
        reader.readLine();
    }
}
