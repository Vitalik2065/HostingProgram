package com.example.giantprojekt.configs;

/**
 * Константы для формирования Excel-таблицы серверов:
 * заголовки столбцов и пути к данным.
 */
public class ExcelConfig {
    public static final String[] HEADERS = {
            "UUID", "ID", "Name", "Description",
            "Memory", "Swap", "Disk", "IO", "CPU",
            "Databases", "Allocations", "Backups",
            "User ID", "User Email", "Node",
            "Allocation", "Nest", "Egg",
            "Startup Command", "Image",
            "Created At", "Updated At", "Suspended"
    };

    public static final String[][] PATHS = {
            {},                     // UUID (entry.getKey)
            {"id"},                 // ID
            {"name"},               // Name
            {"description"},        // Description
            {"limits", "memory"},
            {"limits", "swap"},
            {"limits", "disk"},
            {"limits", "io"},
            {"limits", "cpu"},
            {"feature_limits", "databases"},
            {"feature_limits", "allocations"},
            {"feature_limits", "backups"},
            {"user"},               // User ID
            {},                     // User Email (спецобработка)
            {"node"},
            {"allocation"},
            {"nest"},
            {"egg"},
            {"container", "startup_command"},
            {"container", "image"},
            {"created_at"},
            {"updated_at"},
            {"suspended"}
    };
}
