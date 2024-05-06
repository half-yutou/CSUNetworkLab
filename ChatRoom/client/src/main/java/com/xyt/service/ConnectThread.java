package com.xyt.service;

import com.xyt.common.Message;
import com.xyt.common.MessageType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConnectThread implements Runnable {
    private Socket socket;

    @Override
    public void run() {
        while (true) {
            try {
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                Message returnMess = (Message) in.readObject();
                if (MessageType.RET_ONLINE_USER.equals(returnMess.getType())) {
                    System.out.println("=====在线用户列表=====");
                    String[] onlineUsers = returnMess.getContent().split(" ");
                    for (String u : onlineUsers) {
                        System.out.println("用户: " + u);
                    }
                } else if (MessageType.COMMON_MESSAGE.equals(returnMess.getType())) {
                    System.out.println();
                    System.out.println(returnMess.getSender() + "对你说: " + returnMess.getContent());
                } else if (MessageType.GROUP_MESSAGE.equals(returnMess.getType())) {
                    System.out.println();
                    System.out.println(returnMess.getSender() + "对大家说: " + returnMess.getContent());
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

}
