package de.teamlapen.factions.common.core;

import de.teamlapen.factions.api.util.REFERENCE;
import de.teamlapen.factions.common.world.effects.OblivionMobEffect;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class FactionEffects {
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, REFERENCE.MOD_ID);

    public static final DeferredHolder<MobEffect, MobEffect> OBLIVION = EFFECTS.register("oblivion", () -> new OblivionMobEffect<>(MobEffectCategory.NEUTRAL, 0x4E9331));

    public static void register(IEventBus bus) {
        EFFECTS.register(bus);
    }
}
