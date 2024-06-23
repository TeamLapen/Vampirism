package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.effects.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Handles all potion registrations and reference.
 */
public class ModEffects {
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, REFERENCE.MODID);

    public static final DeferredHolder<MobEffect, MobEffect> SANGUINARE = EFFECTS.register("sanguinare", () -> new SanguinareEffect(MobEffectCategory.NEUTRAL, 0x6A0888));
    public static final DeferredHolder<MobEffect, MobEffect> SATURATION = EFFECTS.register("saturation", () -> new VampirismEffect(MobEffectCategory.BENEFICIAL, 0xDCFF00));
    public static final DeferredHolder<MobEffect, MobEffect> SUNSCREEN = EFFECTS.register("sunscreen", () -> new VampirismEffect(MobEffectCategory.BENEFICIAL, 0xFFF100).addAttributeModifier(ModAttributes.SUNDAMAGE, ModEffects.SUNSCREEN.getId(), -0.5, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static final DeferredHolder<MobEffect, MobEffect> DISGUISE_AS_VAMPIRE = EFFECTS.register("disguise_as_vampire", () -> new VampirismEffect(MobEffectCategory.NEUTRAL, 0x999900));
    public static final DeferredHolder<MobEffect, MobEffect> FIRE_PROTECTION = EFFECTS.register("fire_protection", () -> new VampirismEffect(MobEffectCategory.BENEFICIAL, 14981690));
    public static final DeferredHolder<MobEffect, MobEffect> GARLIC = EFFECTS.register("garlic", () -> new VampirismEffect(MobEffectCategory.HARMFUL, 0xFFFFFF));
    public static final DeferredHolder<MobEffect, MobEffect> POISON = EFFECTS.register("poison", () -> new VampirismPoisonEffect(0x4E9331));
    public static final DeferredHolder<MobEffect, MobEffect> FREEZE = EFFECTS.register("freeze", FreezeEffect::new);
    public static final DeferredHolder<MobEffect, MobEffect> NEONATAL = EFFECTS.register("neonatal", () -> new VampirismEffect(MobEffectCategory.NEUTRAL, 0xFFBBBB).addAttributeModifier(Attributes.ATTACK_DAMAGE, ModEffects.NEONATAL.getId(), -0.15, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL).addAttributeModifier(Attributes.MOVEMENT_SPEED, ModEffects.NEONATAL.getId(), -0.15, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static final DeferredHolder<MobEffect, MobEffect> OBLIVION = EFFECTS.register("oblivion", OblivionEffect::new);
    public static final DeferredHolder<MobEffect, MobEffect> ARMOR_REGENERATION = EFFECTS.register("armor_regeneration", () -> new VampirismEffect(MobEffectCategory.NEUTRAL, 0xD17642));
    public static final DeferredHolder<MobEffect, MobEffect> BAD_OMEN_HUNTER = EFFECTS.register("bad_omen_hunter", () -> new BadOmenEffect(ModFactions.HUNTER));
    public static final DeferredHolder<MobEffect, MobEffect> BAD_OMEN_VAMPIRE = EFFECTS.register("bad_omen_vampire", () -> new BadOmenEffect(ModFactions.VAMPIRE));
    public static final DeferredHolder<MobEffect, MobEffect> LORD_SPEED = EFFECTS.register("lord_speed", () -> new VampirismEffect(MobEffectCategory.BENEFICIAL, 0xffffff).addAttributeModifier(Attributes.MOVEMENT_SPEED, ModEffects.LORD_SPEED.getId(), 0.07F, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static final DeferredHolder<MobEffect, MobEffect> LORD_ATTACK_SPEED = EFFECTS.register("lord_attack_speed", () -> new VampirismEffect(MobEffectCategory.BENEFICIAL, 0xffffff).addAttributeModifier(Attributes.ATTACK_SPEED, ModEffects.LORD_ATTACK_SPEED.getId(), 0.05F, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static final DeferredHolder<MobEffect, MobEffect> NO_BLOOD = EFFECTS.register("no_blood", () -> new VampirismEffect(MobEffectCategory.HARMFUL, 0x191919)
            .addAttributeModifier(Attributes.MOVEMENT_SPEED, ModEffects.NO_BLOOD.getId(), -0.4F, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            .addAttributeModifier(Attributes.ATTACK_SPEED, ModEffects.NO_BLOOD.getId(), -0.3F, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            .addAttributeModifier(ModAttributes.SUNDAMAGE, ModEffects.NO_BLOOD.getId(), 0.2, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            .addAttributeModifier(Attributes.ARMOR_TOUGHNESS, ModEffects.NO_BLOOD.getId(), -0.4, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            .addAttributeModifier(Attributes.ARMOR, ModEffects.NO_BLOOD.getId(), -0.2, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
    );
    public static final DeferredHolder<MobEffect, BleedingEffect> BLEEDING = EFFECTS.register("bleeding", () -> new BleedingEffect(MobEffectCategory.HARMFUL, 0x740000));

    static void register(IEventBus bus) {
        EFFECTS.register(bus);
    }

}
