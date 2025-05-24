package com.example.giantprojekt.service.DiscordServices;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;

public class DiscordAssignmentHandler {
    private static final String EXCEL_RESOURCE = ResourceBundle
            .getBundle("application")
            .getString("excel.file.name");

    private static final int DISCORD_COLUMN_INDEX = 23;

    /**
     * Находит строку с данным UUID и записывает в неё discordId.
     * Перезаписывает тот же файл в target/classes.
     */
    public void assignDiscordIdToServerRow(String uuid, String discordId)
            throws IOException, InvalidFormatException, URISyntaxException
    {
        // 1) Открываем шаблон из ресурсов
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(EXCEL_RESOURCE)) {
            if (is == null) {
                throw new FileNotFoundException("Ресурс не найден: " + EXCEL_RESOURCE);
            }
            Workbook workbook = WorkbookFactory.create(is);
            Sheet sheet = workbook.getSheetAt(0);

            // 2) Заполняем ячейку с Discord ID
            for (Row row : sheet) {
                Cell uuidCell = row.getCell(0);
                if (uuidCell != null && uuid.equals(uuidCell.getStringCellValue())) {
                    Cell discordCell = row.getCell(DISCORD_COLUMN_INDEX);
                    if (discordCell == null) {
                        discordCell = row.createCell(DISCORD_COLUMN_INDEX);
                    }
                    discordCell.setCellValue(discordId);
                    break;
                }
            }

            // 3) Находим реальный путь к файлу в target/classes
            URL resourceUrl = getClass().getClassLoader().getResource(EXCEL_RESOURCE);
            if (resourceUrl == null) {
                throw new FileNotFoundException("Ресурс не найден при сохранении: " + EXCEL_RESOURCE);
            }
            // URL → URI → Path (пробелы раскодируются автоматически)
            URI uri = resourceUrl.toURI();
            Path path = Paths.get(uri);

            // 4) Записываем изменения
            //    Используем Files.newOutputStream, чтобы не возиться с FileOutputStream напрямую
            try (OutputStream os = Files.newOutputStream(path)) {
                workbook.write(os);
            } finally {
                workbook.close();
            }
        }
    }
}
