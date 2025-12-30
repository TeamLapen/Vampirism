package de.teamlapen.vampirism.client.models.items.properties;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.api.util.VIdentifier;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record HasName(String name) implements ConditionalItemModelProperty {

    public static final Identifier ID = VIdentifier.mod("has_name");
    public static final MapCodec<HasName> CODEC = RecordCodecBuilder.mapCodec(
            inst -> inst.group(
                    Codec.STRING.fieldOf("name").forGetter(HasName::name))
                    .apply(inst, HasName::new));

    @Override
    public boolean get(@NotNull ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed, @NotNull ItemDisplayContext displayContext) {
        Component component = stack.get(DataComponents.CUSTOM_NAME);
        return component != null && component.toString().contains(name);
    }

    @Override
    public @NotNull MapCodec<? extends ConditionalItemModelProperty> type() {
        return CODEC;
    }
}
