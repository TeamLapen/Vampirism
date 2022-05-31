package de.teamlapen.vampirism.player.hunter.actions;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.core.ModRegistries;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;

import static de.teamlapen.lib.lib.util.UtilLib.getNull;

/**
 * Registers and holds all skills for hunter players
 */
public class HunterActions {
    public static final DeferredRegister<IAction> ACTIONS = DeferredRegister.create(ModRegistries.ACTIONS, REFERENCE.MODID);

    public static final RegistryObject<AwarenessHunterAction> AWARENESS_HUNTER =
            ACTIONS.register("awareness_hunter", AwarenessHunterAction::new);
    public static final RegistryObject<DisguiseHunterAction> DISGUISE_HUNTER =
            ACTIONS.register("disguise_hunter", DisguiseHunterAction::new);
    public static final RegistryObject<PotionResistanceHunterAction> POTION_RESISTANCE_HUNTER =
            ACTIONS.register("potion_resistance_hunter", PotionResistanceHunterAction::new);

    public static void registerDefaultActions(IEventBus bus) {
        ACTIONS.register(bus);
    }
}