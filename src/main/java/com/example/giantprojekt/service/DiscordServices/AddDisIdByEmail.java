package com.example.giantprojekt.service.DiscordServices;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.*;
import java.util.ResourceBundle;

public class AddDisIdByEmail {

    // берём "output.xlsx" из application.properties
    private static final String EXCEL_FILE_NAME = ResourceBundle
            .getBundle("application")
            .getString("excel.file.name");

    // строим путь: <текущая дерриктория>/<output.xlsx>
    private static final Path EXCEL_PATH = Paths.get(
            System.getProperty("user.dir"),
            EXCEL_FILE_NAME
    );

    // Индексы колонок: email в 13-й, Discord ID в 23-й
    private static final int EMAIL_COLUMN_INDEX   = 13;
    private static final int DISCORD_COLUMN_INDEX = 23;

    public void AddEmailToAll(String email, String discordID)
            throws IOException, InvalidFormatException
    {
        // 1) Открываем реальный Excel-файл с диска
        try (InputStream in = Files.newInputStream(EXCEL_PATH);
             Workbook wb = WorkbookFactory.create(in))
        {
            Sheet sheet = wb.getSheetAt(0);

            // 2) Для каждой строки: если email совпал — пишем Discord ID
            for (Row row : sheet) {
                Cell emailCell = row.getCell(EMAIL_COLUMN_INDEX);
                if (emailCell != null
                        && email.equals(emailCell.getStringCellValue()))
                {
                    Cell discordCell = row.getCell(
                            DISCORD_COLUMN_INDEX,
                            Row.MissingCellPolicy.CREATE_NULL_AS_BLANK
                    );
                    discordCell.setCellValue(discordID);
                }
            }

            // 3) Сохраняем изменения **обратно в тот же файл**
            try (OutputStream out = Files.newOutputStream(
                    EXCEL_PATH,
                    StandardOpenOption.WRITE,
                    StandardOpenOption.TRUNCATE_EXISTING))
            {
                wb.write(out);
            }
        }
    }
}
