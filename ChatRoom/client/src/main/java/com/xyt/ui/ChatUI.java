package com.xyt.ui;

import com.xyt.service.ClientService;
import com.xyt.service.ConnectThreadManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.CopyOnWriteArrayList;

@SuppressWarnings({"all"})
public class ChatUI {
    private String curUserid;
    private JFrame frame;
    private JPanel loginPanel, mainPanel, cardPanel;
    private JTextField idField, passwordField;
    private JTextArea messageArea;
    private JTextField messageField;
    private ClientService clientService;

    public ChatUI(ClientService clientService) {
        this.clientService = clientService;
        initialize();
    }

    public void showMessage(CopyOnWriteArrayList<String> messageBox) {
        try {
            Thread.sleep(100);
            for (String s : messageBox) {
                messageArea.append(s + "\n");
            }
            messageBox.clear();
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void initialize() {
        // Frame setup
        frame = new JFrame("即时通讯系统");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        // Card panel for switching between login and main panels
        cardPanel = new JPanel(new CardLayout());
        frame.getContentPane().add(cardPanel, BorderLayout.CENTER);

        // Login Panel
        loginPanel = new JPanel(new GridLayout(3, 2));
        cardPanel.add(loginPanel, "loginPanel");

        JLabel idLabel = new JLabel("用户名:");
        loginPanel.add(idLabel);

        idField = new JTextField();
        loginPanel.add(idField);
        idField.setColumns(10);

        JLabel passwordLabel = new JLabel("密码:");
        loginPanel.add(passwordLabel);

        passwordField = new JPasswordField();
        loginPanel.add(passwordField);
        passwordField.setColumns(10);

        JButton loginButton = new JButton("登录");
        loginPanel.add(loginButton);

        JButton exitButton = new JButton("退出");
        loginPanel.add(exitButton);

        ChatUI now = this;
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String id = idField.getText();
                String passwd = passwordField.getText();
                ConnectThreadManager.user2ChatUI.put(id, now);
                if (clientService.login(id, passwd)) {
                    curUserid = id;
                    showMainPanel();
                } else {
                    JOptionPane.showMessageDialog(frame, "账号不存在或密码错误!");
                }
            }
        });

        exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        // Main Panel
        mainPanel = new JPanel(new BorderLayout());
        cardPanel.add(mainPanel, "mainPanel");

        JPanel topPanel = new JPanel();
        mainPanel.add(topPanel, BorderLayout.NORTH);

        JButton onlineUsersButton = new JButton("查询在线用户");
        topPanel.add(onlineUsersButton);

        JButton groupChatButton = new JButton("群发消息");
        topPanel.add(groupChatButton);

        JButton privateChatButton = new JButton("私发消息");
        topPanel.add(privateChatButton);

        JButton logoutButton = new JButton("退出系统");
        topPanel.add(logoutButton);

        messageArea = new JTextArea();
        messageArea.setEditable(false);
        mainPanel.add(new JScrollPane(messageArea), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        messageField = new JTextField();
        messageField.setColumns(20);
        bottomPanel.add(messageField);

        JButton sendButton = new JButton("发送");
        bottomPanel.add(sendButton);

        onlineUsersButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clientService.onlineUser();
            }
        });

        groupChatButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String groupContent = JOptionPane.showInputDialog(frame, "请输入群发内容:");
                if (groupContent != null && !groupContent.trim().isEmpty()) {
                    clientService.groupChat(groupContent, clientService.getUser().getUserId());
                }
            }
        });

        privateChatButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String receiver = JOptionPane.showInputDialog(frame, "请输入接收方id:");
                if (receiver != null && !receiver.trim().isEmpty()) {
                    String content = JOptionPane.showInputDialog(frame, "请输入内容:");
                    if (content != null && !content.trim().isEmpty()) {
                        clientService.privateChat(content, clientService.getUser().getUserId(), receiver);
                    }
                }
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clientService.logout();
                showLoginPanel();
            }
        });

        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String content = messageField.getText();
                if (content != null && !content.trim().isEmpty()) {
                    clientService.groupChat(content, clientService.getUser().getUserId());
                    messageField.setText("");
                }
            }
        });

        // Show login panel initially
        showLoginPanel();
        frame.setVisible(true);
    }

    private void showLoginPanel() {
        CardLayout cl = (CardLayout) (cardPanel.getLayout());
        cl.show(cardPanel, "loginPanel");
    }

    private void showMainPanel() {
        CardLayout cl = (CardLayout) (cardPanel.getLayout());
        cl.show(cardPanel, "mainPanel");
    }

    public static void main(String[] args) {
        ClientService clientService = new ClientService();
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    ChatUI window = new ChatUI(clientService);
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
