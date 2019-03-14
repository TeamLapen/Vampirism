package de.teamlapen.vampirism.player.hunter.actions;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

/**
 * Registers and holds all skills for hunter players
 */
@GameRegistry.ObjectHolder(REFERENCE.MODID)
public class HunterActions {
    public static final DisguiseHunterAction disguise_hunter = UtilLib.getNull();
    public static final AwarenessHunterAction awareness_hunter = UtilLib.getNull();

    public static void registerDefaultActions(IForgeRegistry<IAction> registry) {
        registry.register(new DisguiseHunterAction().setRegistryName("vampirism", "disguise_hunter"));
        registry.register(new AwarenessHunterAction().setRegistryName("vampirism", "awareness_hunter"));
    }
}