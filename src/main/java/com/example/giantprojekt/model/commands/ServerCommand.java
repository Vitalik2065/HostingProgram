package com.example.giantprojekt.model.commands;


import com.example.giantprojekt.service.api.ApiException;

/** Любое действие, применимое к серверу. */
public interface ServerCommand {
    String getName();
    void execute(String serverUuid) throws ApiException;
}