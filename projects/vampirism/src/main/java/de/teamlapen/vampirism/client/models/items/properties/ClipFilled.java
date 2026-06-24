package de.teamlapen.vampirism.client.models.items.properties;

import com.mojang.serialization.MapCodec;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.common.core.ModDataComponents;
import de.teamlapen.vampirism.common.world.items.component.ArrowContainerProjectiles;
import de.teamlapen.vampirism.common.world.items.component.QuarrelPouchContents;
import de.teamlapen.vampirism.common.world.items.crossbow.arrow.ArrowContainer;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record ClipFilled() implements RangeSelectItemModelProperty {

    public static final Identifier ID = VIdentifier.mod("clip_filled");
    public static final MapCodec<ClipFilled> CODEC = MapCodec.unit(ClipFilled::new);

    @Override
    public float get(@NotNull ItemStack stack, @Nullable ClientLevel level, @Nullable ItemOwner entity, int light) {
        if (stack.getItem() instanceof ArrowContainer container) {
            var arrows = stack.get(ModDataComponents.CONTAINED_PROJECTILES);
            return Math.clamp((arrows == null ? 0 : arrows.count()) / (float) container.maxCount(), 0, 1);
        } else {
            QuarrelPouchContents pouch = stack.getOrDefault(ModDataComponents.QUARREL_POUCH_CONTENTS, QuarrelPouchContents.EMPTY);
            return Math.clamp(pouch.getCount() / (float) QuarrelPouchContents.MAX_ITEMS, 0, 1);
        }
    }

    @Override
    public @NotNull MapCodec<? extends RangeSelectItemModelProperty> type() {
        return CODEC;
    }
}
