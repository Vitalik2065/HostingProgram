package com.example.giantprojekt.configs;

public class ExcelConfig {
    public static final String[] HEADERS = {
            "UUID",               // 0
            "ID",                 // 1
            "Name",               // 2
            "Description",        // 3
            "Memory",             // 4
            "Swap",               // 5
            "Disk",               // 6
            "IO",                 // 7
            "CPU",                // 8
            "Databases",          // 9
            "Allocations",        // 10
            "Backups",            // 11
            "User ID",            // 12
            "User Email",         // 13 <-- пустой путь
            "Node",               // 14
            "Allocation",         // 15
            "Nest",               // 16
            "Egg",                // 17
            "Startup Command",    // 18
            "Image",              // 19
            "Created At",         // 20
            "Updated At",         // 21
            "Suspended",          // 22
            "Discord ID"          // 23 <-- тоже пустой путь
    };

    public static final String[][] PATHS = {
            {},                                     // UUID
            {"id"},                                 // ID
            {"name"},                               // Name
            {"description"},                        // Description
            {"limits","memory"},                    // Memory
            {"limits","swap"},                      // Swap
            {"limits","disk"},                      // Disk
            {"limits","io"},                        // IO
            {"limits","cpu"},                       // CPU
            {"feature_limits","databases"},         // Databases
            {"feature_limits","allocations"},       // Allocations
            {"feature_limits","backups"},           // Backups
            {"relationships","user","attributes","id"}, // User ID
            {},                                     // User Email (берётся из внешней Map)
            {"node"},                               // Node
            {"allocation"},                         // Allocation
            {"nest"},                               // Nest
            {"egg"},                                // Egg
            {"container","startup_command"},        // Startup Command
            {"container","image"},                  // Image
            {"created_at"},                         // Created At
            {"updated_at"},                         // Updated At
            {"suspended"},                          // Suspended
            {}                                      // Discord ID (берётся вручную)
    };
}
