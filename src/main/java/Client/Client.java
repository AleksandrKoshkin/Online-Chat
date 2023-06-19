package Client;

import Server.Logger;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Properties;
import java.util.Scanner;

public class Client {
    public static final String settings = "settings.txt";
    private int port;
    private String host;
    private final BufferedReader in;
    private final PrintWriter out;
    private String path;
    private Socket socket;
    private final Scanner scanner;

    public Client() {

        Logger log = Logger.getInstance();
        setting();
        try {
            this.socket = new Socket(host, port);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
            scanner = new Scanner(System.in);
            String msg;
            System.out.print("Введите ваше имя: ");
            msg = scanner.nextLine();
            out.println("/name " + msg);
            String receivedMsg = "SERVER: " + in.readLine();
            System.out.println(receivedMsg);
            log.log(receivedMsg, path);

            Thread read = new Thread(() -> {
                String text;
                try {
                    while (true) {
                        text = in.readLine();
                        if (text.contains("/exit")) {
                            System.out.println("Вы вышли из чата");
                            break;
                        }
                        System.out.println(text);
                        log.log(text, path);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            Thread write = new Thread(() -> {
                while (true) {
                    String userMsg;
                    Scanner scanner = new Scanner(System.in);
                    userMsg = scanner.nextLine();
                    out.println(userMsg);
                    out.flush();
                    log.log(userMsg, path);
                    if (userMsg.equals("/exit")) {
                        break;
                    }
                }
            });

            write.start();
            read.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setting() {
        try (FileReader reader = new FileReader(settings)) {
            Properties properties = new Properties();
            properties.load(reader);
            port = Integer.parseInt(properties.getProperty("SERVER_PORT"));
            host = properties.getProperty("SERVER_HOST");
            path = properties.getProperty("CLIENT_LOG");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        new Client();
    }
}


