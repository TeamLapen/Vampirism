package de.teamlapen.faction.common.tags;

import de.teamlapen.faction.api.util.FIdentifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class FactionItemTags {

    /**
     * Tools a minion can use for the collect resource tasks. Each tool type has a base tag and two tier tags that unlock better loot pools.
     * Higher tier tags are contained in the lower tier tags.
     */
    public static final MinionTool MINION_AXE = new MinionTool("axe");
    public static final MinionTool MINION_PICKAXE = new MinionTool("pickaxe");
    public static final MinionTool MINION_SHOVEL = new MinionTool("shovel");
    public static final MinionTool MINION_SWORD = new MinionTool("sword");
    public static final TagKey<Item> MINION_FISHING_ROD = tag("minion_tools/fishing_rod");

    private static TagKey<Item> tag(String name) {
        return ItemTags.create(FIdentifier.mod(name));
    }

    public record MinionTool(TagKey<Item> base, TagKey<Item> tier1, TagKey<Item> tier2) {

        private MinionTool(String name) {
            this(tag("minion_tools/" + name), tag("minion_tools/" + name + "/tier_1"), tag("minion_tools/" + name + "/tier_2"));
        }
    }
}
