package com.example.giantprojekt.model.commands;

import java.util.List;

/** Реестр всех команд, доступных в принципе. */
public final class CommandRegistry {

    private static final List<ServerCommand> COMMANDS = List.of(
            new SuspendCommand(),
            new UnsuspendCommand()
    );

    private CommandRegistry() {}

    public static List<ServerCommand> getAll() { return COMMANDS; }
}