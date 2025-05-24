package com.example.giantprojekt.model.commands;

import com.example.giantprojekt.cache.ServerCache;
import com.example.giantprojekt.service.api.ApiException;
import com.example.giantprojekt.service.api.PterodactylApiService;

/** Снять приостановку с сервера. */
public class UnsuspendCommand implements ServerCommand {

    private final PterodactylApiService api = PterodactylApiService.getInstance();

    @Override public String getName() { return "Unsuspend"; }

    @Override
    public void execute(String serverUuid) throws ApiException {
        int id = ServerCache.uuidToId(serverUuid);
        if (id < 0) throw new ApiException("ID for uuid " + serverUuid + " not found");
        api.resumeServer(id);
    }
}
