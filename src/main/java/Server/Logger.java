package Server;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;

public class Logger {
    private static Logger logger = null;

    public void log(String msg, String path) {
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(path, true));
            writer.write("[" + LocalDateTime.now() + "]" + msg + "\n");
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static Logger getInstance() {
        if (logger == null) {
            logger = new Logger();
        }
        return logger;
    }
}
