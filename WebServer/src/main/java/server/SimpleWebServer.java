package server;

import java.io.*;
import java.net.*;

public class SimpleWebServer {
    public static void main(String[] args) {
        try {
            int port = 8848; // 服务器监听的端口
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server listening on port " + port + "...");

            while (true) {
                Socket clientSocket = serverSocket.accept(); // 等待客户端连接

                // 处理客户端请求
                Thread thread = new Thread(new HttpRequestHandler(clientSocket));
                thread.start();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}


