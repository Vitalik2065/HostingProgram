package com.example.giantprojekt.ui.util;

import com.example.giantprojekt.model.ServerInfo;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * Заполняет TableView<ServerInfo> всеми нужными колонками в нужном порядке.
 */
public final class TableConfigurator {

    private TableConfigurator() {}

    public static void configure(TableView<ServerInfo> tv) {
        tv.getColumns().clear();

        // основные поля
        add(tv, "uuid",        200, "UUID");
        add(tv, "id",           50,  "ID");
        add(tv, "name",        150,  "Name");
        add(tv, "description", 200,  "Description");
        add(tv, "memory",       80,  "Memory");
        add(tv, "swap",         80,  "Swap");
        add(tv, "disk",         80,  "Disk");
        add(tv, "io",           60,  "IO");
        add(tv, "cpu",          60,  "CPU");
        add(tv, "databases",    80,  "Databases");
        add(tv, "allocations",  80,  "Allocations");
        add(tv, "backups",      80,  "Backups");

        // пользовательские поля
        add(tv, "userId",       80,  "User ID");
        add(tv, "userEmail",   150,  "User Email");



        // остальное
        add(tv, "node",        100,  "Node");
        add(tv, "allocation",  100,  "Allocation");
        add(tv, "nest",        100,  "Nest");
        add(tv, "egg",         100,  "Egg");
        add(tv, "startup",     300,  "Startup Command");
        add(tv, "image",       200,  "Image");
        add(tv, "createdAt",   180,  "Created At");
        add(tv, "updatedAt",   180,  "Updated At");
        add(tv, "suspended",    80,  "Suspended");
        add(tv, "discordId",   120, "Discord ID");
    }

    private static void add(TableView<ServerInfo> tv,
                            String prop,
                            double prefWidth,
                            String title) {
        TableColumn<ServerInfo, String> col = new TableColumn<>(title);
        col.setPrefWidth(prefWidth);
        col.setCellValueFactory(new PropertyValueFactory<>(prop));
        tv.getColumns().add(col);
    }
}
