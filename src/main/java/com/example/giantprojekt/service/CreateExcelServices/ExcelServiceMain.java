package com.example.giantprojekt.service.CreateExcelServices;

import com.example.giantprojekt.service.CreateExcelServices.GettingInfoFromPanel.PanelApiClient;
import com.example.giantprojekt.service.CreateExcelServices.ExcelExporter;

import java.io.IOException;
import java.util.Map;

public class ExcelServiceMain {
    public static void main(String[] args) {
        // 1) Забираем данные с панели
        Map<String, Map<String, Object>> servers;
        Map<Integer, String>             userIdToEmail;
        try {
            servers       = PanelApiClient.fetchAllServers();
            userIdToEmail = PanelApiClient.fetchAllUsers();
        } catch (Exception e) {
            System.err.println("Ошибка получения данных: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        // 2) Куда сохранять
        String output = args.length > 0 ? args[0] : "output.xlsx";

        // 3) Экспортируем в Excel
        try {
            ExcelExporter.export(servers, userIdToEmail, output);
            System.out.println("Готово: " + output);
        } catch (IOException ex) {
            System.err.println("Ошибка записи Excel-файла: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}