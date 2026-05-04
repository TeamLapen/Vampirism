package de.teamlapen.vampirism.client.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ClientConfig {

    public final ModConfigSpec.BooleanValue renderAdvancedMobPlayerFaces;
    public final ModConfigSpec.BooleanValue renderVampireEyes;
    public final ModConfigSpec.BooleanValue renderVampireForestFog;
    public final ModConfigSpec.BooleanValue correctVampireFOV;
    public final ModConfigSpec.BooleanValue renderBloodVision;
    public final ModConfigSpec.BooleanValue renderVampireSwordParticles;
    public final ModConfigSpec.ConfigValue<String> garlicFinderAuraColor;

    // Overlays
    public final ModConfigSpec.BooleanValue showFullScreenOverlay;
    public final ModConfigSpec.BooleanValue showBatHUDOverlay;
    public final ModConfigSpec.BooleanValue showDisguiseHUDOverlay;
    public final ModConfigSpec.BooleanValue showVampireRageHUDOverlay;
    public final ModConfigSpec.BooleanValue showSunHUDOverlay;

    public ClientConfig(ModConfigSpec.Builder builder) {
        this.renderAdvancedMobPlayerFaces = builder
                .comment("When enabled, renders player faces on advanced hunter and vampire mobs.")
                .define("renderAdvancedMobPlayerFaces", true);
        this.renderVampireEyes = builder
                .comment("When enabled, renders vampire eye and fang overlays on faces.")
                .define("renderVampireEyes", true);
        this.renderVampireForestFog = builder
                .comment("When enabled, renders fog in the vampire forest biome. May be enforced server-side.")
                .define("renderVampireForestFog", true);
        this.correctVampireFOV = builder
                .comment("Set to false to disable the FOV change caused by the vampire speed buff.")
                .define("correctVampireFOV", true);
        this.renderBloodVision = builder
                .comment("Set to false to disable the blood vision effect. The ability can still be unlocked and activated, but will have no visual effect.")
                .define("renderBloodVision", true);
        this.renderVampireSwordParticles = builder
                .comment("When enabled, renders particles when holding a charged vampire sword.")
                .define("renderVampireSwordParticles", true);
        this.garlicFinderAuraColor = builder
                .comment("The color used by the garlic finder to highlight blocks, in HEX without an alpha channel.")
                .define("garlicFinderAuraColor", "#e0b74f");

        builder.push("overlays");
        this.showFullScreenOverlay = builder
                .comment("When enabled, renders full-screen colored overlays, e.g. when leveling up as a vampire.")
                .define("showFullScreenOverlay", true);
        this.showBatHUDOverlay = builder
                .comment("When enabled, shows the bat mode indicator in the HUD.")
                .define("showBatHUDOverlay", true);
        this.showDisguiseHUDOverlay = builder
                .comment("When enabled, shows the disguise indicator in the HUD.")
                .define("showDisguiseHUDOverlay", true);
        this.showVampireRageHUDOverlay = builder
                .comment("When enabled, shows the vampire rage indicator in the HUD.")
                .define("showVampireRageHUDOverlay", true);
        this.showSunHUDOverlay = builder
                .comment("When enabled, shows the sun damage warning in the HUD.")
                .define("showSunHUDOverlay", true);
        builder.pop();
    }
}
