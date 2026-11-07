package de.teamlapen.vampirism.common.world.items.component;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.ChatFormatting;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

import java.util.function.IntFunction;

/**
 * Dimension specific variants of the ritual knife used for the Velmorra portal ritual.
 */
public enum RitualKnifeType implements StringRepresentable {
    /**
     * Overworld knife, finds the dimension
     */
    SEEKING(0, "seeking", ChatFormatting.DARK_AQUA),
    /**
     * End knife, provides the magic teleport
     */
    PASSAGE(1, "passage", ChatFormatting.LIGHT_PURPLE),
    /**
     * Nether knife, anchors the portal
     */
    ANCHORING(2, "anchoring", ChatFormatting.RED);

    public static final Codec<RitualKnifeType> CODEC = StringRepresentable.fromEnum(RitualKnifeType::values);
    private static final IntFunction<RitualKnifeType> BY_ID = ByIdMap.continuous(RitualKnifeType::getId, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
    public static final StreamCodec<ByteBuf, RitualKnifeType> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, RitualKnifeType::getId);

    private final int id;
    private final String name;
    private final ChatFormatting color;

    RitualKnifeType(int id, String name, ChatFormatting color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public int getId() {
        return this.id;
    }

    public ChatFormatting getColor() {
        return this.color;
    }

    public String getTranslationKey() {
        return "item.vampirism.ritual_knife." + this.name;
    }

    public String getTooltipKey() {
        return "tooltip.vampirism.ritual_knife." + this.name;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}
