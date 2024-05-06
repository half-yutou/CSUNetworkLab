package com.xyt.service;

import com.xyt.common.Message;
import com.xyt.common.MessageType;
import com.xyt.common.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

@Data
@AllArgsConstructor
public class ServerService {
    private ServerSocket socket;
    private static ConcurrentHashMap<String, User> validUsers = new ConcurrentHashMap<>();

    static {
        validUsers.put("000", new User("000", "123456"));
        validUsers.put("001", new User("001", "123456"));
        validUsers.put("002", new User("002", "123456"));
        validUsers.put("003", new User("003", "123456"));
        validUsers.put("004", new User("004", "123456"));
    }

    public ServerService() {
        try {
            this.socket = new ServerSocket(9999);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean checkUser(User user) {
        return user.getPasswd()
                .equals(validUsers.get(user.getUserId()).getPasswd());
    }

    public void loginService() {
        try {
            while (true) {
                Socket clientSocket = socket.accept();
                ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());

                User user = (User) in.readObject();
                Message message = new Message();
                if (checkUser(user)) {
                    ConnectThread connectThread = new ConnectThread(clientSocket, user.getUserId());
                    new Thread(connectThread).start();
                    ConnectThreadManager.addThread(user.getUserId(), connectThread);

                    message.setType(MessageType.LOGIN_SUCCESS);
                    out.writeObject(message);
                } else {
                    message.setType(MessageType.LOGIN_FAIL);
                    out.writeObject(message);
                    clientSocket.close();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
