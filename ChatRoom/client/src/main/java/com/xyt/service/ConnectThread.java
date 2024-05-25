package com.xyt.service;

import com.xyt.common.Message;
import com.xyt.common.MessageType;
import com.xyt.ui.ChatUI;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConnectThread implements Runnable {
    private Socket socket;
    private ChatUI chatUI;

    @Override
    public void run() {
        while (true) {
            try {
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                Message returnMess = (Message) in.readObject();
                if (MessageType.RET_ONLINE_USER.equals(returnMess.getType())) {
                    CopyOnWriteArrayList<String> messageBox = new CopyOnWriteArrayList<>();
                    messageBox.add("=====在线用户列表=====");
                    String[] onlineUsers = returnMess.getContent().split(" ");
                    for (String u : onlineUsers) {
                        messageBox.add("用户: " + u);
                    }
                    chatUI.showMessage(messageBox);

                } else if (MessageType.COMMON_MESSAGE.equals(returnMess.getType())) {
                    System.out.println();
                    System.out.println(returnMess.getSender() + "对你说: " + returnMess.getContent());

                    CopyOnWriteArrayList<String> messageBox = new CopyOnWriteArrayList<>();
                    messageBox.add(returnMess.getSender() + "对你说: " + returnMess.getContent());
                    chatUI.showMessage(messageBox);
                } else if (MessageType.GROUP_MESSAGE.equals(returnMess.getType())) {
                    System.out.println();
                    System.out.println(returnMess.getSender() + "对大家说: " + returnMess.getContent());

                    CopyOnWriteArrayList<String> messageBox = new CopyOnWriteArrayList<>();
                    messageBox.add(returnMess.getSender() + "对大家说: " + returnMess.getContent());
                    chatUI.showMessage(messageBox);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

}
