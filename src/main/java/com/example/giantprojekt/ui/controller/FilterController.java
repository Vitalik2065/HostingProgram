package com.example.giantprojekt.ui.controller;

import com.example.giantprojekt.model.ServerInfo;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.TextField;

import java.util.function.Predicate;

/** Отвечает за поиск по UUID/Name. */
public final class FilterController {

    private FilterController() {}

    public static void bind(TextField field,
                            FilteredList<ServerInfo> filtered) {

        field.textProperty().addListener((o,old,v) ->
                filtered.setPredicate(build(v)));

        // esc – очистить
        field.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case ESCAPE -> field.clear();
            }
        });
    }

    public static Predicate<ServerInfo> build(String text) {
        String t = text == null ? "" : text.trim().toLowerCase();
        if (t.isEmpty()) return s -> true;
        return s -> s.getUuid().toLowerCase().contains(t)
                || s.getName().toLowerCase().contains(t);
    }
}
