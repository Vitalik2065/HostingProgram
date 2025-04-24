package com.example.giantprojekt.service.CreateExcelServices;

import com.example.giantprojekt.configs.ExcelConfig;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class ExcelExporter {
    public static void export(
            Map<String, Map<String, Object>> servers,
            Map<Integer, String>              userEmail,
            String                            path
    ) throws IOException {
        try (Workbook wb = new XSSFWorkbook();
             FileOutputStream fos = new FileOutputStream(path)) {
            Sheet sheet = wb.createSheet("Data");
            Row header = sheet.createRow(0);
            for (int i = 0; i < ExcelConfig.HEADERS.length; i++)
                header.createCell(i).setCellValue(ExcelConfig.HEADERS[i]);

            List<Entry<String, Map<String, Object>>> list = new ArrayList<>(servers.entrySet());
            list.sort(Comparator.comparingInt(e -> ((Number) e.getValue().getOrDefault("user", 0)).intValue()));

            for (int r = 0; r < list.size(); r++) {
                Entry<String, Map<String, Object>> e = list.get(r);
                Row row = sheet.createRow(r + 1);
                for (int c = 0; c < ExcelConfig.HEADERS.length; c++) {
                    Cell cell = row.createCell(c);
                    Object v = c == 0 ? e.getKey()
                            : c == 13 ? userEmail.getOrDefault(
                            ((Number) DataExtractor.getValue(e.getValue(), "user")).intValue(),
                            "unknown")
                            : DataExtractor.getValue(e.getValue(), ExcelConfig.PATHS[c]);
                    if (v instanceof Number)
                        cell.setCellValue(((Number) v).doubleValue());
                    else if (v instanceof Boolean)
                        cell.setCellValue((Boolean) v ? "Yes" : "No");
                    else
                        cell.setCellValue(v.toString());
                }
            }
            wb.write(fos);
        }
    }
}
