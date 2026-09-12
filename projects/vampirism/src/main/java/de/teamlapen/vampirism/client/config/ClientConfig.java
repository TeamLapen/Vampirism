package de.teamlapen.vampirism.client.config;

import de.teamlapen.faction.client.config.values.ColorConfigValue;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.TranslatableEnum;

public class ClientConfig {

    public final ColorConfigValue garlicFinderAuraColor;
    public final ModConfigSpec.EnumValue<ChargeBarDisplay> chargeBarDisplayType;
    public final ColorConfigValue appliedOilColor;
    public final ColorConfigValue vampireSwordChargeColor;
    public final ColorConfigValue vampireSwordTrainingColor;

    // Rendering
    public final ModConfigSpec.BooleanValue renderAdvancedMobPlayerFaces;
    public final ModConfigSpec.BooleanValue renderVampireEyes;
    public final ModConfigSpec.BooleanValue renderVampireForestFog;
    public final ModConfigSpec.BooleanValue correctVampireFOV;
    public final ModConfigSpec.BooleanValue renderBloodVision;
    public final ModConfigSpec.BooleanValue renderVampireSwordParticles;

    // Overlays
    public final ModConfigSpec.BooleanValue showFullScreenOverlay;
    public final ModConfigSpec.BooleanValue showBatHUDOverlay;
    public final ModConfigSpec.BooleanValue showDisguiseHUDOverlay;
    public final ModConfigSpec.BooleanValue showVampireRageHUDOverlay;
    public final ModConfigSpec.BooleanValue showSunHUDOverlay;
    public final ModConfigSpec.BooleanValue showSunBlindOverlay;
    public final ModConfigSpec.BooleanValue showSunBlindOverlayWithShaders;
    public final ModConfigSpec.BooleanValue showNearbyVampireOverlay;

    public ClientConfig(ModConfigSpec.Builder builder) {
        this.garlicFinderAuraColor = ColorConfigValue.define(builder,
                "garlicFinderAuraColor", "#e0b74f",
                "The color used by the garlic finder to highlight blocks.");
        this.chargeBarDisplayType = builder
                .comment("How the charge overlay of items (e.g. oiled weapons and vampire swords) should be displayed.")
                .defineEnum("chargeBarDisplayType", ChargeBarDisplay.ITEM);
        this.appliedOilColor = ColorConfigValue.define(builder,
                "appliedOilColor", "#5555ff",
                "The color of the weapon charge bar that displays the oil applied to the item (gets applied only when the display type is set to 'Item'");
        this.vampireSwordChargeColor = ColorConfigValue.define(builder,
                "vampireSwordChargeColor", "#d11b1f",
                "The color of the vampire sword charge bar that displays its blood charging progress (gets applied only when the display type is set to 'Item'");
        this.vampireSwordTrainingColor = ColorConfigValue.define(builder,
                "vampireSwordTrainingColor", "#3c7c0c",
                "The color of the vampire sword charge bar that displays its training progress (gets applied only when the display type is set to 'Item'");

        builder.push("rendering");
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
        builder.pop();

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
        this.showSunBlindOverlay = builder
                .comment("When enabled, washes the screen white when a vampire looks directly at the sun.")
                .define("showSunBlindOverlay", true);
        this.showSunBlindOverlayWithShaders = builder
                .comment("When enabled, keeps the sun blinding overlay while an Iris shader pack is active. Shader packs usually apply their own sun glare, so the overlay is hidden by default in that case.")
                .define("showSunBlindOverlayWithShaders", false);
        this.showNearbyVampireOverlay = builder
                .comment("When enabled, shows the nearby vampire warning in the HUD.")
                .define("showNearbyVampireOverlay", true);
        builder.pop();
    }

    public enum ChargeBarDisplay implements TranslatableEnum {
        CORNER, ITEM
    }
}
