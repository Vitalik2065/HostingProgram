package com.example.giantprojekt.service.loader;

import com.example.giantprojekt.cache.ServerCache;
import com.example.giantprojekt.model.ServerInfo;
import com.example.giantprojekt.service.CreateExcelServices.DataExtractor;
import com.example.giantprojekt.service.CreateExcelServices.ExcelExporter;
import com.example.giantprojekt.service.CreateExcelServices.GettingInfoFromPanel.PanelApiClient;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.*;
import java.util.*;

public final class ServerLoader {

    private ServerLoader() {}

    private static int IDX_UUID, IDX_USER_ID, IDX_USER_EMAIL, IDX_DISCORD;

    public static List<ServerInfo> load() throws Exception {

        /* ---------- 1. Данные из API ---------- */
        @SuppressWarnings("unchecked")
        Map<String, Map<String, Object>> rawServers = PanelApiClient.fetchAllServers();
        Map<Integer, String> apiUserEmails          = PanelApiClient.fetchAllUsers();

        /* ---------- 2. Путь к внешнему файлу ---------- */
        String excelName = ResourceBundle.getBundle("application")
                .getString("excel.file.name");   // output.xlsx
        Path excelPath = Paths.get(System.getProperty("user.dir"), excelName);

        /* ---------- 3. Если файла нет – создаём полностью через ExcelExporter ---------- */
        if (Files.notExists(excelPath)) {
            ExcelExporter.export(rawServers, apiUserEmails, excelPath.toString());
        }

        /* ---------- 4. Чистим «мертвые» UUID ---------- */
        cleanupExcel(excelPath, rawServers.keySet());

        /* ---------- 5. Запоминаем индексы колонок ---------- */
        initColumnIndices(excelPath);

        /* ---------- 6. Читаем Excel и собираем ServerInfo ---------- */
        List<ServerInfo> result = new ArrayList<>();

        try (InputStream in = Files.newInputStream(excelPath);
             Workbook wb    = WorkbookFactory.create(in))
        {
            Sheet sheet = wb.getSheetAt(0);

            for (int r = 1; r <= sheet.getLastRowNum(); r++) {
                Row row = sheet.getRow(r);
                if (row == null) continue;

                String uuid = cellAsString(row.getCell(IDX_UUID));
                if (uuid.isEmpty()) continue;

                Map<String, Object> data = rawServers.get(uuid);
                if (data == null) continue;          // строка в Excel, но сервера уже нет в API

                String userIdStr  = cellAsString(row.getCell(IDX_USER_ID));
                String userEmail  = cellAsString(row.getCell(IDX_USER_EMAIL));
                String discordId  = cellAsString(row.getCell(IDX_DISCORD));

                /* ---- формируем объект ---- */
                ServerInfo s = new ServerInfo();

                int serverId = Integer.parseInt(String.valueOf(DataExtractor.getValue(data, "id")));
                s.setId(String.valueOf(serverId));
                s.setUuid(uuid);
                ServerCache.put(uuid, serverId);

                s.setName        (String.valueOf(DataExtractor.getValue(data, "name")));
                s.setDescription (String.valueOf(DataExtractor.getValue(data, "description")));
                s.setMemory      (String.valueOf(DataExtractor.getValue(data, "limits", "memory")));
                s.setSwap        (String.valueOf(DataExtractor.getValue(data, "limits", "swap")));
                s.setDisk        (String.valueOf(DataExtractor.getValue(data, "limits", "disk")));
                s.setIo          (String.valueOf(DataExtractor.getValue(data, "limits", "io")));
                s.setCpu         (String.valueOf(DataExtractor.getValue(data, "limits", "cpu")));
                s.setDatabases   (String.valueOf(DataExtractor.getValue(data, "feature_limits", "databases")));
                s.setAllocations (String.valueOf(DataExtractor.getValue(data, "feature_limits", "allocations")));
                s.setBackups     (String.valueOf(DataExtractor.getValue(data, "feature_limits", "backups")));
                s.setStartup     (String.valueOf(DataExtractor.getValue(data, "container", "startup_command")));
                s.setImage       (String.valueOf(DataExtractor.getValue(data, "container", "image")));
                s.setNode        (String.valueOf(DataExtractor.getValue(data, "node")));
                s.setAllocation  (String.valueOf(DataExtractor.getValue(data, "allocation")));
                s.setNest        (String.valueOf(DataExtractor.getValue(data, "nest")));
                s.setEgg         (String.valueOf(DataExtractor.getValue(data, "egg")));
                s.setCreatedAt   (String.valueOf(DataExtractor.getValue(data, "created_at")));
                s.setUpdatedAt   (String.valueOf(DataExtractor.getValue(data, "updated_at")));
                s.setSuspended   (String.valueOf(DataExtractor.getValue(data, "suspended")));

                /* ---- User ID ---- */
                int userId = 0;
                if (!userIdStr.isBlank()) {
                    try { userId = Integer.parseInt(userIdStr); }
                    catch (NumberFormatException ignored) {}
                } else {
                    Object uObj = DataExtractor.getValue(data,"relationships","user","attributes","id");
                    if (uObj != null) {
                        try { userId = Integer.parseInt(String.valueOf(uObj)); }
                        catch (NumberFormatException ignored) {}
                    }
                }
                s.setUserId(String.valueOf(userId));

                /* ---- User Email ---- */
                if (userEmail.isBlank()) {
                    userEmail = apiUserEmails.getOrDefault(userId, "");
                }
                s.setUserEmail(userEmail);

                /* ---- Discord ID ---- */
                s.setDiscordId(discordId);

                result.add(s);
            }
        }

        return result;
    }

    /* ---------------- helpers ---------------- */

    private static void initColumnIndices(Path excelPath)
            throws IOException, InvalidFormatException
    {
        try (InputStream in = Files.newInputStream(excelPath);
             Workbook wb    = WorkbookFactory.create(in))
        {
            Row header = wb.getSheetAt(0).getRow(0);
            IDX_UUID       = findCol(header, "UUID");
            IDX_USER_ID    = findCol(header, "User ID");
            IDX_USER_EMAIL = findCol(header, "User Email");
            IDX_DISCORD    = findCol(header, "Discord ID");
            if (IDX_DISCORD < 0) IDX_DISCORD = header.getLastCellNum() - 1;
        }
    }

    private static int findCol(Row header, String title) {
        for (Cell c : header) {
            if (title.equals(cellAsString(c))) return c.getColumnIndex();
        }
        return -1;
    }

    private static void cleanupExcel(Path excelPath, Set<String> validUuids)
            throws IOException, InvalidFormatException
    {
        if (Files.notExists(excelPath)) return;

        try (InputStream in = Files.newInputStream(excelPath);
             Workbook wb    = WorkbookFactory.create(in))
        {
            Sheet sheet = wb.getSheetAt(0);
            int colUUID = findCol(sheet.getRow(0), "UUID");
            if (colUUID < 0) return;

            for (int r = sheet.getLastRowNum(); r >= 1; r--) {
                Row row = sheet.getRow(r);
                if (row == null) continue;
                String uuid = cellAsString(
                        row.getCell(colUUID, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL));
                if (!validUuids.contains(uuid)) {
                    sheet.removeRow(row);
                    sheet.shiftRows(r + 1, sheet.getLastRowNum(), -1);
                }
            }
            try (OutputStream out = Files.newOutputStream(excelPath,
                    StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING))
            {
                wb.write(out);
            }
        }
    }

    /** Универсальное чтение значения ячейки как строки. */
    private static String cellAsString(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:   return cell.getStringCellValue().trim();
            case BOOLEAN:  return Boolean.toString(cell.getBooleanCellValue());
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell))
                    return cell.getDateCellValue().toString();
                double d = cell.getNumericCellValue();
                long   l = (long) d;
                return d == l ? Long.toString(l) : Double.toString(d);
            case FORMULA:
                try { return cell.getStringCellValue().trim(); }
                catch (IllegalStateException e) {
                    double dv = cell.getNumericCellValue();
                    long   lv = (long) dv;
                    return dv == lv ? Long.toString(lv) : Double.toString(dv);
                }
            default: return "";
        }
    }
}
