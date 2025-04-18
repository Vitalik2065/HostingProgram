package com.example.giantprojekt.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class PanelGetServers {



    public static Map<String, Map<String, Object>> fetchAllServers() {
        Map<String, Map<String, Object>> servers = new HashMap<>();
        Gson gson = new Gson();
        int currentPage = 1;
        int totalPages = 1; // будем обновлять после первого запроса
        String API_KEY = ResourceBundle
                .getBundle("application")
                .getString("pterodactyl.api.token");
        String get_url = ResourceBundle
                .getBundle("application")
                .getString("pterodactyl.api.url");

        try {
            do {
                URL pageUrl = new URL(get_url + "api/application/servers?page=" + currentPage);
                HttpURLConnection conn = (HttpURLConnection) pageUrl.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Authorization", API_KEY);
                conn.setRequestProperty("Accept", "application/json");

                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String inputLine;
                    StringBuilder response = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    // Парсим JSON ответа
                    JsonObject jsonObject = gson.fromJson(response.toString(), JsonObject.class);
                    JsonArray dataArray = jsonObject.getAsJsonArray("data");






                    // Обработка каждого сервера на текущей странице
                    for (JsonElement element : dataArray) {
                        JsonObject attributes = element.getAsJsonObject().getAsJsonObject("attributes");
                        String uuid = attributes.get("uuid").getAsString();

                        Map<String, Object> serverInfo = new HashMap<>();
                        serverInfo.put("id", attributes.get("id").getAsInt());
                        serverInfo.put("name", attributes.get("name").getAsString());
                        serverInfo.put("description", attributes.get("description").getAsString());

                        serverInfo.put("user", attributes.get("user").getAsInt());
                        serverInfo.put("suspended", attributes.get("suspended").getAsBoolean());
                        // <--- Добавляем недостающие поля:
                        serverInfo.put("node", attributes.get("node").getAsInt());
                        serverInfo.put("allocation", attributes.get("allocation").getAsInt());
                        serverInfo.put("nest", attributes.get("nest").getAsInt());
                        serverInfo.put("egg", attributes.get("egg").getAsInt());

                        // limits
                        JsonObject limits = attributes.getAsJsonObject("limits");
                        Map<String, Object> limitsMap = new HashMap<>();
                        limitsMap.put("memory", limits.get("memory").getAsInt());
                        limitsMap.put("swap", limits.get("swap").getAsInt());
                        limitsMap.put("disk", limits.get("disk").getAsInt());
                        limitsMap.put("io", limits.get("io").getAsInt());
                        limitsMap.put("cpu", limits.get("cpu").getAsInt());
                        serverInfo.put("limits", limitsMap);





                        // feature_limits
                        JsonObject featureLimits = attributes.getAsJsonObject("feature_limits");
                        Map<String, Object> featureLimitsMap = new HashMap<>();
                        featureLimitsMap.put("databases", featureLimits.get("databases").getAsInt());
                        featureLimitsMap.put("allocations", featureLimits.get("allocations").getAsInt());
                        featureLimitsMap.put("backups", featureLimits.get("backups").getAsInt());
                        serverInfo.put("feature_limits", featureLimitsMap);

                        // container
                        JsonObject container = attributes.getAsJsonObject("container");
                        if (container != null) {
                            Map<String, Object> containerMap = new HashMap<>();
                            containerMap.put("startup_command", container.get("startup_command").getAsString());
                            containerMap.put("image", container.get("image").getAsString());
                            serverInfo.put("container", containerMap);
                        }

                        // Даты
                        serverInfo.put("created_at", attributes.get("created_at").getAsString());
                        serverInfo.put("updated_at", attributes.get("updated_at").getAsString());

                        servers.put(uuid, serverInfo);
                    }

                    // Обновляем пагинацию
                    JsonObject meta = jsonObject.getAsJsonObject("meta");
                    JsonObject pagination = meta.getAsJsonObject("pagination");
                    totalPages = pagination.get("total_pages").getAsInt();
                } else {
                    System.out.println("Ошибка: " + conn.getResponseMessage());
                    break; // если ошибка, выходим из цикла
                }
                conn.disconnect();
                currentPage++;
            } while (currentPage <= totalPages);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Количество серверов: " + servers.size());
        return servers;
    }
}
