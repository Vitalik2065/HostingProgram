package com.example.giantprojekt.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;

public class ExcelService {
    public static void main(String[] args) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Data");

        Row row = sheet.createRow(0);
        row.createCell(0).setCellValue("Пример");
        row.createCell(1).setCellValue(123);

        String filePath = System.getProperty("user.dir") + "/output.xlsx";
        System.out.println("Создаём файл по пути: " + filePath);

        try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
            workbook.write(fileOut);
            System.out.println("✅ Файл успешно записан!");
        } catch (IOException e) {
            System.out.println("❌ Ошибка при записи файла!");
            e.printStackTrace();
        }

        try {
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
