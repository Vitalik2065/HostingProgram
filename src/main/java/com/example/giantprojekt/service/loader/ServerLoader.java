package com.example.giantprojekt.service.loader;

import com.example.giantprojekt.cache.ServerCache;
import com.example.giantprojekt.model.ServerInfo;
import com.example.giantprojekt.service.CreateExcelServices.DataExtractor;
import com.example.giantprojekt.service.CreateExcelServices.GettingInfoFromPanel.PanelApiClient;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** Забирает данные с панели и формирует ServerInfo. */
public final class ServerLoader {

    private ServerLoader() {}

    public static List<ServerInfo> load() throws Exception {
        @SuppressWarnings("unchecked")
        Map<String, Map<String, Object>> raw = PanelApiClient.fetchAllServers();
        Map<Integer, String> emails          = PanelApiClient.fetchAllUsers();

        return raw.entrySet().stream().map(e -> {
            String uuid = e.getKey();
            Map<String, Object> d = e.getValue();
            ServerInfo s = new ServerInfo();

            int id = Integer.parseInt(String.valueOf(DataExtractor.getValue(d, "id")));
            s.setId(String.valueOf(id));
            ServerCache.put(uuid, id);

            s.setUuid(uuid);
            s.setName( String.valueOf(DataExtractor.getValue(d, "name")));
            s.setDescription(String.valueOf(DataExtractor.getValue(d, "description")));

            s.setMemory(String.valueOf(DataExtractor.getValue(d,"limits","memory")));
            s.setSwap(  String.valueOf(DataExtractor.getValue(d,"limits","swap")));
            s.setDisk(  String.valueOf(DataExtractor.getValue(d,"limits","disk")));
            s.setIo(    String.valueOf(DataExtractor.getValue(d,"limits","io")));
            s.setCpu(   String.valueOf(DataExtractor.getValue(d,"limits","cpu")));

            s.setDatabases(  String.valueOf(DataExtractor.getValue(d,"feature_limits","databases")));
            s.setAllocations(String.valueOf(DataExtractor.getValue(d,"feature_limits","allocations")));
            s.setBackups(    String.valueOf(DataExtractor.getValue(d,"feature_limits","backups")));

            s.setUserId(String.valueOf(id));
            s.setUserEmail(emails.getOrDefault(id,""));

            s.setStartup(String.valueOf(DataExtractor.getValue(d,"container","startup_command")));
            s.setImage(  String.valueOf(DataExtractor.getValue(d,"container","image")));

            s.setNode(      String.valueOf(DataExtractor.getValue(d,"node")));
            s.setAllocation(String.valueOf(DataExtractor.getValue(d,"allocation")));
            s.setNest(      String.valueOf(DataExtractor.getValue(d,"nest")));
            s.setEgg(       String.valueOf(DataExtractor.getValue(d,"egg")));
            s.setCreatedAt( String.valueOf(DataExtractor.getValue(d,"created_at")));
            s.setUpdatedAt( String.valueOf(DataExtractor.getValue(d,"updated_at")));
            s.setSuspended( String.valueOf(DataExtractor.getValue(d,"suspended")));

            return s;
        }).collect(Collectors.toList());
    }
}
