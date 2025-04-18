package com.example.giantprojekt.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map.Entry;


public class ExcelServiceMain {
    public static void main(String[] args) {
        // Создаем книгу и лист Excel
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Data");

        // Создаем заголовочную строку для основной таблицы серверов
        Row header = sheet.createRow(0);
        int cellIndex = 0;
        header.createCell(cellIndex++).setCellValue("UUID");
        header.createCell(cellIndex++).setCellValue("ID");
        header.createCell(cellIndex++).setCellValue("Name");
        header.createCell(cellIndex++).setCellValue("Description");
        header.createCell(cellIndex++).setCellValue("Memory");
        header.createCell(cellIndex++).setCellValue("Swap");
        header.createCell(cellIndex++).setCellValue("Disk");
        header.createCell(cellIndex++).setCellValue("IO");
        header.createCell(cellIndex++).setCellValue("CPU");
        header.createCell(cellIndex++).setCellValue("Databases");
        header.createCell(cellIndex++).setCellValue("Allocations");
        header.createCell(cellIndex++).setCellValue("Backups");
        header.createCell(cellIndex++).setCellValue("User ID");
        header.createCell(cellIndex++).setCellValue("User Email");
        header.createCell(cellIndex++).setCellValue("Node");
        header.createCell(cellIndex++).setCellValue("Allocation");
        header.createCell(cellIndex++).setCellValue("Nest");
        header.createCell(cellIndex++).setCellValue("Egg");
        header.createCell(cellIndex++).setCellValue("Startup Command");
        header.createCell(cellIndex++).setCellValue("Image");
        header.createCell(cellIndex++).setCellValue("Created At");
        header.createCell(cellIndex++).setCellValue("Updated At");
        header.createCell(cellIndex++).setCellValue("Suspended");

        // Получаем данные серверов и пользователей
        Map<String, Map<String, Object>> servers = PanelGetServers.fetchAllServers();
        Map<Integer, String> userIdToEmail = PanelGetUsers.fetchAllUsers();

        // Сортируем серверы по ID пользователя, чтобы сервера одного владельца шли подряд
        List<Entry<String, Map<String, Object>>> sortedEntries = new ArrayList<>(servers.entrySet());
        Collections.sort(sortedEntries, new Comparator<Entry<String, Map<String, Object>>>() {
            @Override
            public int compare(Entry<String, Map<String, Object>> e1, Entry<String, Map<String, Object>> e2) {
                int user1 = (int) e1.getValue().getOrDefault("user", 0);
                int user2 = (int) e2.getValue().getOrDefault("user", 0);
                return Integer.compare(user1, user2);
            }
        });

        int rowIndex = 1; // начинаем с первой строки после заголовка
        for (Entry<String, Map<String, Object>> entry : sortedEntries) {
            String uuid = entry.getKey();
            Map<String, Object> serverData = entry.getValue();

            Row row = sheet.createRow(rowIndex++);
            cellIndex = 0;

            // Записываем основные данные
            row.createCell(cellIndex++).setCellValue(uuid);
            if (serverData.containsKey("id"))
                row.createCell(cellIndex++).setCellValue((int) serverData.get("id"));
            else
                row.createCell(cellIndex++).setCellValue("");
            row.createCell(cellIndex++).setCellValue((String) serverData.getOrDefault("name", ""));
            row.createCell(cellIndex++).setCellValue((String) serverData.getOrDefault("description", ""));

            // Ограничения (limits)
            Map<String, Object> limits = (Map<String, Object>) serverData.get("limits");
            if (limits != null) {
                row.createCell(cellIndex++).setCellValue((int) limits.getOrDefault("memory", 0));
                row.createCell(cellIndex++).setCellValue((int) limits.getOrDefault("swap", 0));
                row.createCell(cellIndex++).setCellValue((int) limits.getOrDefault("disk", 0));
                row.createCell(cellIndex++).setCellValue((int) limits.getOrDefault("io", 0));
                row.createCell(cellIndex++).setCellValue((int) limits.getOrDefault("cpu", 0));
            } else {
                for (int i = 0; i < 5; i++) {
                    row.createCell(cellIndex++).setCellValue("");
                }
            }

            // Функциональные ограничения (feature_limits)
            Map<String, Object> featureLimits = (Map<String, Object>) serverData.get("feature_limits");
            if (featureLimits != null) {
                row.createCell(cellIndex++).setCellValue((int) featureLimits.getOrDefault("databases", 0));
                row.createCell(cellIndex++).setCellValue((int) featureLimits.getOrDefault("allocations", 0));
                row.createCell(cellIndex++).setCellValue((int) featureLimits.getOrDefault("backups", 0));
            } else {
                for (int i = 0; i < 3; i++) {
                    row.createCell(cellIndex++).setCellValue("");
                }
            }

            // Другие параметры
            int userId = (int) serverData.getOrDefault("user", 0);
            row.createCell(cellIndex++).setCellValue(userId);
            // Используем реальный email, если он есть
            String email = userIdToEmail.getOrDefault(userId, "unknown");
            row.createCell(cellIndex++).setCellValue(email);
            row.createCell(cellIndex++).setCellValue((int) serverData.getOrDefault("node", 0));
            row.createCell(cellIndex++).setCellValue((int) serverData.getOrDefault("allocation", 0));
            row.createCell(cellIndex++).setCellValue((int) serverData.getOrDefault("nest", 0));
            row.createCell(cellIndex++).setCellValue((int) serverData.getOrDefault("egg", 0));

            // Контейнер
            Map<String, Object> container = (Map<String, Object>) serverData.get("container");
            if (container != null) {
                row.createCell(cellIndex++).setCellValue((String) container.getOrDefault("startup_command", ""));
                row.createCell(cellIndex++).setCellValue((String) container.getOrDefault("image", ""));
            } else {
                row.createCell(cellIndex++).setCellValue("");
                row.createCell(cellIndex++).setCellValue("");
            }

            row.createCell(cellIndex++).setCellValue((String) serverData.getOrDefault("created_at", ""));
            row.createCell(cellIndex++).setCellValue((String) serverData.getOrDefault("updated_at", ""));
            row.createCell(cellIndex++).setCellValue(((boolean) serverData.getOrDefault("suspended", false)) ? "Yes" : "No");
        }

        // Дополнительное задание: Группировка серверов по аккаунту (User)
        int groupStartRow = rowIndex + 2; // оставляем пустую строку между таблицами
        Row groupHeader = sheet.createRow(groupStartRow);
        groupHeader.createCell(0).setCellValue("Account (Email)");
        groupHeader.createCell(1).setCellValue("Servers");

        Map<String, List<String>> accountToServers = new HashMap<>();
        for (Entry<String, Map<String, Object>> entry : servers.entrySet()) {
            Map<String, Object> serverData = entry.getValue();
            int userIdForGrouping = (int) serverData.getOrDefault("user", 0);
            String accountEmail = userIdToEmail.getOrDefault(userIdForGrouping, "unknown");
            String serverName = (String) serverData.getOrDefault("name", "Unnamed");
            accountToServers.computeIfAbsent(accountEmail, k -> new ArrayList<>()).add(serverName);
        }

        int groupRowIndex = groupStartRow + 1;
        for (Entry<String, List<String>> groupEntry : accountToServers.entrySet()) {
            Row row = sheet.createRow(groupRowIndex++);
            row.createCell(0).setCellValue(groupEntry.getKey());
            String serversList = String.join(", ", groupEntry.getValue());
            row.createCell(1).setCellValue(serversList);
        }

        // Сохраняем файл Excel
        try (FileOutputStream fileOut = new FileOutputStream("output.xlsx")) {
            workbook.write(fileOut);
            System.out.println("Файл успешно создан!");
        } catch (IOException e) {
            System.out.println("Ошибка при записи файла!");
            e.printStackTrace();
        } finally {
            try {
                workbook.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
