package de.teamlapen.vampirism.common.components.predicates;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import de.teamlapen.vampirism.common.core.ModDataComponents;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.predicates.DataComponentPredicate;

public record ChargedRitualKnifePredicate() implements DataComponentPredicate {

    public static final ChargedRitualKnifePredicate INSTANCE = new ChargedRitualKnifePredicate();
    public static final Codec<ChargedRitualKnifePredicate> CODEC = MapCodec.unitCodec(INSTANCE);

    @Override
    public boolean matches(DataComponentGetter componentGetter) {
        return componentGetter.getOrDefault(ModDataComponents.CHARGED_RITUAL_KNIFE, false);
    }
}
