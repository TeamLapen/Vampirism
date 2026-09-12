package de.teamlapen.vampirism.client.models.items.properties;

import com.mojang.serialization.MapCodec;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.common.tags.ModOilTags;
import de.teamlapen.vampirism.common.world.items.component.OilContent;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record StrongOil() implements ConditionalItemModelProperty {

    public static final Identifier ID = VIdentifier.mod("strong_oil");
    public static final MapCodec<StrongOil> CODEC = MapCodec.unit(StrongOil::new);

    @Override
    public boolean get(@NotNull ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed, @NotNull ItemDisplayContext displayContext) {
        return OilContent.getOil(stack).is(ModOilTags.STRONG);
    }

    @Override
    public @NotNull MapCodec<? extends ConditionalItemModelProperty> type() {
        return CODEC;
    }
}
