package com.example.giantprojekt.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.*;
import java.util.ResourceBundle;

public class DiscordAssignmentHandler {
    private static String EXCEL_RESOURCE = ResourceBundle
            .getBundle("application")
            .getString("exel.file.name");

    private static final int DISCORD_COLUMN_INDEX = 24;


    public void assignDiscordIdToServerRow(String uuid, String discordId) throws IOException {
        try (InputStream is = getClass()
                .getClassLoader()
                .getResourceAsStream(EXCEL_RESOURCE)) {

            if (is == null) {
                throw new IllegalArgumentException("Ресурс не найден: " + EXCEL_RESOURCE);
            }

            Workbook workbook = WorkbookFactory.create(is);
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                Cell uuidCell = row.getCell(0);
                if (uuidCell != null && uuid.equals(uuidCell.getStringCellValue())) {
                    Cell discordCell = row.getCell(DISCORD_COLUMN_INDEX);
                    if (discordCell == null) {
                        discordCell = row.createCell(DISCORD_COLUMN_INDEX);
                    }
                    discordCell.setCellValue(discordId);
                    break;  // UUID уникален, выходим из цикла
                }
            }

            // Сохраняем изменения обратно на диск
            String path = getClass()
                    .getClassLoader()
                    .getResource(EXCEL_RESOURCE)
                    .getPath();
            try (OutputStream os = new FileOutputStream(path)) {
                workbook.write(os);
            }
            workbook.close();
        }
    }


}
