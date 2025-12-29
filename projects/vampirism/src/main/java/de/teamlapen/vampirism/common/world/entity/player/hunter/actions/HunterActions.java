package de.teamlapen.vampirism.common.world.entity.player.hunter.actions;

import de.teamlapen.factions.api.factions.actions.ILastingAction;
import de.teamlapen.factions.api.registries.actions.DeferredAction;
import de.teamlapen.factions.api.registries.actions.DeferredActionRegister;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.world.entity.player.hunter.IHunterPlayer;
import net.neoforged.bus.api.IEventBus;
import org.jetbrains.annotations.ApiStatus;

/**
 * Registers and holds all skills for hunter players
 */
public class HunterActions {
    public static final DeferredActionRegister<IHunterPlayer> ACTIONS = DeferredActionRegister.create(REFERENCE.MODID);

    public static final DeferredAction<IHunterPlayer, ILastingAction<IHunterPlayer>, AwarenessHunterAction> AWARENESS_HUNTER = ACTIONS.registerAction("awareness_hunter", AwarenessHunterAction::new);
    public static final DeferredAction<IHunterPlayer, ILastingAction<IHunterPlayer>, DisguiseHunterAction> DISGUISE_HUNTER = ACTIONS.registerAction("disguise_hunter", DisguiseHunterAction::new);
    public static final DeferredAction<IHunterPlayer, ILastingAction<IHunterPlayer>, PotionResistanceHunterAction> POTION_RESISTANCE_HUNTER = ACTIONS.registerAction("potion_resistance_hunter", PotionResistanceHunterAction::new);

    @ApiStatus.Internal
    public static void register(IEventBus bus) {
        ACTIONS.register(bus);
    }
}