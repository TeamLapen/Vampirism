package de.teamlapen.vampirism.player.hunter.actions;

import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

import static de.teamlapen.lib.lib.util.UtilLib.getNull;

/**
 * Registers and holds all skills for hunter players
 */
@ObjectHolder(REFERENCE.MODID)
public class HunterActions {
    public static final AwarenessHunterAction awareness_hunter = getNull();
    public static final DisguiseHunterAction disguise_hunter = getNull();
    public static final PotionResistanceHunterAction potion_resistance_hunter = getNull();

    public static void registerDefaultActions(IForgeRegistry<IAction> registry) {
        registry.register(new AwarenessHunterAction().setRegistryName(REFERENCE.MODID, "awareness_hunter"));
        registry.register(new DisguiseHunterAction().setRegistryName(REFERENCE.MODID, "disguise_hunter"));
        registry.register(new PotionResistanceHunterAction().setRegistryName(REFERENCE.MODID, "potion_resistance_hunter"));
    }
}