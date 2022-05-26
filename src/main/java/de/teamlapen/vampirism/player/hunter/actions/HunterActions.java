package de.teamlapen.vampirism.player.hunter.actions;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.core.ModRegistries;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

/**
 * Registers and holds all skills for hunter players
 */
public class HunterActions {
    public static final DeferredRegister<IAction<?>> ACTIONS = DeferredRegister.create(ModRegistries.ACTIONS, REFERENCE.MODID);

    public static final RegistryObject<AwarenessHunterAction> awareness_hunter;
    public static final RegistryObject<DisguiseHunterAction> disguise_hunter;
    public static final RegistryObject<PotionResistanceHunterAction> potion_resistance_hunter;

    public static void registerDefaultActions(IEventBus bus) {
        ACTIONS.register(bus);
    }

    static {
        awareness_hunter = ACTIONS.register("awareness_hunter", AwarenessHunterAction::new);
        disguise_hunter = ACTIONS.register("disguise_hunter", DisguiseHunterAction::new);
        potion_resistance_hunter = ACTIONS.register("potion_resistance_hunter", PotionResistanceHunterAction::new);
    }
}