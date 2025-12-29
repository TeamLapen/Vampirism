package de.teamlapen.factions.client.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ClientConfig {

    public final ModConfigSpec.BooleanValue renderTotemFactionName;

    public final ModConfigSpec.BooleanValue disableHudActionCooldownRendering;
    public final ModConfigSpec.BooleanValue disableHudActionDurationRendering;
    public final ModConfigSpec.BooleanValue enableFactionLevelOverlayRendering;

    public final ModConfigSpec.IntValue guiLevelOffsetX;
    public final ModConfigSpec.IntValue guiLevelOffsetY;

    public final ModConfigSpec.IntValue overrideGuiSkillButtonX;
    public final ModConfigSpec.IntValue overrideGuiSkillButtonY;
    public final ModConfigSpec.BooleanValue guiSkillButton;

    public final ModConfigSpec.ConfigValue<String> actionOrder;
    public final ModConfigSpec.ConfigValue<String> minionTaskOrder;
    public final ModConfigSpec.BooleanValue enableVillageRaidOverlayRendering;



    public ClientConfig(ModConfigSpec.Builder builder) {
        this.renderTotemFactionName = builder.comment("Render the faction name of totems").define("renderTotemFactionName", true);
        this.enableVillageRaidOverlayRendering = builder.comment("Disable the rendering of the village raid overlay in the HUD").define("enableVillageRaidOverlayRendering", true);

        this.guiLevelOffsetX = builder.comment("X-Offset of the level indicator from the center in pixels").defineInRange("levelOffsetX", 0, -250, 250);
        this.guiLevelOffsetY = builder.comment("Y-Offset of the level indicator from the bottom in pixels").defineInRange("levelOffsetY", 47, 0, 270);
        this.guiSkillButton = builder.comment("Render skill menu button in inventory").define("skillButtonEnable", true);
        this.overrideGuiSkillButtonX = builder.comment("Force the guiSkillButton to the following x position from the center of the inventory, default value is 125").defineInRange("overrideGuiSkillButtonX", 125, Integer.MIN_VALUE, Integer.MAX_VALUE);
        this.overrideGuiSkillButtonY = builder.comment("Force the guiSkillButton to the following y position from the center of the inventory, default value is -22").defineInRange("overrideGuiSkillButtonY", -22, Integer.MIN_VALUE, Integer.MAX_VALUE);

        this.disableHudActionCooldownRendering = builder.comment("Disable the rendering of the action cooldowns in the HUD").define("disableHudActionCooldownRendering", false);
        this.disableHudActionDurationRendering = builder.comment("Disable the rendering of the action durations in the HUD").define("disableHudActionDurationRendering", false);

        this.enableFactionLevelOverlayRendering = builder.comment("Disable the rendering of the faction level overlay in the HUD").define("enableFactionLevelOverlayRendering", true);

        this.actionOrder = builder.comment("Action ordering").define("actionOrder", "", ClientConfigHelper::testActions);
        this.minionTaskOrder = builder.comment("Minion task ordering").define("minionTaskOrder", "", ClientConfigHelper::testTasks);
    }
}
