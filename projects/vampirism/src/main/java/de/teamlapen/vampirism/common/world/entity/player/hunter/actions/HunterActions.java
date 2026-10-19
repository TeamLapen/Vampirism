package de.teamlapen.vampirism.common.world.entity.player.hunter.actions;

import de.teamlapen.faction.api.factions.actions.ILastingAction;
import de.teamlapen.faction.api.registries.actions.DeferredAction;
import de.teamlapen.faction.api.registries.actions.DeferredActionRegister;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.world.entity.player.hunter.IHunterPlayer;
import net.neoforged.bus.api.IEventBus;
import org.jetbrains.annotations.ApiStatus;

/**
 * Registers and holds all skills for hunter players
 */
public class HunterActions {
    public static final DeferredActionRegister<IHunterPlayer> ACTIONS = DeferredActionRegister.create(REFERENCE.MODID);

    public static final DeferredAction<IHunterPlayer, ILastingAction<IHunterPlayer>, CamouflageAction> CAMOUFLAGE = ACTIONS.registerAction("camouflage", CamouflageAction::new);
    public static final DeferredAction<IHunterPlayer, ILastingAction<IHunterPlayer>, SixthSenseAction> SIXTH_SENSE = ACTIONS.registerAction("sixth_sense", SixthSenseAction::new);
    public static final DeferredAction<IHunterPlayer, ILastingAction<IHunterPlayer>, PotionResistanceAction> POTION_RESISTANCE = ACTIONS.registerAction("potion_resistance", PotionResistanceAction::new);

    @ApiStatus.Internal
    public static void register(IEventBus bus) {
        ACTIONS.register(bus);
    }
}