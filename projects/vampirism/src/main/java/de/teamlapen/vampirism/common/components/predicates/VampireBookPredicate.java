package de.teamlapen.vampirism.common.components.predicates;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.api.world.items.components.IVampireBook;
import de.teamlapen.vampirism.common.core.ModDataComponents;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.predicates.DataComponentPredicate;
import net.minecraft.resources.ResourceLocation;

public record VampireBookPredicate(ResourceLocation id) implements DataComponentPredicate {

    public static final Codec<VampireBookPredicate> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(VampireBookPredicate::id)
    ).apply(inst, VampireBookPredicate::new));

    @Override
    public boolean matches(DataComponentGetter componentGetter) {
        IVampireBook iVampireBook = componentGetter.get(ModDataComponents.VAMPIRE_BOOK);
        return iVampireBook != null && iVampireBook.id().equals(this.id);
    }
}
