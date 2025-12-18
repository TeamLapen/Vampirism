package de.teamlapen.vampirism.common.world.items.consume;

import de.teamlapen.factions.common.world.items.consume.FactionBasedConsumeEffect;
import de.teamlapen.vampirism.api.EnumStrength;
import de.teamlapen.vampirism.common.core.ModEffects;
import de.teamlapen.vampirism.common.tags.ModFactionTags;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.consume_effects.RemoveStatusEffectsConsumeEffect;

public class ModConsumables {
    public static final Consumable GARLIC = net.minecraft.world.item.component.Consumables.defaultFood()
            .onConsume(new RemoveStatusEffectsConsumeEffect(ModEffects.SANGUINARE))
            .onConsume(FactionBasedConsumeEffect.build(ModFactionTags.IS_VAMPIRE, new AffectGarlic(EnumStrength.MEDIUM))).build();
}
