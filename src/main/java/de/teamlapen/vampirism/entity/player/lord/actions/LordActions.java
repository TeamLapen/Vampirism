package de.teamlapen.vampirism.entity.player.lord.actions;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.DeferredAction;
import de.teamlapen.vampirism.api.DeferredActionRegister;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.entity.player.FactionBasePlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.ApiStatus;

public class LordActions {
    public static final DeferredActionRegister<?> ACTIONS = DeferredActionRegister.create(REFERENCE.MODID);

    public static final DeferredAction<?, IAction<?>, SpeedLordAction<? extends IFactionPlayer<?>>> LORD_SPEED = ACTIONS.registerUnspecified("lord_speed", SpeedLordAction::new);
    public static final DeferredAction<?, IAction<?>, AttackSpeedLordAction<?>> LORD_ATTACK_SPEED = ACTIONS.registerUnspecified("lord_attack_speed", AttackSpeedLordAction::new);

    @ApiStatus.Internal
    public static void register(IEventBus bus){
        ACTIONS.register(bus);
    }
}
