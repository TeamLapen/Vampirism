package de.teamlapen.faction.api.factions.refinements;

import com.mojang.serialization.Codec;
import de.teamlapen.faction.api.tags.FactionRefinementSetTags;
import net.minecraft.ChatFormatting;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;

import java.util.Locale;

public enum Rarity implements StringRepresentable {
    COMMON(4, ChatFormatting.WHITE, FactionRefinementSetTags.COMMON),
    UNCOMMON(3, ChatFormatting.GREEN, FactionRefinementSetTags.UNCOMMON),
    RARE(3, ChatFormatting.BLUE, FactionRefinementSetTags.RARE),
    EPIC(2, ChatFormatting.DARK_PURPLE, FactionRefinementSetTags.EPIC),
    LEGENDARY(1, ChatFormatting.GOLD, FactionRefinementSetTags.LEGENDARY);

    public static final Codec<Rarity> CODEC = StringRepresentable.fromEnum(Rarity::values);

    public final int weight;
    public final ChatFormatting color;
    public final TagKey<IRefinementSet> tag;

    Rarity(int weight, ChatFormatting color, TagKey<IRefinementSet> tag) {
        this.weight = weight;
        this.color = color;
        this.tag = tag;
    }

    @Override
    public String getSerializedName() {
        return name().toLowerCase(Locale.ROOT);
    }
}
