package de.teamlapen.vampirism.common.world.items.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.api.VEnums;
import de.teamlapen.vampirism.common.core.ModDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.level.ItemLike;

///
/// | Level | Item |
/// |-------|------|
/// | -1    | none |
/// | 0    | pure blood 1 |
/// | 1    | pure blood 2 |
/// | 2    | pure blood 3 |
/// | 3    | pure blood 4 |
/// | 4    | pure blood 5 |
/// | 5    | sovereign blood |
/// @param level
public record PureLevel(int level) {

    public static final PureLevel EMPTY = new PureLevel(-1);
    public static final PureLevel LOW = new PureLevel(0);
    public static final PureLevel SOVEREIGN = new PureLevel(5);

    public static final Codec<PureLevel> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.INT.fieldOf("level").forGetter(PureLevel::level)
    ).apply(inst, PureLevel::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, PureLevel> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, PureLevel::level,
            PureLevel::new
    );

    public static ItemStack pureBlood(ItemStack stack, int level) {
        stack.set(ModDataComponents.PURE_LEVEL, new PureLevel(level));
        return stack;
    }

    public static ItemStackTemplate template(Holder<Item> stack, int level) {
        return new ItemStackTemplate(stack, 1, DataComponentPatch.builder().set(ModDataComponents.PURE_LEVEL.get(), new PureLevel(level)).build());
    }

    public static ItemStack pureBlood(ItemLike item, int level) {
        return pureBlood(item.asItem().getDefaultInstance(), level);
    }

    public MutableComponent getPurityTooltip() {
        return formatByPurity(Component.translatable("tooltip.vampirism.purity", level() + 1));
    }

    public MutableComponent formatByPurity(MutableComponent component) {
        return level == 5 ? component.withStyle(VEnums.SOVEREIGN_STYLE) : component.withStyle(ChatFormatting.DARK_RED);
    }
}
