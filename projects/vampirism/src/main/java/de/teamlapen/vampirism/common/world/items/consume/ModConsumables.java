package de.teamlapen.vampirism.common.world.items.consume;

import de.teamlapen.faction.common.world.items.consume.FactionBasedConsumeEffect;
import de.teamlapen.vampirism.api.EnumStrength;
import de.teamlapen.vampirism.api.VampirismTags;
import de.teamlapen.vampirism.common.core.ModEffects;
import de.teamlapen.vampirism.common.core.ModFactions;
import de.teamlapen.vampirism.common.tags.ModFactionTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.item.consume_effects.RemoveStatusEffectsConsumeEffect;

public class ModConsumables {

    public static final Consumable NASTY_NON_VAMPIRES = Consumables.defaultFood()
            .onConsume(FactionBasedConsumeEffect.allExcept(ModFactions.VAMPIRE, new MobEffectInstance(MobEffects.NAUSEA, 400)))
            .build();
    public static final Consumable GARLIC = Consumables.defaultFood()
            .onConsume(new RemoveStatusEffectsConsumeEffect(ModEffects.SANGUINARE))
            .onConsume(FactionBasedConsumeEffect.build(VampirismTags.Factions.IS_VAMPIRE, new AffectGarlic(EnumStrength.MEDIUM)))
            .build();
}
