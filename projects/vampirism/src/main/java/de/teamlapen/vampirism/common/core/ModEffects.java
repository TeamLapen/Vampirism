package de.teamlapen.vampirism.common.core;

import de.teamlapen.faction.common.world.effects.FactionBadOmenMobEffect;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.common.world.effects.*;
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

    public static final DeferredHolder<MobEffect, MobEffect> SANGUINARE = EFFECTS.register("sanguinare", () -> new SanguinareMobEffect(MobEffectCategory.NEUTRAL, 0x6A0888));
    public static final DeferredHolder<MobEffect, MobEffect> SATURATION = EFFECTS.register("saturation", () -> new SimpleMobEffect(MobEffectCategory.BENEFICIAL, 0xDCFF00));
    public static final DeferredHolder<MobEffect, MobEffect> SUNSCREEN = EFFECTS.register("sunscreen", () -> new SimpleMobEffect(MobEffectCategory.BENEFICIAL, 0xFFF100).addAttributeModifier(ModAttributes.SUNDAMAGE, ModEffects.SUNSCREEN.getId(), -0.5, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static final DeferredHolder<MobEffect, MobEffect> DISGUISE_AS_VAMPIRE = EFFECTS.register("disguise_as_vampire", () -> new SimpleMobEffect(MobEffectCategory.NEUTRAL, 0x999900));
    public static final DeferredHolder<MobEffect, MobEffect> FIRE_PROTECTION = EFFECTS.register("fire_protection", () -> new SimpleMobEffect(MobEffectCategory.BENEFICIAL, 14981690));
    public static final DeferredHolder<MobEffect, MobEffect> GARLIC = EFFECTS.register("garlic", () -> new SimpleMobEffect(MobEffectCategory.HARMFUL, 0xFFFFFF));
    public static final DeferredHolder<MobEffect, MobEffect> TOXICANT = EFFECTS.register("toxicant", GarlicEffect::new);
    public static final DeferredHolder<MobEffect, MobEffect> FREEZE = EFFECTS.register("freeze", () -> new FreezeMobEffect(MobEffectCategory.HARMFUL, 0xFFFFFF));
    public static final DeferredHolder<MobEffect, MobEffect> NEONATAL = EFFECTS.register("neonatal", () -> new SimpleMobEffect(MobEffectCategory.NEUTRAL, 0xFFBBBB).addAttributeModifier(Attributes.ATTACK_DAMAGE, ModEffects.NEONATAL.getId(), -0.15, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL).addAttributeModifier(Attributes.MOVEMENT_SPEED, ModEffects.NEONATAL.getId(), -0.15, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static final DeferredHolder<MobEffect, MobEffect> ARMOR_REGENERATION = EFFECTS.register("armor_regeneration", () -> new ArmorRegenerationMobEffect(MobEffectCategory.NEUTRAL, 0xD17642));
    public static final DeferredHolder<MobEffect, MobEffect> BAD_OMEN_VAMPIRE = EFFECTS.register("bad_omen_vampire", () -> new FactionBadOmenMobEffect(ModFactions.VAMPIRE));
    public static final DeferredHolder<MobEffect, MobEffect> BAD_OMEN_HUNTER = EFFECTS.register("bad_omen_hunter", () -> new FactionBadOmenMobEffect(ModFactions.HUNTER));
    public static final DeferredHolder<MobEffect, MobEffect> LORD_SPEED = EFFECTS.register("lord_speed", () -> new SimpleMobEffect(MobEffectCategory.BENEFICIAL, 0xffffff).addAttributeModifier(Attributes.MOVEMENT_SPEED, ModEffects.LORD_SPEED.getId(), 0.07F, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static final DeferredHolder<MobEffect, MobEffect> LORD_ATTACK_SPEED = EFFECTS.register("lord_attack_speed", () -> new SimpleMobEffect(MobEffectCategory.BENEFICIAL, 0xffffff).addAttributeModifier(Attributes.ATTACK_SPEED, ModEffects.LORD_ATTACK_SPEED.getId(), 0.05F, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static final DeferredHolder<MobEffect, MobEffect> NO_BLOOD = EFFECTS.register("no_blood", () -> new SimpleMobEffect(MobEffectCategory.HARMFUL, 0x191919)
            .addAttributeModifier(Attributes.MOVEMENT_SPEED, ModEffects.NO_BLOOD.getId(), -0.4F, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            .addAttributeModifier(Attributes.ATTACK_SPEED, ModEffects.NO_BLOOD.getId(), -0.3F, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            .addAttributeModifier(ModAttributes.SUNDAMAGE, ModEffects.NO_BLOOD.getId(), 0.2, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            .addAttributeModifier(Attributes.ARMOR_TOUGHNESS, ModEffects.NO_BLOOD.getId(), -0.4, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            .addAttributeModifier(Attributes.ARMOR, ModEffects.NO_BLOOD.getId(), -0.2, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
    );
    public static final DeferredHolder<MobEffect, BleedingMobEffect> BLEEDING = EFFECTS.register("bleeding", () -> new BleedingMobEffect(MobEffectCategory.HARMFUL, 0x740000));
    public static final DeferredHolder<MobEffect, MobEffect> CRUCIFIX_SUPPRESSION = EFFECTS.register("crucifix_suppression", () -> new SimpleMobEffect(MobEffectCategory.HARMFUL, 0x8B0000));

    static void register(IEventBus bus) {
        EFFECTS.register(bus);
    }

}
