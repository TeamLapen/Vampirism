package de.teamlapen.vampirism.client.core;

import de.teamlapen.vampirism.client.gui.*;
import de.teamlapen.vampirism.core.ModContainer;
import de.teamlapen.vampirism.inventory.container.*;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModScreens {
    /**
     * Call on serial loading queue to avoid possible issues with parallel access
     */
    @SuppressWarnings("RedundantTypeArguments")
    public static void registerScreensUnsafe() {
        MenuScreens.<HunterTableContainer, HunterTableScreen>register(ModContainer.hunter_table.get(), HunterTableScreen::new);
        MenuScreens.<AlchemicalCauldronContainer, AlchemicalCauldronScreen>register(ModContainer.alchemical_cauldron.get(), AlchemicalCauldronScreen::new);
        MenuScreens.<WeaponTableContainer, WeaponTableScreen>register(ModContainer.weapon_table.get(), WeaponTableScreen::new);
        MenuScreens.<HunterTrainerContainer, HunterTrainerScreen>register(ModContainer.hunter_trainer.get(), HunterTrainerScreen::new);
        MenuScreens.<HunterBasicContainer, HunterBasicScreen>register(ModContainer.hunter_basic.get(), HunterBasicScreen::new);
        MenuScreens.<AltarInfusionContainer, AltarInfusionScreen>register(ModContainer.altar_infusion.get(), AltarInfusionScreen::new);
        MenuScreens.<BloodGrinderContainer, BloodGrinderScreen>register(ModContainer.blood_grinder.get(), BloodGrinderScreen::new);
        MenuScreens.<MinionContainer, MinionScreen>register(ModContainer.minion.get(), MinionScreen::new);
        MenuScreens.register(ModContainer.task_master.get(), TaskBoardScreen::new);
        MenuScreens.register(ModContainer.extended_potion_table.get(), PotionTableScreen::new);
        MenuScreens.register(ModContainer.vampirism.get(), VampirismScreen::new);
    }
}
