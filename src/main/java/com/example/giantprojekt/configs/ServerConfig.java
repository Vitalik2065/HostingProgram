package com.example.giantprojekt.configs;

/** Конфиг ключей атрибутов серверов из Pterodactyl. */
public class ServerConfig {
    /** Простые поля в корне "attributes". */
    public static final String[] FLAT_KEYS = {
            "id", "name", "description", "user", "suspended",
            "node", "allocation", "nest", "egg"
    };

    /** Вложенные ключи: [имя_объекта, имя_поля]. */
    public static final String[][] NESTED_KEYS = {
            {"limits",        "memory"},
            {"limits",        "swap"},
            {"limits",        "disk"},
            {"limits",        "io"},
            {"limits",        "cpu"},
            {"feature_limits","databases"},
            {"feature_limits","allocations"},
            {"feature_limits","backups"},
            {"container",     "startup_command"},
            {"container",     "image"}
    };

    /** Даты, которые тоже лежат в корне attributes. */
    public static final String[] DATE_KEYS = {
            "created_at", "updated_at"
    };

    private ServerConfig() {}
}
