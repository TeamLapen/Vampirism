package de.teamlapen.vampirism.config;

import de.teamlapen.vampirism.client.ClientConfigHelper;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.NotNull;

/**
 * Client only configuration
 */
public class ClientConfig {

    // Entity rendering
    public final ModConfigSpec.BooleanValue renderAdvancedMobPlayerFaces;
    public final ModConfigSpec.BooleanValue renderVampireEyes;

    // World rendering
    public final ModConfigSpec.BooleanValue renderVampireForestFog;

    // Overlay rendering
    public final ModConfigSpec.BooleanValue renderScreenOverlay;
    public final ModConfigSpec.BooleanValue disableHudActionCooldownRendering;
    public final ModConfigSpec.BooleanValue disableHudActionDurationRendering;
    public final ModConfigSpec.BooleanValue enableHudBatOverlayRendering;
    public final ModConfigSpec.BooleanValue enableVillageRaidOverlayRendering;
    public final ModConfigSpec.BooleanValue enableDisguiseOverlayRendering;
    public final ModConfigSpec.BooleanValue enableFactionLevelOverlayRendering;
    public final ModConfigSpec.IntValue guiLevelOffsetX;
    public final ModConfigSpec.IntValue guiLevelOffsetY;
    public final ModConfigSpec.BooleanValue enableNearbyVampireOverlayRendering;
    public final ModConfigSpec.BooleanValue enableRageOverlayRendering;
    public final ModConfigSpec.BooleanValue enableSunOverlayRendering;

    // Gui rendering
    public final ModConfigSpec.IntValue overrideGuiSkillButtonX;
    public final ModConfigSpec.IntValue overrideGuiSkillButtonY;
    public final ModConfigSpec.BooleanValue guiSkillButton;
    public final ModConfigSpec.BooleanValue disableFovChange;
    public final ModConfigSpec.BooleanValue disableBloodVisionRendering;


    // Internal
    public final ModConfigSpec.ConfigValue<String> actionOrder;
    public final ModConfigSpec.ConfigValue<String> minionTaskOrder;

    ClientConfig(ModConfigSpec.@NotNull Builder builder) {
        builder.comment("Client configuration settings").push("client");
        builder.comment("Configure rendering").push("render");
        this.renderAdvancedMobPlayerFaces = builder.comment("Render player faces on advanced hunter or vampires").define("advancedMobPlayerFaces", true);
        this.renderVampireEyes = builder.comment("Render vampire eye/fang face overlay").define("vampireEyes", true);
        this.renderVampireForestFog = builder.comment("Render fog in vampire biome. Might be enforced server side").define("vampireForestFog", true);
        this.renderScreenOverlay = builder.comment("Render full screen colored overlays for effects like vampire levelup").define("renderScreenOverlay", true);
        builder.pop();

        builder.comment("Configure GUI").push("gui");
        this.guiLevelOffsetX = builder.comment("X-Offset of the level indicator from the center in pixels").defineInRange("levelOffsetX", 0, -250, 250);
        this.guiLevelOffsetY = builder.comment("Y-Offset of the level indicator from the bottom in pixels").defineInRange("levelOffsetY", 47, 0, 270);
        this.guiSkillButton = builder.comment("Render skill menu button in inventory").define("skillButtonEnable", true);
        this.overrideGuiSkillButtonX = builder.comment("Force the guiSkillButton to the following x position from the center of the inventory, default value is 125").defineInRange("overrideGuiSkillButtonX", 125, Integer.MIN_VALUE, Integer.MAX_VALUE);
        this.overrideGuiSkillButtonY = builder.comment("Force the guiSkillButton to the following y position from the center of the inventory, default value is -22").defineInRange("overrideGuiSkillButtonY", -22, Integer.MIN_VALUE, Integer.MAX_VALUE);
        this.disableFovChange = builder.comment("Disable the FOV change caused by the speed buf for vampire players").define("disableFovChange", false);
        this.disableBloodVisionRendering = builder.comment("Disable the effect of blood vision. It can still be unlocked and activated but does not have any effect").define("disableBloodVisionRendering", false);
        this.disableHudActionCooldownRendering = builder.comment("Disable the rendering of the action cooldowns in the HUD").define("disableHudActionCooldownRendering", false);
        this.disableHudActionDurationRendering = builder.comment("Disable the rendering of the action durations in the HUD").define("disableHudActionDurationRendering", false);
        builder.pop();


        builder.comment("Overlay rendering").push("overlay");

        this.enableHudBatOverlayRendering = builder.comment("Disable the rendering of the bat overlay in the HUD").define("enableHudBatOverlayRendering", true);
        this.enableVillageRaidOverlayRendering = builder.comment("Disable the rendering of the village raid overlay in the HUD").define("enableVillageRaidOverlayRendering", true);
        this.enableDisguiseOverlayRendering = builder.comment("Disable the rendering of the disguise overlay in the HUD").define("enableDisguiseOverlayRendering", true);
        this.enableFactionLevelOverlayRendering = builder.comment("Disable the rendering of the faction level overlay in the HUD").define("enableFactionLevelOverlayRendering", true);
        this.enableNearbyVampireOverlayRendering = builder.comment("Disable the rendering of the nearby vampire overlay in the HUD").define("enableNearbyVampireOverlayRendering", true);
        this.enableRageOverlayRendering = builder.comment("Disable the rendering of the rage overlay in the HUD").define("enableRageOverlayRendering", true);
        this.enableSunOverlayRendering = builder.comment("Disable the rendering of the sun overlay in the HUD").define("enableSunOverlayRendering", true);
        builder.pop();

        builder.push("internal");
        this.actionOrder = builder.comment("Action ordering").define("actionOrder", "", ClientConfigHelper::testActions);
        this.minionTaskOrder = builder.comment("Minion task ordering").define("minionTaskOrder", "", ClientConfigHelper::testTasks);

        builder.pop();
        builder.pop();

    }

}
