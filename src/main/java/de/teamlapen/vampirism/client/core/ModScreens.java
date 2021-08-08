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
        MenuScreens.<HunterTableContainer, HunterTableScreen>register(ModContainer.hunter_table, HunterTableScreen::new);
        MenuScreens.<AlchemicalCauldronContainer, AlchemicalCauldronScreen>register(ModContainer.alchemical_cauldron, AlchemicalCauldronScreen::new);
        MenuScreens.<WeaponTableContainer, WeaponTableScreen>register(ModContainer.weapon_table, WeaponTableScreen::new);
        MenuScreens.<HunterTrainerContainer, HunterTrainerScreen>register(ModContainer.hunter_trainer, HunterTrainerScreen::new);
        MenuScreens.<HunterBasicContainer, HunterBasicScreen>register(ModContainer.hunter_basic, HunterBasicScreen::new);
        MenuScreens.<AltarInfusionContainer, AltarInfusionScreen>register(ModContainer.altar_infusion, AltarInfusionScreen::new);
        MenuScreens.<BloodGrinderContainer, BloodGrinderScreen>register(ModContainer.blood_grinder, BloodGrinderScreen::new);
        MenuScreens.<MinionContainer, MinionScreen>register(ModContainer.minion, MinionScreen::new);
        MenuScreens.register(ModContainer.task_master, TaskBoardScreen::new);
        MenuScreens.register(ModContainer.extended_potion_table, PotionTableScreen::new);
        MenuScreens.register(ModContainer.vampirism, VampirismScreen::new);
    }
}
