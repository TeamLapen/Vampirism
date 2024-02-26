package de.teamlapen.vampirism.entity.player.lord.actions;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.ApiStatus;

public class LordActions {
    public static final DeferredRegister<IAction<?>> ACTIONS = DeferredRegister.create(VampirismRegistries.Keys.ACTION, REFERENCE.MODID);

    public static final DeferredHolder<IAction<?>, SpeedLordAction<?>> LORD_SPEED = ACTIONS.register("lord_speed", SpeedLordAction::new);
    public static final DeferredHolder<IAction<?>, AttackSpeedLordAction<?>> LORD_ATTACK_SPEED = ACTIONS.register("lord_attack_speed", AttackSpeedLordAction::new);

    @ApiStatus.Internal
    public static void register(IEventBus bus){
        ACTIONS.register(bus);
    }
}
