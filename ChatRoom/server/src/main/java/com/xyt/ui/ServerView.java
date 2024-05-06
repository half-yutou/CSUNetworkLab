package com.xyt.ui;

import com.xyt.service.ServerService;

public class ServerView {
    public static void main(String[] args) {
        ServerService serverService = new ServerService();
        serverService.loginService();
    }
}

