package com.xyt.service;

import java.util.HashMap;

public class ConnectThreadManager {
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
