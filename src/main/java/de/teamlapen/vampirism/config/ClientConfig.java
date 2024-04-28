package de.teamlapen.vampirism.config;

import de.teamlapen.vampirism.client.ClientConfigHelper;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.NotNull;

/**
 * Client only configuration
 */
public class ClientConfig {

    public final ModConfigSpec.IntValue overrideGuiSkillButtonX;
    public final ModConfigSpec.IntValue overrideGuiSkillButtonY;
    public final ModConfigSpec.IntValue guiLevelOffsetX;
    public final ModConfigSpec.IntValue guiLevelOffsetY;
    public final ModConfigSpec.BooleanValue guiSkillButton;
    public final ModConfigSpec.BooleanValue renderAdvancedMobPlayerFaces;
    public final ModConfigSpec.BooleanValue renderVampireEyes;
    public final ModConfigSpec.BooleanValue renderVampireForestFog;
    public final ModConfigSpec.BooleanValue renderScreenOverlay;
    public final ModConfigSpec.BooleanValue disableFovChange;
    public final ModConfigSpec.BooleanValue disableBloodVisionRendering;
    public final ModConfigSpec.BooleanValue disableHudActionCooldownRendering;
    public final ModConfigSpec.BooleanValue disableHudActionDurationRendering;
    public final ModConfigSpec.ConfigValue<String> actionOrder;
    public final ModConfigSpec.ConfigValue<String> minionTaskOrder;

    ClientConfig(ModConfigSpec.@NotNull Builder builder) {
        builder.comment("Client configuration settings")
                .push("client");


        //Rendering
        builder.comment("Configure rendering").push("render");
        renderAdvancedMobPlayerFaces = builder.comment("Render player faces on advanced hunter or vampires").define("advancedMobPlayerFaces", true);
        renderVampireEyes = builder.comment("Render vampire eye/fang face overlay").define("vampireEyes", true);
        renderVampireForestFog = builder.comment("Render fog in vampire biome. Might be enforced server side").define("vampireForestFog", true);
        renderScreenOverlay = builder.comment("Render screen overlay. Don't disable").define("screenOverlay", true);

        builder.pop();

        builder.comment("Configure GUI").push("gui");
        guiLevelOffsetX = builder.comment("X-Offset of the level indicator from the center in pixels").defineInRange("levelOffsetX", 0, -250, 250);
        guiLevelOffsetY = builder.comment("Y-Offset of the level indicator from the bottom in pixels. Must be > 0").defineInRange("levelOffsetY", 47, 0, 270);
        guiSkillButton = builder.comment("Render skill menu button in inventory").define("skillButtonEnable", true);
        overrideGuiSkillButtonX = builder.comment("Force the guiSkillButton to the following x position from the center of the inventory, default value is 125").defineInRange("overrideGuiSkillButtonX", 125, Integer.MIN_VALUE, Integer.MAX_VALUE);
        overrideGuiSkillButtonY = builder.comment("Force the guiSkillButton to the following y position from the center of the inventory, default value is -22").defineInRange("overrideGuiSkillButtonY", -22, Integer.MIN_VALUE, Integer.MAX_VALUE);

        disableFovChange = builder.comment("Disable the FOV change caused by the speed buf for vampire players").define("disableFovChange", false);
        disableBloodVisionRendering = builder.comment("Disable the effect of blood vision. It can still be unlocked and activated but does not have any effect").define("disableBloodVisionRendering", false);
        disableHudActionCooldownRendering = builder.comment("Disable the rendering of the action cooldowns in the HUD").define("disableHudActionCooldownRendering", false);
        disableHudActionDurationRendering = builder.comment("Disable the rendering of the action durations in the HUD").define("disableHudActionDurationRendering", false);

        builder.pop();

        builder.push("internal");
        actionOrder = builder.comment("Action ordering").define("actionOrder", "", ClientConfigHelper::testActions);
        minionTaskOrder = builder.comment("Minion task ordering").define("minionTaskOrder", "", ClientConfigHelper::testTasks);
        builder.pop();

        builder.pop();
    }

}
