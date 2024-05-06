package server;

import java.io.*;
import java.net.Socket;

class HttpRequestHandler implements Runnable {
    private Socket clientSocket;

    public HttpRequestHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            // 读取客户端请求
            String requestLine = in.readLine();
            System.out.println("Request received: " + requestLine);

            // 解析请求
            String[] requestParts = requestLine.split(" ");
            String method = requestParts[0];
            String path = requestParts[1];


            // 处理GET请求
            if (method.equals("GET")) {
                // 获取资源文件的输入流
                InputStream inputStream = SimpleWebServer.class.getResourceAsStream("/webroot" + path);

                if (inputStream != null) {
                    out.println("HTTP/1.1 200 OK");
                    out.println("Content-Type: text/html");
                    out.println();
                    // 如果输入流不为 null，说明找到了对应的资源文件
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    // 逐行读取文件内容
                    while ((line = reader.readLine()) != null) {
                        out.println(line);
                    }
                    reader.close();
                } else {
                    // 如果输入流为 null，说明资源文件不存在或无法读取
                    // 返回404 Not Found响应
                    out.println("HTTP/1.1 404 Not Found");
                    out.println("Content-Type: text/html");
                    out.println();
                    out.println("<h1>404 Not Found</h1>");
                }
            }

            // 关闭流和连接
            out.close();
            in.close();
            clientSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

