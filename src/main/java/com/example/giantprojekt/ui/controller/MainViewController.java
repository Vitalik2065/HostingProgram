package com.example.giantprojekt.ui.controller;

import com.example.giantprojekt.cache.ServerCache;
import com.example.giantprojekt.model.commands.CommandRegistry;
import com.example.giantprojekt.model.commands.ServerCommand;
import com.example.giantprojekt.model.ServerInfo;
import com.example.giantprojekt.service.loader.ServerLoader;
import com.example.giantprojekt.service.api.ApiException;
import com.example.giantprojekt.ui.util.TableConfigurator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination.ModifierValue;
import javafx.scene.input.KeyCombination.Modifier;
import javafx.scene.input.KeyCombination.ModifierValue;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCode;
import javafx.scene.control.TablePosition;
import javafx.scene.input.KeyEvent;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class MainViewController implements Initializable {

    /* ---------- FXML ---------- */
    @FXML private TextField filterField;
    @FXML private TableView<ServerInfo> serversTable;
    @FXML private Label statusLabel;

    /* ---------- данные ---------- */
    private final ObservableList<ServerInfo> master  = FXCollections.observableArrayList();
    private final FilteredList<ServerInfo>   filtered = new FilteredList<>(master, s -> true);

    /* ---------- инициализация ---------- */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        /* колонки формируем программно */
        TableConfigurator.configure(serversTable);
        serversTable.setItems(filtered);

        /* поиск */
        filterField.textProperty().addListener((o,old,v) ->
                filtered.setPredicate(s -> v==null || v.isBlank()
                        || s.getUuid().toLowerCase().contains(v.toLowerCase().trim())
                        || s.getName().toLowerCase().contains(v.toLowerCase().trim()))
        );
        filterField.setOnKeyPressed(e -> { if (e.getCode()== KeyCode.ESCAPE) filterField.clear(); });

        /* выбор ячеек и копирование */
        enableCopyToClipboard(serversTable);

        /* контекстное меню Suspend / Unsuspend */
        attachContextMenu();

        /* первая загрузка */
        refresh();
    }

    /* ---------- кнопка Refresh ---------- */
    @FXML private void onRefresh(ActionEvent e) { refresh(); }

    /* ---------- обновление таблицы ---------- */
    private void refresh() {
        try {
            List<ServerInfo> list = ServerLoader.load();
            master.setAll(list);
            statusLabel.setText("Loaded " + list.size() + " servers");
        } catch (Exception ex) {
            ex.printStackTrace();
            statusLabel.setText("Error: " + ex.getMessage());
        }
    }

    /* ---------- контекстное меню ---------- */
    private void attachContextMenu() {
        serversTable.setRowFactory(tv -> {
            TableRow<ServerInfo> row = new TableRow<>();
            row.setOnContextMenuRequested(ev -> {
                if (row.isEmpty()) return;

                ServerInfo s = row.getItem();
                boolean suspended = switch (s.getSuspended().toLowerCase()) {
                    case "true","yes","1","suspended" -> true;
                    default -> false;
                };

                ContextMenu menu = new ContextMenu();
                for (ServerCommand cmd : CommandRegistry.getAll()) {
                    boolean isSuspend   = cmd.getClass().getSimpleName().startsWith("Suspend");
                    boolean isUnsuspend = cmd.getClass().getSimpleName().startsWith("Unsuspend");
                    if (suspended && isSuspend)     continue;
                    if (!suspended && isUnsuspend)  continue;

                    MenuItem mi = new MenuItem(cmd.getName());
                    mi.setOnAction(a -> {
                        try {
                            cmd.execute(s.getUuid());
                            refresh();
                        } catch (ApiException ex) {
                            new Alert(Alert.AlertType.ERROR, ex.getMessage(), ButtonType.OK).showAndWait();
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

    /* ---------- копирование Ctrl+C ---------- */
    private static void enableCopyToClipboard(TableView<ServerInfo> tv) {

        tv.getSelectionModel().setCellSelectionEnabled(true);

        tv.addEventHandler(KeyEvent.KEY_PRESSED, ev -> {
            if (new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN).match(ev)) {

                String text = tv.getSelectionModel().getSelectedCells().stream()
                        .map(TablePosition::getTableColumn)
                        .distinct()
                        .map(col -> {
                            int row = tv.getSelectionModel().getSelectedCells().get(0).getRow();
                            Object v = col.getCellData(row);
                            return v != null ? v.toString() : "";
                        })
                        .collect(Collectors.joining(System.lineSeparator()));

                ClipboardContent cc = new ClipboardContent();
                cc.putString(text);
                Clipboard.getSystemClipboard().setContent(cc);
                ev.consume();
            }
        });
    }
}
