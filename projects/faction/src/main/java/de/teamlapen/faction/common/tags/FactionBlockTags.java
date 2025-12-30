package de.teamlapen.faction.common.tags;

import de.teamlapen.faction.api.util.FIdentifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class FactionBlockTags {

    public static final TagKey<Block> TOTEM_TOP_CRAFTED = tag("totem_top_crafted");
    public static final TagKey<Block> TOTEM_TOP_FRAGILE = tag("totem_top_fragile");
    public static final TagKey<Block> TOTEM_TOP = tag("totem_top");

    private static TagKey<Block> tag(String name) {
        return BlockTags.create(FIdentifier.mod(name));
    }
}
