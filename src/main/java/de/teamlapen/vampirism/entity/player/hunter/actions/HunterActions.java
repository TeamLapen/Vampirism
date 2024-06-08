package de.teamlapen.vampirism.entity.player.hunter.actions;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.DeferredAction;
import de.teamlapen.vampirism.api.DeferredActionRegister;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.api.entity.player.actions.ILastingAction;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
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