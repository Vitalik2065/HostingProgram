package com.example.giantprojekt.service;


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
public class AddDisIdByEmail {

    private static final String EXCEL_RESOURCE = ResourceBundle
            .getBundle("application")
            .getString("excel.file.name");

    private static final int DISCORD_COLUMN_INDEX = 23;


    public void AddEmailToAll(String email, String discordID) throws IOException, URISyntaxException {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(EXCEL_RESOURCE)) {
            if (is == null) {
                throw new FileNotFoundException("Ресурс не найден: " + EXCEL_RESOURCE);
            }

            Workbook workbook = WorkbookFactory.create(is);
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                Cell emailCell = row.getCell(13);
                if (emailCell != null && email.equals(emailCell.getStringCellValue())) {
                    Cell discordCell = row.getCell(DISCORD_COLUMN_INDEX);
                    if (discordCell == null) {
                        discordCell = row.createCell(DISCORD_COLUMN_INDEX);
                    }
                    discordCell.setCellValue(discordID);
                }
            }


            URL resourseUrl = getClass().getClassLoader().getResource(EXCEL_RESOURCE);
            if (resourseUrl == null) {
                throw new FileNotFoundException("Ресурс не найден при сохранении: " + EXCEL_RESOURCE);
            }

            URI uri = resourseUrl.toURI();
            Path path = Paths.get(uri);


            try (OutputStream os = Files.newOutputStream(path)) {
                workbook.write(os);
            } finally {
                workbook.close();
            }
        }



    }
}