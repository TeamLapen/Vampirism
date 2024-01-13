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
    public static final DeferredHolder<MobEffect, MobEffect> SUNSCREEN = EFFECTS.register("sunscreen", () -> new VampirismEffect(MobEffectCategory.BENEFICIAL, 0xFFF100).addAttributeModifier(ModAttributes.SUNDAMAGE.get(), "9dc9420c-3e5e-41c7-9ba4-ff70e9dc69fc", -0.5, AttributeModifier.Operation.MULTIPLY_TOTAL));
    public static final DeferredHolder<MobEffect, MobEffect> DISGUISE_AS_VAMPIRE = EFFECTS.register("disguise_as_vampire", () -> new VampirismEffect(MobEffectCategory.NEUTRAL, 0x999900));
    public static final DeferredHolder<MobEffect, MobEffect> FIRE_PROTECTION = EFFECTS.register("fire_protection", () -> new VampirismEffect(MobEffectCategory.BENEFICIAL, 14981690));
    public static final DeferredHolder<MobEffect, MobEffect> GARLIC = EFFECTS.register("garlic", () -> new VampirismEffect(MobEffectCategory.HARMFUL, 0xFFFFFF));
    public static final DeferredHolder<MobEffect, MobEffect> POISON = EFFECTS.register("poison", () -> new VampirismPoisonEffect(0x4E9331));
    public static final DeferredHolder<MobEffect, MobEffect> FREEZE = EFFECTS.register("freeze", FreezeEffect::new);
    public static final DeferredHolder<MobEffect, MobEffect> NEONATAL = EFFECTS.register("neonatal", () -> new VampirismEffect(MobEffectCategory.NEUTRAL, 0xFFBBBB).addAttributeModifier(Attributes.ATTACK_DAMAGE, "377d132d-d091-43b2-8a8f-b940f9bc894c", -0.15, AttributeModifier.Operation.MULTIPLY_TOTAL).addAttributeModifier(Attributes.MOVEMENT_SPEED, "ad6d7def-46e2-485f-afba-39252767f114", -0.15, AttributeModifier.Operation.MULTIPLY_TOTAL));
    public static final DeferredHolder<MobEffect, MobEffect> OBLIVION = EFFECTS.register("oblivion", OblivionEffect::new);
    public static final DeferredHolder<MobEffect, MobEffect> ARMOR_REGENERATION = EFFECTS.register("armor_regeneration", () -> new VampirismEffect(MobEffectCategory.NEUTRAL, 0xD17642));
    public static final DeferredHolder<MobEffect, MobEffect> BAD_OMEN_HUNTER = EFFECTS.register("bad_omen_hunter", () -> new BadOmenEffect() {
        @Override
        public IFaction<?> getFaction() {
            return VReference.HUNTER_FACTION;
        }
    });
    public static final DeferredHolder<MobEffect, MobEffect> BAD_OMEN_VAMPIRE = EFFECTS.register("bad_omen_vampire", () -> new BadOmenEffect() {
        @Override
        public IFaction<?> getFaction() {
            return VReference.VAMPIRE_FACTION;
        }
    });
    public static final DeferredHolder<MobEffect, MobEffect> LORD_SPEED = EFFECTS.register("lord_speed", () -> new VampirismEffect(MobEffectCategory.BENEFICIAL, 0xffffff).addAttributeModifier(Attributes.MOVEMENT_SPEED, "efe607d8-db8a-4156-b9d0-6a0640593057", 0.07F, AttributeModifier.Operation.MULTIPLY_TOTAL));
    public static final DeferredHolder<MobEffect, MobEffect> LORD_ATTACK_SPEED = EFFECTS.register("lord_attack_speed", () -> new VampirismEffect(MobEffectCategory.BENEFICIAL, 0xffffff).addAttributeModifier(Attributes.ATTACK_SPEED, "a2ca9534-3baf-404f-b159-bc835bf963e6", 0.05F, AttributeModifier.Operation.MULTIPLY_TOTAL));
    public static final DeferredHolder<MobEffect, MobEffect> NO_BLOOD = EFFECTS.register("no_blood", () -> new VampirismEffect(MobEffectCategory.HARMFUL, 0x191919)
            .addAttributeModifier(Attributes.MOVEMENT_SPEED, "48bb1318-3d52-4030-a264-de52b59d03d0", -0.4F, AttributeModifier.Operation.MULTIPLY_TOTAL)
            .addAttributeModifier(Attributes.ATTACK_SPEED, "6d9474ab-6693-49f5-9357-ad4444a16bd4", -0.3F, AttributeModifier.Operation.MULTIPLY_TOTAL)
            .addAttributeModifier(ModAttributes.SUNDAMAGE.get(), "45ebd53a-14fa-4ede-b4e7-412e075a8b5f", 0.2, AttributeModifier.Operation.MULTIPLY_TOTAL)
            .addAttributeModifier(Attributes.ARMOR_TOUGHNESS, "45ebd53a-14fa-4ede-b4e7-412e075a8b5f", -0.4, AttributeModifier.Operation.MULTIPLY_TOTAL)
            .addAttributeModifier(Attributes.ARMOR, "45ebd53a-14fa-4ede-b4e7-412e075a8b5f", -0.2, AttributeModifier.Operation.MULTIPLY_TOTAL)
    );
    public static final DeferredHolder<MobEffect, BleedingEffect> BLEEDING = EFFECTS.register("bleeding", () -> new BleedingEffect(MobEffectCategory.HARMFUL, 0x740000));

    static void register(IEventBus bus) {
        EFFECTS.register(bus);
    }

}
