package de.teamlapen.vampirism.client.models.items.properties;

import com.mojang.serialization.MapCodec;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.common.core.ModDataComponents;
import de.teamlapen.vampirism.common.items.component.ContainedProjectiles;
import de.teamlapen.vampirism.common.items.component.QuarrelPouchContents;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record ClipFilled() implements RangeSelectItemModelProperty {

    public static final ResourceLocation ID = VResourceLocation.mod("clip_filled");
    public static final MapCodec<ClipFilled> CODEC = MapCodec.unit(ClipFilled::new);

    @Override
    public float get(@NotNull ItemStack stack, @Nullable ClientLevel level, @Nullable ItemOwner entity, int light) {
        ContainedProjectiles container = stack.getOrDefault(ModDataComponents.CONTAINED_PROJECTILES, ContainedProjectiles.EMPTY);
        QuarrelPouchContents pouch = stack.getOrDefault(ModDataComponents.QUARREL_POUCH_CONTENTS, QuarrelPouchContents.EMPTY);
        if (!container.getProjectiles().isEmpty()) {
            return Math.clamp(container.getProjectiles().size() / (float) container.getMaxCount(), 0, 1);
        } else {
            return Math.clamp(pouch.getCount() / (float) QuarrelPouchContents.MAX_ITEMS, 0, 1);
        }
    }

    @Override
    public @NotNull MapCodec<? extends RangeSelectItemModelProperty> type() {
        return CODEC;
    }
}
