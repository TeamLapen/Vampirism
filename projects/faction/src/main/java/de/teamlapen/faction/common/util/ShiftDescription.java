package de.teamlapen.faction.common.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.faction.api.world.IDescriptionProvider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public record ShiftDescription(@Nullable Component component, @Nullable String formatableString) {

    public static final Codec<ShiftDescription> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            ComponentSerialization.CODEC.optionalFieldOf("component").forGetter(x -> Optional.ofNullable(x.component)),
            Codec.STRING.optionalFieldOf("formatableString").forGetter(x -> Optional.ofNullable(x.formatableString))
    ).apply(inst, ShiftDescription::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ShiftDescription> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.optional(ComponentSerialization.STREAM_CODEC), x -> Optional.ofNullable(x.component),
            ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8), x -> Optional.ofNullable(x.formatableString),
            ShiftDescription::new
    );

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private ShiftDescription(Optional<Component> component, Optional<String> formatableString) {
        this(component.orElse(null), formatableString.orElse(null));
    }

    public static ShiftDescription of(Component component) {
        return new ShiftDescription(component, null);
    }

    public static ShiftDescription of(String formatableString) {
        return new ShiftDescription(null, formatableString);
    }

    public static ShiftDescription of() {
        return new ShiftDescription((Component) null, null);
    }

    public void addTooltips(ItemStack stack, @Nullable Player player, Item.TooltipContext context, TooltipFlag tooltipFlag, List<Component> tooltipComponents) {
        var component = this.component;
        if (component == null) {
            component = Optional.ofNullable(this.formatableString).or(() -> stack.typeHolder().unwrapKey().map(x -> x.identifier().toLanguageKey("tooltip"))).map(x -> {
                IDescriptionProvider provider = getDescriptionProvider(stack);
                if (provider != null) {
                    return Component.translatable(x, provider.getDescriptionParameters());
                } else {
                    return Component.translatable(x);
                }
            }).orElse(Component.empty());
        }

        DescriptionUtil.addDescriptionTooltip(component, context, tooltipFlag, tooltipComponents);
    }

    @Nullable
    private static IDescriptionProvider getDescriptionProvider(ItemStack stack) {
        Item item = stack.getItem();
        if (item instanceof IDescriptionProvider provider) {
            return provider;
        }
        if (item instanceof BlockItem blockItem && blockItem.getBlock() instanceof IDescriptionProvider provider) {
            return provider;
        }

        return null;
    }
}
