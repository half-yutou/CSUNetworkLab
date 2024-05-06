package com.xyt.ui;

import com.xyt.service.ClientService;

import java.util.Scanner;

public class ClientView {
    private boolean loop = true;
    private final ClientService clientService = new ClientService();

    public static void main(String[] args) {
        ClientView clientView = new ClientView();
        clientView.ui();
    }

    public void ui() {
        String key;
        Scanner scan = new Scanner(System.in);
        while (loop) {
            System.out.println("=====即时通讯系统=====");
            System.out.println("\t\t1. 登录系统");
            System.out.println("\t\t9. 退出系统");
            System.out.print("请输入选择:");
            key = scan.next();
            switch (key) {
                case "1":
                    System.out.println("请输入用户名");
                    String id = scan.next();
                    System.out.println("请输入密 码");
                    String passwd = scan.next();

                    if (clientService.login(id, passwd)) {
                        while (loop) {
                            System.out.println("=====欢迎登录=====");
                            System.out.println("\t\t1. 查询在线用户");
                            System.out.println("\t\t2. 群发消息");
                            System.out.println("\t\t3. 私发消息");
                            System.out.println("\t\t9. 退出系统");
                            System.out.print("请输入选择: ");
                            key = scan.next();
                            switch (key) {
                                case "1":
                                    clientService.onlineUser();
                                    break;
                                case "2":
                                    System.out.println("请输入群发内容: ");
                                    String groupContent = scan.next();

                                    clientService.groupChat(groupContent,
                                            clientService.getUser().getUserId());

                                    break;
                                case "3":
                                    System.out.print("请输入接收方id: ");
                                    String receiver = scan.next();
                                    System.out.println("请输入内容: ");
                                    String content = scan.next();

                                    clientService.privateChat(content,
                                            clientService.getUser().getUserId(), receiver);
                                    break;
                                case "9":
                                    System.out.println("=====系统退出=====");
                                    clientService.logout();
                                    loop = false;
                                    break;
                            }
                        }
                    } else {
                        System.out.println("账号不存在 or 密码错误!");
                    }
                    break;
                case "9":
                    System.out.println("=====系统退出=====");
                    loop = false;
                    break;
            }
        }
    }
}
