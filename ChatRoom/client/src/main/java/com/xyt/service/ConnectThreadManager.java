package com.xyt.service;

import com.xyt.ui.ChatUI;

import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class ConnectThreadManager {
    public static HashMap<String, ChatUI> user2ChatUI = new HashMap<>();
    //key: userId
    public static HashMap<String, ConnectThread> map = new HashMap<>();

    public static void addThread(String userId, ConnectThread connectThread) {
        map.put(userId, connectThread);
    }

    public static ConnectThread getThread(String userId) {
        return map.get(userId);
    }

    public static void deleteThread(String userId) {
        map.remove(userId);
    }
}
