package de.teamlapen.faction.common.util;

import com.google.common.base.Preconditions;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.EitherCodec;
import com.mojang.serialization.codecs.OptionalFieldCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.faction.api.world.IDescriptionProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.DependantName;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Consumer;

public final class ShiftDescription {

    public static final Codec<ShiftDescription> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            ComponentSerialization.CODEC.optionalFieldOf("component").forGetter(x -> Optional.ofNullable(x.component)),
            Codec.STRING.optionalFieldOf("formatableString").forGetter(x -> Optional.ofNullable(x.formatableString)),
    ).apply(inst, ShiftDescription::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ShiftDescription> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.optional(ComponentSerialization.STREAM_CODEC), x -> Optional.ofNullable(x.component),
            ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8), x -> Optional.ofNullable(x.formatableString),
            ShiftDescription::new
    );

    private @Nullable Component component;
    private @Nullable String formatableString;

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private ShiftDescription(@Nullable Optional<Component> component, @Nullable Optional<String> formatableString) {
        this.component = component.orElse(null);
        this.formatableString = formatableString.orElse(null);
    }

    public ShiftDescription(Component component) {
        this.component = component;
    }

    public ShiftDescription(String string) {
        this.formatableString = string;
    }

    public ShiftDescription() {
    }

    public void addTooltips(ItemStack stack, @Nullable Player player, Item.TooltipContext context, TooltipFlag tooltipFlag, Consumer<Component> tooltipAdder) {
        @Nullable
        Component description = this.component;
        if (description != null) {
            if (this.formatableString != null && stack.getItem() instanceof IDescriptionProvider provider) {
                description = this.component = Component.translatable(this.formatableString, provider.getDescriptionParameters());
            } else {
                description = this.component = Component.translatable(stack.getItem().getKey().identifier().toLanguageKey("tooltip"));
            }
        }

        if (description == null) return;
        DescriptionUtil.addDescriptionTooltip(description, context, tooltipFlag, tooltipAdder);
    }
}
