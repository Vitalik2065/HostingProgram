package com.example.giantprojekt.service.api;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;

/**
 * Синглтон-сервис для вызова Pterodactyl Application API.
 *  – Всегда добавляет «Bearer » к токену (если вы не написали его вручную).<br>
 *  – Читает тело ошибки и прокидывает его в ApiException, чтобы понять причину 4xx/5xx.
 */
public final class PterodactylApiService {

    /* ---------- синглтон ---------- */

    private static final PterodactylApiService INSTANCE = new PterodactylApiService();

    public static PterodactylApiService getInstance() { return INSTANCE; }

    /* ---------- поля ---------- */

    private final String token;    // чистый JWT без "Bearer "
    private final String baseUrl;  // https://panel.example.com/api/application/

    /* ---------- конструктор ---------- */

    private PterodactylApiService() {
        ResourceBundle rb = ResourceBundle.getBundle("application");
        String rawToken = rb.getString("pterodactyl.api.token").trim();
        this.token   = rawToken.startsWith("Bearer ") ? rawToken.substring(7) : rawToken;
        this.baseUrl = rb.getString("pterodactyl.api.url") + "api/application/";
    }

    /* ---------- публичное API ---------- */

    public void suspendServer(int id) throws ApiException {
        post("/servers/" + id + "/suspend");
    }

    public void resumeServer(int id) throws ApiException {
        post("/servers/" + id + "/unsuspend");
    }

    public void deleteServer(int id) throws ApiException {
        request("DELETE", "/servers/" + id);
    }

    /* ---------- низкоуровневые вызовы ---------- */

    private void post(String path) throws ApiException {
        request("POST", path);
    }

    /**
     * Универсальный метод: выполняет HTTP-запрос и проверяет код ответа.
     * Любой код ≥ 400 превращается в ApiException с текстом ошибки из тела.
     */
    private void request(String method, String path) throws ApiException {
        final String urlStr = baseUrl + (path.startsWith("/") ? path.substring(1) : path);

        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
            conn.setRequestMethod(method);
            conn.setRequestProperty("Authorization", "Bearer " + token);
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setFixedLengthStreamingMode(0);

            // Для POST|PUT|PATCH нужно закрыть пустое тело
            if ("POST".equals(method) || "PUT".equals(method) || "PATCH".equals(method)) {
                conn.setDoOutput(true);
                conn.getOutputStream().close();
            }

            handleResponse(conn, method + " " + urlStr);

        } catch (IOException io) {
            throw new ApiException("I/O error calling " + method + " " + urlStr, io);
        }
    }

    /**
     * Проверяет код ответа. Если ≥ 400 — читает тело ошибки и бросает ApiException.
     */
    private void handleResponse(HttpURLConnection conn, String op) throws ApiException {
        try {
            int code = conn.getResponseCode();
            if (code >= 400) {
                String body = readStream(conn.getErrorStream());
                throw new ApiException("HTTP " + code + " → " + body);
            }
        } catch (IOException io) {
            throw new ApiException("Cannot read response for " + op, io);
        } finally {
            conn.disconnect();
        }
    }

    /* ---------- utils ---------- */

    private static String readStream(InputStream is) {
        if (is == null) return "";
        try (InputStream in = is) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            return "(unable to read error body)";
        }
    }
}
