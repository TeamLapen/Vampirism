package de.teamlapen.faction.common.core;

import de.teamlapen.faction.api.factions.actions.IAction;
import de.teamlapen.faction.api.registries.actions.DeferredAction;
import de.teamlapen.faction.api.registries.actions.DeferredActionRegister;
import de.teamlapen.faction.api.util.REFERENCE;
import de.teamlapen.faction.api.world.entities.player.IFactionPlayer;
import de.teamlapen.faction.common.factions.lord.actions.AttackSpeedLordAction;
import de.teamlapen.faction.common.factions.lord.actions.SpeedLordAction;
import net.neoforged.bus.api.IEventBus;

public class FactionActions {

    public static final DeferredActionRegister<?> ACTIONS = DeferredActionRegister.create(REFERENCE.MOD_ID);

    public static final DeferredAction<?, IAction<?>, SpeedLordAction<? extends IFactionPlayer<?>>> LORD_SPEED = ACTIONS.registerUnspecified("lord_speed", SpeedLordAction::new);
    public static final DeferredAction<?, IAction<?>, AttackSpeedLordAction<?>> LORD_ATTACK_SPEED = ACTIONS.registerUnspecified("lord_attack_speed", AttackSpeedLordAction::new);

    static void register(IEventBus bus) {
        ACTIONS.register(bus);
    }
}
