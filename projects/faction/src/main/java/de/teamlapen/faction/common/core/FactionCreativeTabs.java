package de.teamlapen.faction.common.core;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.OminousBottleAmplifier;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

public class FactionCreativeTabs {

    public static void addToExistingCreativeTabs(BuildCreativeModeTabContentsEvent event) {
        ItemStack maxOminousBottle = new ItemStack(Items.OMINOUS_BOTTLE);
        maxOminousBottle.set(DataComponents.OMINOUS_BOTTLE_AMPLIFIER, new OminousBottleAmplifier(4));

        if (event.getTabKey().equals(CreativeModeTabs.FOOD_AND_DRINKS)) {
            insertAfter(FactionItems.OBLIVION_POTION.get(), maxOminousBottle, event);
        } else if (event.getTabKey().equals(CreativeModeTabs.FUNCTIONAL_BLOCKS)) {
            insertAfter(FactionBlocks.TOTEM_BASE.get(), Blocks.BELL, event);
            insertAfter(FactionBlocks.TOTEM_TOP.get(), FactionBlocks.TOTEM_BASE.get(), event);
            insertAfter(FactionBlocks.TOTEM_TOP_CRAFTED.get(), FactionBlocks.TOTEM_TOP.get(), event);
        }
    }

    private static void insertAfter(ItemLike item, ItemStack insertAfterItem, BuildCreativeModeTabContentsEvent event) {
        event.insertAfter(insertAfterItem, new ItemStack(item), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
    }

    private static void insertAfter(ItemLike item, ItemLike insertAfterItem, BuildCreativeModeTabContentsEvent event) {
        event.insertAfter(new ItemStack(insertAfterItem), new ItemStack(item), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
    }
}
