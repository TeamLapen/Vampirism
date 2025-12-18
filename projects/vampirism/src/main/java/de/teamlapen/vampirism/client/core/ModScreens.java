package de.teamlapen.vampirism.client.core;

import de.teamlapen.factions.api.util.FResourceLocation;
import de.teamlapen.factions.client.core.FactionAppearanceScreens;
import de.teamlapen.vampirism.api.client.VampirismOverlays;
import de.teamlapen.vampirism.client.DisguiseOverlay;
import de.teamlapen.vampirism.client.VampirismModClient;
import de.teamlapen.vampirism.client.gui.overlay.BatOverlay;
import de.teamlapen.vampirism.client.gui.overlay.BloodBarOverlay;
import de.teamlapen.vampirism.client.gui.overlay.RageOverlay;
import de.teamlapen.vampirism.client.gui.overlay.SunOverlay;
import de.teamlapen.vampirism.client.gui.screens.*;
import de.teamlapen.vampirism.client.gui.screens.diffuser.FogDiffuserScreen;
import de.teamlapen.vampirism.client.gui.screens.diffuser.GarlicDiffuserScreen;
import de.teamlapen.vampirism.common.core.ModFactions;
import de.teamlapen.vampirism.common.core.ModMenus;
import net.minecraft.client.gui.components.WidgetSprites;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import org.jetbrains.annotations.NotNull;

public class ModScreens {
    /**
     * Call on serial loading queue to avoid possible issues with parallel access
     */
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenus.HUNTER_TABLE.get(), HunterTableScreen::new);
        event.register(ModMenus.ALCHEMICAL_CAULDRON.get(), AlchemicalCauldronScreen::new);
        event.register(ModMenus.WEAPON_TABLE.get(), WeaponTableScreen::new);
        event.register(ModMenus.HUNTER_TRAINER.get(), HunterTrainerScreen::new);
        event.register(ModMenus.HUNTER_BASIC.get(), HunterBasicScreen::new);
        event.register(ModMenus.ALTAR_INFUSION.get(), AltarInfusionScreen::new);
        event.register(ModMenus.BLOOD_GRINDER.get(), BloodGrinderScreen::new);
        event.register(ModMenus.EXTENDED_POTION_TABLE.get(), PotionTableScreen::new);
        event.register(ModMenus.ALCHEMICAL_TABLE.get(), AlchemyTableScreen::new);
        event.register(ModMenus.VAMPIRE_BEACON.get(), VampireBeaconScreen::new);
        event.register(ModMenus.REVERT_BACK.get(), InjectionChairRevertBackScreen::new);
        event.register(ModMenus.GARLIC_DIFFUSER.get(), GarlicDiffuserScreen::new);
        event.register(ModMenus.FOG_DIFFUSER.get(), FogDiffuserScreen::new);
        event.register(ModMenus.INFUSER_MENU.get(), InfuserScreen::new);
    }

    static void registerScreenOverlays(@NotNull RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.FOOD_LEVEL, VampirismOverlays.BLOOD_BAR_ID, new BloodBarOverlay());
        event.registerAbove(VanillaGuiLayers.CAMERA_OVERLAYS, VampirismOverlays.RAGE, new RageOverlay());
        event.registerAbove(VanillaGuiLayers.CAMERA_OVERLAYS, VampirismOverlays.BAT, new BatOverlay());
        event.registerAbove(VanillaGuiLayers.CAMERA_OVERLAYS, VampirismOverlays.DISGUISE, new DisguiseOverlay());
        event.registerAbove(VanillaGuiLayers.CAMERA_OVERLAYS, VampirismOverlays.SUN, new SunOverlay());
        event.registerAbove(VanillaGuiLayers.CAMERA_OVERLAYS, VampirismOverlays.FULL_SCREEN, VampirismModClient.services().fullScreenOverlay());
    }

    public static void registerAppearanceScreens(FactionAppearanceScreens.RegisterFactionAppearanceScreensEvent event) {
        event.register(ModFactions.VAMPIRE, VampirePlayerAppearanceScreen::new, new WidgetSprites(FResourceLocation.mod("widget/appearance"), FResourceLocation.mod("widget/appearance_highlighted")));
    }
}
