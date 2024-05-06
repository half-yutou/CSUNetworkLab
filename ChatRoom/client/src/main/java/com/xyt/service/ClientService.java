package com.xyt.service;

import com.xyt.common.Message;
import com.xyt.common.MessageType;
import com.xyt.common.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientService {
    private User user = new User();
    private Socket socket;

    /**
     * 向服务器发送登录请求,并返回是否登录成功
     * @param userId
     * @param passwd
     * @return
     */
    public boolean login(String userId, String passwd) {
        user.setUserId(userId);
        user.setPasswd(passwd);

        try {
            socket = new Socket(InetAddress.getLocalHost(), 9999);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            out.writeObject(user);
            Message message = (Message) in.readObject();

            if (MessageType.LOGIN_SUCCESS.equals(message.getType())) {
                ConnectThread connectThread = new ConnectThread(socket);
                new Thread(connectThread).start();

                ConnectThreadManager.addThread(userId, connectThread);

                return true;
            } else {
                socket.close();
                return false;
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 向服务器发送登出请求
     */
    public void logout() {
        Message message = new Message();
        message.setType(MessageType.EXIT_CLIENT);
        message.setSender(user.getUserId());

        try {
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(message);
            ConnectThreadManager.deleteThread(user.getUserId());
            System.exit(0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 向服务器发送查询 在线用户的 message
     */
    public void onlineUser() {
        Message message = new Message();
        message.setType(MessageType.GET_ONLINE_USER);

        try {
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 私聊消息
     */
    public void privateChat(String content, String sender, String receiver) {
        Message message = new Message(sender, receiver, content, new Date().toString(),
                MessageType.COMMON_MESSAGE);
        try {
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(message);
            System.out.println();
            System.out.println(sender + "-->" + receiver + ": " + content);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * 群聊消息
     */
    public void groupChat(String content, String sender) {
        Message message = new Message(sender, "-1", content, new Date().toString(),
                MessageType.GROUP_MESSAGE);
        try {
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(message);
            System.out.println();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
