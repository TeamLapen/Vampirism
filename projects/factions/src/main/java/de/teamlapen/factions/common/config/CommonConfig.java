package de.teamlapen.factions.common.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class CommonConfig {

    public final ModConfigSpec.BooleanValue enableFactionLogging;

    public CommonConfig(ModConfigSpec.Builder builder) {
        this.enableFactionLogging = builder.comment("Enable a custom vampirism log file that logs specific faction actions").gameRestart().define("enableFactionLogging", false);

    }
}
