package Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Properties;

public class Server implements Runnable {
    public static ArrayList<Socket> clients = new ArrayList<>();
    public static final String setting = "settings.txt";
    public static int port;
    public static String host;
    public static String path;
    private static final Logger log = Logger.getInstance();
    public Socket client;

    public Server(Socket socket) {
        this.client = socket;
    }

    @Override
    public void run() {
        String name = null;
        log.log("Сервер работает. Port: " + port + " Host: " + host, path);
        System.out.println("Сервер работает. Port: " + port + " Host: " + host);
        while (true) {
            try {
                PrintWriter out = new PrintWriter(this.client.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(this.client.getInputStream()));
                String line;
                while ((line = in.readLine()) != null) {
                    if (line.lastIndexOf("/name") == 0) {
                        name = line;
                        name = name.replace("/name", "").trim();
                        log.log(name + " зашёл в чат", path);
                        communicationServer("Привет, " + name + "!");
                        continue;
                    }
                    if (line.equals("/exit")) {
                        log.log(name + " покинул чат", path);
                        communication('[' + name + "] " + " покинул чат");
                        communicationServer("/exit");
                        clients.remove(client);
                        return;
                    }
                    communication('[' + name + "] " + line);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void communication(String message) throws IOException {
        for (Socket client : clients) {
            if (client.isClosed()) {
                continue;
            }
            if (!client.equals(this.client)) {
                PrintWriter sender = new PrintWriter(client.getOutputStream());
                sender.println(message);
                sender.flush();
            }
        }
        log.log(message, path);
    }

    private void communicationServer(String message) throws IOException {
        PrintWriter sender = new PrintWriter(client.getOutputStream());
        sender.println(message);
        sender.flush();
        log.log(message, path);
    }

    public static void connect() {
        try (FileReader reader = new FileReader(setting)) {
            Properties properties = new Properties();
            properties.load(reader);
            port = Integer.parseInt(properties.getProperty("SERVER_PORT"));
            host = properties.getProperty("SERVER_HOST");
            path = properties.getProperty("SERVER_LOG");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try (ServerSocket server = new ServerSocket(port)) {
            while (true) {
                Socket client = server.accept();
                clients.add(client);
                new Thread(new Server(client)).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
