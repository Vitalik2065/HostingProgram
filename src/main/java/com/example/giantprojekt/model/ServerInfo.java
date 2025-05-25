// ServerInfo.java
package com.example.giantprojekt.model;

public class ServerInfo {
    private String uuid;
    private String id;
    private String name;
    private String description;
    private String memory;
    private String swap;
    private String disk;
    private String io;
    private String cpu;
    private String databases;
    private String allocations;
    private String backups;
    private String userId;
    private String userEmail;
    private String node;
    private String allocation;
    private String nest;
    private String egg;
    private String startup;
    private String image;
    private String createdAt;
    private String updatedAt;
    private String suspended;

    private String discordId;

    // Геттеры и сеттеры для всех полей:
    public String getUuid() { return uuid; }
    public void setUuid(String uuid) { this.uuid = uuid; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getMemory() { return memory; }
    public void setMemory(String memory) { this.memory = memory; }

    public String getSwap() { return swap; }
    public void setSwap(String swap) { this.swap = swap; }

    public String getDisk() { return disk; }
    public void setDisk(String disk) { this.disk = disk; }

    public String getIo() { return io; }
    public void setIo(String io) { this.io = io; }

    public String getCpu() { return cpu; }
    public void setCpu(String cpu) { this.cpu = cpu; }

    public String getDatabases() { return databases; }
    public void setDatabases(String databases) { this.databases = databases; }

    public String getAllocations() { return allocations; }
    public void setAllocations(String allocations) { this.allocations = allocations; }

    public String getBackups() { return backups; }
    public void setBackups(String backups) { this.backups = backups; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public String getNode() { return node; }
    public void setNode(String node) { this.node = node; }

    public String getAllocation() { return allocation; }
    public void setAllocation(String allocation) { this.allocation = allocation; }

    public String getNest() { return nest; }
    public void setNest(String nest) { this.nest = nest; }

    public String getEgg() { return egg; }
    public void setEgg(String egg) { this.egg = egg; }

    public String getStartup() { return startup; }
    public void setStartup(String startup) { this.startup = startup; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    public String getSuspended() { return suspended; }
    public void setSuspended(String suspended) { this.suspended = suspended; }

    public String getDiscordId() { return discordId; }
    public void setDiscordId(String discordId) {
        this.discordId = discordId;
    }

}
