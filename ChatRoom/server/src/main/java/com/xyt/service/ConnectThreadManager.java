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

    public static String getOnlineUsers() {
        String ret = "";
        for (String key : map.keySet()) {
            ret += key;
            ret += " ";
        }
        return ret;
    }
}
