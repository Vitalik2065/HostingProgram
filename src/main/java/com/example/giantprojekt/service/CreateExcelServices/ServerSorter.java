package com.example.giantprojekt.service.CreateExcelServices;

import java.util.*;
import java.util.Map.Entry;


public class ServerSorter {

    public static List<Entry<String, Map<String, Object>>> sortByUserId(
            Map<String, Map<String, Object>> servers
    ) {
        List<Entry<String, Map<String, Object>>> entries = new ArrayList<>(servers.entrySet());
        entries.sort(Comparator.comparingInt(e ->
                (int) e.getValue().getOrDefault("user", 0)
        ));
        return entries;
    }
}
