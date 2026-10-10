package de.teamlapen.vampirism.common.world.items.consume;

import de.teamlapen.faction.common.world.items.consume.FactionBasedConsumeEffect;
import de.teamlapen.vampirism.api.world.EnumStrength;
import de.teamlapen.vampirism.api.VampirismTags;
import de.teamlapen.vampirism.common.core.ModEffects;
import de.teamlapen.vampirism.common.core.ModFactions;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.world.item.consume_effects.RemoveStatusEffectsConsumeEffect;

import java.util.List;

public class ModConsumables {

    public static final Consumable NASTY_NON_VAMPIRES = Consumables.defaultFood()
            .onConsume(FactionBasedConsumeEffect.allExcept(ModFactions.VAMPIRE, new MobEffectInstance(MobEffects.NAUSEA, 400)))
            .build();
    public static final Consumable GARLIC = Consumables.defaultFood()
            .onConsume(new RemoveStatusEffectsConsumeEffect(ModEffects.SANGUINARE))
            .onConsume(FactionBasedConsumeEffect.build(VampirismTags.Factions.IS_VAMPIRE, new AffectGarlic(EnumStrength.MEDIUM)))
            .build();
    public static final Consumable GOLDEN_HEART = Consumables.defaultFood()
            .onConsume(FactionBasedConsumeEffect.only(ModFactions.VAMPIRE, List.of(new MobEffectInstance(MobEffects.REGENERATION, 100, 1), new MobEffectInstance(MobEffects.ABSORPTION, 2400, 0))))
            .onConsume(FactionBasedConsumeEffect.allExcept(ModFactions.VAMPIRE, new MobEffectInstance(MobEffects.NAUSEA, 400)))
            .build();
    public static final Consumable ENCHANTED_GOLDEN_HEART = Consumables.defaultFood()
            .onConsume(FactionBasedConsumeEffect.only(ModFactions.VAMPIRE, List.of(new MobEffectInstance(MobEffects.REGENERATION, 400, 1), new MobEffectInstance(MobEffects.RESISTANCE, 6000, 0), new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 6000, 0), new MobEffectInstance(MobEffects.ABSORPTION, 2400, 3))))
            .onConsume(FactionBasedConsumeEffect.allExcept(ModFactions.VAMPIRE, new MobEffectInstance(MobEffects.NAUSEA, 400)))
            .build();
}
