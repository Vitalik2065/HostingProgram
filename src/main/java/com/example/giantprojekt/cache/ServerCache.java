package com.example.giantprojekt.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** Сопоставление UUID > числовой ID сервера (заполняется при загрузке). */
    public final class ServerCache {

    private static final Map<String,Integer> uuidToId = new ConcurrentHashMap<>();

    private ServerCache() {}

    public static void put(String uuid, int id) {
        uuidToId.put(uuid, id);
    }

    public static int uuidToId(String uuid) {
        return uuidToId.getOrDefault(uuid, -1);
    }
}
