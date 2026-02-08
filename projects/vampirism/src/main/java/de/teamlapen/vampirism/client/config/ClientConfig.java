package de.teamlapen.vampirism.client.config;

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
    public final ModConfigSpec.ConfigValue<String> garlicFinderAuraColor;

    // Overlay rendering
    public final ModConfigSpec.BooleanValue renderScreenOverlay;
    public final ModConfigSpec.BooleanValue enableHudBatOverlayRendering;
    public final ModConfigSpec.BooleanValue enableDisguiseOverlayRendering;
    public final ModConfigSpec.BooleanValue enableNearbyVampireOverlayRendering;
    public final ModConfigSpec.BooleanValue enableRageOverlayRendering;
    public final ModConfigSpec.BooleanValue enableSunOverlayRendering;

    // Gui rendering
    public final ModConfigSpec.BooleanValue disableFovChange;
    public final ModConfigSpec.BooleanValue disableBloodVisionRendering;

    // Other rendering
    public final ModConfigSpec.BooleanValue renderVampireSwordParticles;

    public ClientConfig(ModConfigSpec.@NotNull Builder builder) {
        builder.comment("Client configuration settings").push("client");
        builder.comment("Configure rendering").push("render");
        this.renderAdvancedMobPlayerFaces = builder.comment("Render player faces on advanced hunter or vampires").define("advancedMobPlayerFaces", true);
        this.renderVampireEyes = builder.comment("Render vampire eye/fang face overlay").define("vampireEyes", true);
        this.renderVampireForestFog = builder.comment("Render fog in vampire biome. Might be enforced server side").define("vampireForestFog", true);
        this.renderScreenOverlay = builder.comment("Render full screen colored overlays for effects like vampire levelup").define("renderScreenOverlay", true);
        this.garlicFinderAuraColor = builder.comment("The color the garlic finder highlights blocks with in HEX. No alpha channel").define("garlicFinderAuraColor", "#e0b74f");
        builder.pop();

        builder.comment("Configure GUI").push("gui");

        this.disableFovChange = builder.comment("Disable the FOV change caused by the speed buf for vampire players").define("disableFovChange", false);
        this.disableBloodVisionRendering = builder.comment("Disable the effect of blood vision. It can still be unlocked and activated but does not have any effect").define("disableBloodVisionRendering", false);

        builder.pop();

        builder.comment("Overlay rendering").push("overlay");

        this.enableHudBatOverlayRendering = builder.comment("Disable the rendering of the bat overlay in the HUD").define("enableHudBatOverlayRendering", true);
        this.enableDisguiseOverlayRendering = builder.comment("Disable the rendering of the disguise overlay in the HUD").define("enableDisguiseOverlayRendering", true);
        this.enableNearbyVampireOverlayRendering = builder.comment("Disable the rendering of the nearby vampire overlay in the HUD").define("enableNearbyVampireOverlayRendering", true);
        this.enableRageOverlayRendering = builder.comment("Disable the rendering of the rage overlay in the HUD").define("enableRageOverlayRendering", true);
        this.enableSunOverlayRendering = builder.comment("Disable the rendering of the sun overlay in the HUD").define("enableSunOverlayRendering", true);
        builder.pop();

        builder.comment("Other rendering").push("rendering");
        this.renderVampireSwordParticles = builder.comment("Whether to add particles when holding a charged vampire sword").define("renderVampireSwordParticles", true);
        builder.pop();

        builder.push("internal");


        builder.pop();
        builder.pop();

    }

}
