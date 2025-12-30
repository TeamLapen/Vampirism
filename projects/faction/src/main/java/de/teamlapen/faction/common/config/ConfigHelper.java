package de.teamlapen.faction.common.config;

public class ConfigHelper {
    private final FactionConfig factionConfig;

    public ConfigHelper(FactionConfig factionConfig) {
        this.factionConfig = factionConfig;
    }

    public boolean preventRenderingDebugBoundingBoxes() {
        return this.factionConfig.serverConfig().isLoaded() && this.factionConfig.serverConfig().config().preventRenderingDebugBoundingBoxes.get();
    }
}
