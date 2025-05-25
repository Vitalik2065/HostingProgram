package com.example.giantprojekt.service.DiscordServices;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.*;
import java.util.ResourceBundle;

public class DiscordAssignmentHandler {

    // Берём имя файла из application.properties, например "output.xlsx"
    private static final String EXCEL_FILE_NAME = ResourceBundle
            .getBundle("application")
            .getString("excel.file.name");

    // Индексы колонок: UUID в 0-й, Discord ID в 23-й (поправь при необходимости)
    private static final int UUID_COLUMN_INDEX    = 0;
    private static final int DISCORD_COLUMN_INDEX = 23;

    /**
     * Находит корень проекта (там, где pom.xml/build.gradle) и возвращает путь к output.xlsx
     */
    private Path getExcelPath() {
        Path dir = Paths.get(System.getProperty("user.dir")).toAbsolutePath();
        while (dir != null) {
            if (Files.exists(dir.resolve("pom.xml")) ||
                    Files.exists(dir.resolve("build.gradle"))) {
                return dir.resolve(EXCEL_FILE_NAME);
            }
            dir = dir.getParent();
        }
        // если не нашли pom.xml — вернём просто user.dir/output.xlsx
        return Paths.get(System.getProperty("user.dir"))
                .resolve(EXCEL_FILE_NAME);
    }

    /**
     * Ищет в листе строку с заданным uuid, записывает в неё discordId и сохраняет файл.
     */
    public void assignDiscordIdToServerRow(String uuid, String discordId)
            throws IOException, InvalidFormatException
    {
        Path excelPath = getExcelPath();

        // 1) Открываем существующий Excel-файл
        try (InputStream in = Files.newInputStream(excelPath);
             Workbook wb = WorkbookFactory.create(in))
        {
            Sheet sheet = wb.getSheetAt(0);
            boolean updated = false;

            // 2) Ищем строку с совпадающим UUID и пишем Discord ID
            for (Row row : sheet) {
                Cell uuidCell = row.getCell(UUID_COLUMN_INDEX);
                if (uuidCell != null && uuid.equals(uuidCell.getStringCellValue())) {
                    Cell discordCell = row.getCell(
                            DISCORD_COLUMN_INDEX,
                            Row.MissingCellPolicy.CREATE_NULL_AS_BLANK
                    );
                    discordCell.setCellValue(discordId);
                    updated = true;
                    break;
                }
            }

            if (!updated) {
                System.err.println("[WARN] UUID not found in Excel: " + uuid);
            }

            // 3) Перезаписываем файл на диске тем же путём
            try (OutputStream out = Files.newOutputStream(
                    excelPath,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE))
            {
                wb.write(out);
            }
        }
    }
}
