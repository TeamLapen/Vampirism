package de.teamlapen.vampirism.common.entity.player.lord.actions;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.factions.api.entities.player.IFactionPlayer;
import de.teamlapen.factions.api.actions.IAction;
import de.teamlapen.factions.api.registries.actions.DeferredAction;
import de.teamlapen.factions.api.registries.actions.DeferredActionRegister;
import net.neoforged.bus.api.IEventBus;
import org.jetbrains.annotations.ApiStatus;

public class LordActions {
    public static final DeferredActionRegister<?> ACTIONS = DeferredActionRegister.create(REFERENCE.MODID);

    public static final DeferredAction<?, IAction<?>, SpeedLordAction<? extends IFactionPlayer<?>>> LORD_SPEED = ACTIONS.registerUnspecified("lord_speed", SpeedLordAction::new);
    public static final DeferredAction<?, IAction<?>, AttackSpeedLordAction<?>> LORD_ATTACK_SPEED = ACTIONS.registerUnspecified("lord_attack_speed", AttackSpeedLordAction::new);

    @ApiStatus.Internal
    public static void register(IEventBus bus) {
        ACTIONS.register(bus);
    }
}
