
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) throws Exception {
        BooleanSearchEngine engine = new BooleanSearchEngine(new File("pdfs"));
        String word;
        try (ServerSocket serverSocket = new ServerSocket(8989);) {
            while (true) {
                System.out.println("Starting server at " + "8989" + "...");
                try (
                        Socket socket = serverSocket.accept();
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                ) {
                    System.out.println("New connection accepted");
                    word = in.readLine();
                    out.println(engine.search(word));
                }
            }
        } catch (IOException e) {
            System.out.println("server cannot start");
            e.printStackTrace();
        }
    }
}