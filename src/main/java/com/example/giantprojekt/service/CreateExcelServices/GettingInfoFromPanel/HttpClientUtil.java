package com.example.giantprojekt.service.CreateExcelServices.GettingInfoFromPanel;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public final class HttpClientUtil {
    private static final Gson GSON = new Gson();

    private HttpClientUtil() {}

    /** Один GET-запрос, возвращает JSON-строку. */
    public static String getJson(String url, String apiKey) throws Exception {
        HttpURLConnection conn = (HttpURLConnection)new URL(url).openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", apiKey);
        conn.setRequestProperty("Accept",        "application/json");
        try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            return in.lines().collect(Collectors.joining());
        } finally {
            conn.disconnect();
        }
    }

    /**
     * Пагинация: запрашивает все страницы по шаблону baseUrl + "?page=N",
     * собирает объекты из поля "data" и возвращает их в одном списке.
     */
    public static List<JsonObject> fetchAllPages(String baseUrl, String apiKey) throws Exception {
        List<JsonObject> all = new ArrayList<>();
        int page = 1, totalPages;
        do {
            String json    = getJson(baseUrl + "?page=" + page, apiKey);
            JsonObject doc = GSON.fromJson(json, JsonObject.class);
            JsonArray data = doc.getAsJsonArray("data");
            for (JsonElement el : data) {
                all.add(el.getAsJsonObject());
            }
            JsonObject pag = doc.getAsJsonObject("meta")
                    .getAsJsonObject("pagination");
            totalPages = pag.get("total_pages").getAsInt();
            page++;
        } while (page <= totalPages);
        return all;
    }
}