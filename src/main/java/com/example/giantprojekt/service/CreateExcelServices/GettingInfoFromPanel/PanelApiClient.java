package com.example.giantprojekt.service.CreateExcelServices.GettingInfoFromPanel;

import com.example.giantprojekt.configs.ServerConfig;
import com.example.giantprojekt.service.CreateExcelServices.GettingInfoFromPanel.HttpClientUtil;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public final class PanelApiClient {
    private static final String API_KEY = ResourceBundle
            .getBundle("application")
            .getString("pterodactyl.api.token");
    private static final String BASE_URL = ResourceBundle
            .getBundle("application")
            .getString("pterodactyl.api.url") + "api/application/";

    private PanelApiClient() {}

    /** Получить всех серверов: Map<UUID, Map<имяПоля, значение>>. */
    public static Map<String, Map<String, Object>> fetchAllServers() throws Exception {
        String url = BASE_URL + "servers";
        List<JsonObject> pages = HttpClientUtil.fetchAllPages(url, API_KEY);
        return pages.stream().collect(Collectors.toMap(
                server -> server.getAsJsonObject("attributes").get("uuid").getAsString(),
                PanelApiClient::mapServerAttributes
        ));
    }

    /** Получить всех пользователей: Map<userID, email>. */
    public static Map<Integer, String> fetchAllUsers() throws Exception {
        String url = BASE_URL + "users";
        List<JsonObject> pages = HttpClientUtil.fetchAllPages(url, API_KEY);
        Map<Integer, String> users = new HashMap<>();
        for (JsonObject userObj : pages) {
            JsonObject a = userObj.getAsJsonObject("attributes");
            users.put(a.get("id").getAsInt(), a.get("email").getAsString());
        }
        return users;
    }

    /** Преобразовать атрибуты одного сервера в Map<String,Object>. */
    private static Map<String,Object> mapServerAttributes(JsonObject obj) {
        JsonObject a = obj.getAsJsonObject("attributes");
        Map<String,Object> m = new HashMap<>();

        // 1) Простые поля (FLAT_KEYS)
        for (String key : ServerConfig.FLAT_KEYS) {
            m.put(key, unwrap(a.get(key)));
        }

        // 2) Вложенные группы (NESTED_KEYS → строим Map<String,Map> по родителю)
        Map<String, Map<String,Object>> groups = new HashMap<>();
        for (String[] path : ServerConfig.NESTED_KEYS) {
            String parent = path[0], field = path[1];
            JsonObject sub = a.has(parent) ? a.getAsJsonObject(parent) : null;
            Object value = (sub != null ? unwrap(sub.get(field)) : "");
            groups.computeIfAbsent(parent, k -> new HashMap<>()).put(field, value);
        }
        // перенесём каждую группу в итоговую карту
        groups.forEach(m::put);

        // 3) Даты (DATE_KEYS)
        for (String key : ServerConfig.DATE_KEYS) {
            m.put(key, a.get(key).getAsString());
        }

        return m;
    }



    /** Преобразовать JsonElement в Java-тип (int, boolean или String). */
    private static Object unwrap(com.google.gson.JsonElement el) {
        if (el == null || el.isJsonNull()) return "";
        if (el.getAsJsonPrimitive().isNumber())  return el.getAsInt();
        if (el.getAsJsonPrimitive().isBoolean()) return el.getAsBoolean();
        return el.getAsString();
    }
}
