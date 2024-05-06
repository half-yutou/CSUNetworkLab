package com.xyt.service;

import com.xyt.common.Message;
import com.xyt.common.MessageType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConnectThread implements Runnable{
    private Socket socket;
    private String userId;

    @Override
    public void run() {
        while (true) {
            System.out.println("server is connecting to client" + userId);
            try {
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                Message message = (Message) in.readObject();

                if (MessageType.GET_ONLINE_USER.equals(message.getType())) {
                    System.out.println(userId + "请求在线列表");
                    String onlineUsers = ConnectThreadManager.getOnlineUsers();

                    Message retMessage = new Message();
                    retMessage.setType(MessageType.RET_ONLINE_USER);
                    retMessage.setContent(onlineUsers);
                    ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                    out.writeObject(retMessage);
                } else if (MessageType.COMMON_MESSAGE.equals(message.getType())) {
                    String receiver = message.getReceiver();
                    ConnectThread receiverThread = ConnectThreadManager.getThread(receiver);
                    ObjectOutputStream out = new ObjectOutputStream(receiverThread.getSocket()
                            .getOutputStream());
                    out.writeObject(message);
                    System.out.println(message.getSender() + "-->" + receiver + ": " + message.getContent());

                } else if (MessageType.GROUP_MESSAGE.equals(message.getType())) {
                    for (String userId : ConnectThreadManager.map.keySet()) {
                        ObjectOutputStream out = new ObjectOutputStream(
                                ConnectThreadManager.getThread(userId).getSocket().getOutputStream());
                        out.writeObject(message);
                    }
                } else if (MessageType.EXIT_CLIENT.equals(message.getType())){
                    System.out.println(userId + "exit!");
                    ConnectThreadManager.deleteThread(userId);
                    socket.close();
                    break;
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
