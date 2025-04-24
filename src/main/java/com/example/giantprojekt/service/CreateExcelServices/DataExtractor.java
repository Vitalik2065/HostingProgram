package com.example.giantprojekt.service.CreateExcelServices;

import java.util.Map;

@SuppressWarnings("unchecked")
public final class DataExtractor {
    private DataExtractor() {}

    /**
     * Извлекает значение по цепочке ключей из вложенных карт.
     * Если где-то не карта или нет ключа, вернёт пустую строку.
     */
    public static Object getValue(Map<String, Object> data, String... path) {
        if (path == null || path.length == 0) return "";
        Map<String, Object> curr = data;
        for (int i = 0; i < path.length - 1; i++) {
            Object next = curr.get(path[i]);
            if (!(next instanceof Map)) return "";
            curr = (Map<String, Object>) next;
        }
        Object res = curr.get(path[path.length - 1]);
        return res != null ? res : "";
    }
}
