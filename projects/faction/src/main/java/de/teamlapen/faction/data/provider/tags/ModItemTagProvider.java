package de.teamlapen.faction.data.provider.tags;

import de.teamlapen.faction.api.util.REFERENCE;
import de.teamlapen.faction.common.tags.FactionItemTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ItemTagsProvider;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends ItemTagsProvider {

    public ModItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, REFERENCE.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        minionTool(FactionItemTags.MINION_AXE, ItemTags.AXES, Items.IRON_AXE, Items.DIAMOND_AXE, Items.NETHERITE_AXE);
        minionTool(FactionItemTags.MINION_PICKAXE, ItemTags.PICKAXES, Items.IRON_PICKAXE, Items.DIAMOND_PICKAXE, Items.NETHERITE_PICKAXE);
        minionTool(FactionItemTags.MINION_SHOVEL, ItemTags.SHOVELS, Items.IRON_SHOVEL, Items.DIAMOND_SHOVEL, Items.NETHERITE_SHOVEL);
        minionTool(FactionItemTags.MINION_SWORD, ItemTags.SWORDS, Items.IRON_SWORD, Items.DIAMOND_SWORD, Items.NETHERITE_SWORD);
        tag(FactionItemTags.MINION_FISHING_ROD).addTag(Tags.Items.TOOLS_FISHING_ROD);
    }

    /**
     * @param iron      tier 1 tool
     * @param diamond   tier 2 tool
     * @param netherite tier 2 tool
     */
    private void minionTool(FactionItemTags.MinionTool tool, TagKey<Item> base, Item iron, Item diamond, Item netherite) {
        tag(tool.base()).addTag(base).addTag(tool.tier1());
        tag(tool.tier1()).add(iron).addTag(tool.tier2());
        tag(tool.tier2()).add(diamond, netherite);
    }
}
