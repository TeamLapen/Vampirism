package de.teamlapen.faction.common.core;

import de.teamlapen.faction.api.util.REFERENCE;
import de.teamlapen.faction.common.world.effects.OblivionMobEffect;
import de.teamlapen.faction.common.world.effects.ResurrectionFatigueMobEffect;
import de.teamlapen.faction.common.world.effects.SimpleMobEffect;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class FactionEffects {
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, REFERENCE.MOD_ID);

    public static final DeferredHolder<MobEffect, MobEffect> OBLIVION = EFFECTS.register("oblivion", () -> new OblivionMobEffect<>(MobEffectCategory.NEUTRAL, 0x4E9331));
    public static final DeferredHolder<MobEffect, MobEffect> RESURRECTION_FATIGUE = EFFECTS.register("resurrection_fatigue", ResurrectionFatigueMobEffect::new);
    public static final DeferredHolder<MobEffect, MobEffect> LORD_SPEED = EFFECTS.register("lord_speed", () -> new SimpleMobEffect(MobEffectCategory.BENEFICIAL, 0xffffff).addAttributeModifier(Attributes.MOVEMENT_SPEED, FactionEffects.LORD_SPEED.getId(), 0.07F, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static final DeferredHolder<MobEffect, MobEffect> LORD_ATTACK_SPEED = EFFECTS.register("lord_attack_speed", () -> new SimpleMobEffect(MobEffectCategory.BENEFICIAL, 0xffffff).addAttributeModifier(Attributes.ATTACK_SPEED, FactionEffects.LORD_ATTACK_SPEED.getId(), 0.05F, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));


    public static void register(IEventBus bus) {
        EFFECTS.register(bus);
    }
}
