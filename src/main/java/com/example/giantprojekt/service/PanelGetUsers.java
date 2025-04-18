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





public class PanelGetUsers {

    public static Map<Integer, String> fetchAllUsers() {
        Map<Integer, String> userMap = new HashMap<>();
        Gson gson = new Gson();
        String API_KEY = ResourceBundle
                .getBundle("application")
                .getString("pterodactyl.api.token");
        String get_url = ResourceBundle
                .getBundle("application")
                .getString("pterodactyl.api.url");



        int currentPage = 1;
        int totalPages = 1;

        try {
            do {
                // Замените "cloud.frienworld.xyz" на свой домен
                URL url = new URL(get_url + "api/application/users?page=" + currentPage);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Authorization", API_KEY);
                conn.setRequestProperty("Accept", "application/json");

                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    JsonObject json = gson.fromJson(response.toString(), JsonObject.class);
                    JsonArray dataArray = json.getAsJsonArray("data");

                    // Парсим пользователей
                    for (JsonElement element : dataArray) {
                        JsonObject attributes = element.getAsJsonObject().getAsJsonObject("attributes");
                        int userId = attributes.get("id").getAsInt();
                        String email = attributes.get("email").getAsString();
                        userMap.put(userId, email);
                    }

                    // Пагинация
                    JsonObject meta = json.getAsJsonObject("meta");
                    JsonObject pagination = meta.getAsJsonObject("pagination");
                    totalPages = pagination.get("total_pages").getAsInt();

                } else {
                    System.out.println("Ошибка при получении пользователей: " + conn.getResponseMessage());
                    break;
                }
                conn.disconnect();
                currentPage++;
            } while (currentPage <= totalPages);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Всего пользователей: " + userMap.size());
        return userMap;
    }
}
