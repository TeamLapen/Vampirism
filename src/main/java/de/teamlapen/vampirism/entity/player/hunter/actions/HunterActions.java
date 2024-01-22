package de.teamlapen.vampirism.entity.player.hunter.actions;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.ApiStatus;

/**
 * Registers and holds all skills for hunter players
 */
public class HunterActions {
    public static final DeferredRegister<IAction<?>> ACTIONS = DeferredRegister.create(VampirismRegistries.Keys.ACTION, REFERENCE.MODID);

    public static final DeferredHolder<IAction<?>, AwarenessHunterAction> AWARENESS_HUNTER = ACTIONS.register("awareness_hunter", AwarenessHunterAction::new);
    public static final DeferredHolder<IAction<?>, PotionResistanceHunterAction> POTION_RESISTANCE_HUNTER = ACTIONS.register("potion_resistance_hunter", PotionResistanceHunterAction::new);

    @ApiStatus.Internal
    public static void register(IEventBus bus) {
        ACTIONS.register(bus);
    }
}