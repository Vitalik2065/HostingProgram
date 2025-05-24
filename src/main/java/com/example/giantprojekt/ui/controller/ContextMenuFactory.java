package com.example.giantprojekt.ui.controller;

import com.example.giantprojekt.model.commands.CommandRegistry;
import com.example.giantprojekt.model.commands.ServerCommand;
import com.example.giantprojekt.model.commands.SuspendCommand;
import com.example.giantprojekt.model.commands.UnsuspendCommand;
import com.example.giantprojekt.model.ServerInfo;
import com.example.giantprojekt.service.api.ApiException;
import javafx.scene.control.*;

public final class ContextMenuFactory {

    private ContextMenuFactory() {}

    /** Привязывает контекстное меню к TableView. */
    public static void attach(TableView<ServerInfo> tv, Runnable refresh) {

        tv.setRowFactory(table -> {
            TableRow<ServerInfo> row = new TableRow<>();

            row.setOnContextMenuRequested(ev -> {
                if (row.isEmpty()) return;

                ServerInfo info = row.getItem();
                boolean suspended = isSuspended(info.getSuspended());

                ContextMenu menu = new ContextMenu();
                for (ServerCommand cmd : CommandRegistry.getAll()) {

                    /* Показываем только релевантное */
                    if (suspended   && cmd instanceof SuspendCommand)     continue;
                    if (!suspended && cmd instanceof UnsuspendCommand)   continue;

                    MenuItem mi = new MenuItem(cmd.getName());
                    mi.setOnAction(a -> {
                        try {
                            cmd.execute(info.getUuid());
                            refresh.run();                  // обновляем таблицу
                        } catch (ApiException ex) {
                            new Alert(Alert.AlertType.ERROR,
                                    ex.getMessage(),
                                    ButtonType.OK).showAndWait();
                        }
                    });
                    menu.getItems().add(mi);
                }
                if (!menu.getItems().isEmpty())
                    menu.show(row, ev.getScreenX(), ev.getScreenY());
            });
            return row;
        });
    }

    /** Универсальное определение: сервер приостановлен? */
    private static boolean isSuspended(String val) {
        if (val == null) return false;
        String v = val.trim().toLowerCase();
        return switch (v) {
            case "true", "yes", "1", "suspended" -> true;
            default -> false;                  // "false","no","0",""
        };
    }
}
