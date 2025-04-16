package de.teamlapen.vampirism.util;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import de.teamlapen.vampirism.blocks.CoffinBlock;
import de.teamlapen.vampirism.blocks.candle.CandleHolderBlock;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.items.VampireCloakItem;
import net.minecraft.Util;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.Map;

public class ColorListsUtil {

    public static final List<Pair<CandleHolderBlock, CandleHolderBlock>> STANDING_AND_WALL_CANDLE_STICKS = List.of(Pair.of(ModBlocks.CANDLE_STICK.get(), ModBlocks.WALL_CANDLE_STICK.get()), Pair.of(ModBlocks.CANDLE_STICK_NORMAL.get(), ModBlocks.WALL_CANDLE_STICK_NORMAL.get()), Pair.of(ModBlocks.CANDLE_STICK_WHITE.get(), ModBlocks.WALL_CANDLE_STICK_WHITE.get()), Pair.of(ModBlocks.CANDLE_STICK_LIGHT_GRAY.get(), ModBlocks.WALL_CANDLE_STICK_LIGHT_GRAY.get()), Pair.of(ModBlocks.CANDLE_STICK_GRAY.get(), ModBlocks.WALL_CANDLE_STICK_GRAY.get()), Pair.of(ModBlocks.CANDLE_STICK_BLACK.get(), ModBlocks.WALL_CANDLE_STICK_BLACK.get()), Pair.of(ModBlocks.CANDLE_STICK_BROWN.get(), ModBlocks.WALL_CANDLE_STICK_BROWN.get()), Pair.of(ModBlocks.CANDLE_STICK_RED.get(), ModBlocks.WALL_CANDLE_STICK_RED.get()), Pair.of(ModBlocks.CANDLE_STICK_ORANGE.get(), ModBlocks.WALL_CANDLE_STICK_ORANGE.get()), Pair.of(ModBlocks.CANDLE_STICK_YELLOW.get(), ModBlocks.WALL_CANDLE_STICK_YELLOW.get()), Pair.of(ModBlocks.CANDLE_STICK_LIME.get(), ModBlocks.WALL_CANDLE_STICK_LIME.get()), Pair.of(ModBlocks.CANDLE_STICK_GREEN.get(), ModBlocks.WALL_CANDLE_STICK_GREEN.get()), Pair.of(ModBlocks.CANDLE_STICK_CYAN.get(), ModBlocks.WALL_CANDLE_STICK_CYAN.get()), Pair.of(ModBlocks.CANDLE_STICK_LIGHT_BLUE.get(), ModBlocks.WALL_CANDLE_STICK_LIGHT_BLUE.get()), Pair.of(ModBlocks.CANDLE_STICK_BLUE.get(), ModBlocks.WALL_CANDLE_STICK_BLUE.get()), Pair.of(ModBlocks.CANDLE_STICK_MAGENTA.get(), ModBlocks.WALL_CANDLE_STICK_MAGENTA.get()), Pair.of(ModBlocks.CANDLE_STICK_PURPLE.get(), ModBlocks.WALL_CANDLE_STICK_PURPLE.get()), Pair.of(ModBlocks.CANDLE_STICK_PINK.get(), ModBlocks.WALL_CANDLE_STICK_PINK.get()));
    public static final List<Pair<CandleHolderBlock, CandleHolderBlock>> STANDING_AND_WALL_CANDELABRAS = List.of(Pair.of(ModBlocks.CANDELABRA.get(), ModBlocks.WALL_CANDELABRA.get()), Pair.of(ModBlocks.CANDELABRA_NORMAL.get(), ModBlocks.WALL_CANDELABRA_NORMAL.get()), Pair.of(ModBlocks.CANDELABRA_WHITE.get(), ModBlocks.WALL_CANDELABRA_WHITE.get()), Pair.of(ModBlocks.CANDELABRA_LIGHT_GRAY.get(), ModBlocks.WALL_CANDELABRA_LIGHT_GRAY.get()), Pair.of(ModBlocks.CANDELABRA_GRAY.get(), ModBlocks.WALL_CANDELABRA_GRAY.get()), Pair.of(ModBlocks.CANDELABRA_BLACK.get(), ModBlocks.WALL_CANDELABRA_BLACK.get()), Pair.of(ModBlocks.CANDELABRA_BROWN.get(), ModBlocks.WALL_CANDELABRA_BROWN.get()), Pair.of(ModBlocks.CANDELABRA_RED.get(), ModBlocks.WALL_CANDELABRA_RED.get()), Pair.of(ModBlocks.CANDELABRA_ORANGE.get(), ModBlocks.WALL_CANDELABRA_ORANGE.get()), Pair.of(ModBlocks.CANDELABRA_YELLOW.get(), ModBlocks.WALL_CANDELABRA_YELLOW.get()), Pair.of(ModBlocks.CANDELABRA_LIME.get(), ModBlocks.WALL_CANDELABRA_LIME.get()), Pair.of(ModBlocks.CANDELABRA_GREEN.get(), ModBlocks.WALL_CANDELABRA_GREEN.get()), Pair.of(ModBlocks.CANDELABRA_CYAN.get(), ModBlocks.WALL_CANDELABRA_CYAN.get()), Pair.of(ModBlocks.CANDELABRA_LIGHT_BLUE.get(), ModBlocks.WALL_CANDELABRA_LIGHT_BLUE.get()), Pair.of(ModBlocks.CANDELABRA_BLUE.get(), ModBlocks.WALL_CANDELABRA_BLUE.get()), Pair.of(ModBlocks.CANDELABRA_MAGENTA.get(), ModBlocks.WALL_CANDELABRA_MAGENTA.get()), Pair.of(ModBlocks.CANDELABRA_PURPLE.get(), ModBlocks.WALL_CANDELABRA_PURPLE.get()), Pair.of(ModBlocks.CANDELABRA_PINK.get(), ModBlocks.WALL_CANDELABRA_PINK.get()));
    public static final List<CandleHolderBlock> HANGING_CHANDELIERS = List.of(ModBlocks.CHANDELIER.get(), ModBlocks.CHANDELIER_NORMAL.get(), ModBlocks.CHANDELIER_WHITE.get(), ModBlocks.CHANDELIER_LIGHT_GRAY.get(), ModBlocks.CHANDELIER_GRAY.get(), ModBlocks.CHANDELIER_BLACK.get(), ModBlocks.CHANDELIER_BROWN.get(), ModBlocks.CHANDELIER_RED.get(), ModBlocks.CHANDELIER_ORANGE.get(), ModBlocks.CHANDELIER_YELLOW.get(), ModBlocks.CHANDELIER_LIME.get(), ModBlocks.CHANDELIER_GREEN.get(), ModBlocks.CHANDELIER_CYAN.get(), ModBlocks.CHANDELIER_LIGHT_BLUE.get(), ModBlocks.CHANDELIER_BLUE.get(), ModBlocks.CHANDELIER_MAGENTA.get(), ModBlocks.CHANDELIER_PURPLE.get(), ModBlocks.CHANDELIER_PINK.get());

    public static final List<CoffinBlock> COFFINS = List.of(ModBlocks.COFFIN_BLACK.get(), ModBlocks.COFFIN_BLUE.get(), ModBlocks.COFFIN_BROWN.get(), ModBlocks.COFFIN_CYAN.get(), ModBlocks.COFFIN_GRAY.get(), ModBlocks.COFFIN_GREEN.get(), ModBlocks.COFFIN_LIGHT_BLUE.get(), ModBlocks.COFFIN_LIGHT_GRAY.get(), ModBlocks.COFFIN_LIME.get(), ModBlocks.COFFIN_MAGENTA.get(), ModBlocks.COFFIN_ORANGE.get(), ModBlocks.COFFIN_PINK.get(), ModBlocks.COFFIN_PURPLE.get(), ModBlocks.COFFIN_RED.get(), ModBlocks.COFFIN_YELLOW.get(), ModBlocks.COFFIN_WHITE.get());

    public static final List<VampireCloakItem> VAMPIRE_CLOAKS = List.of(ModItems.VAMPIRE_CLOAK_BLACK.get(), ModItems.VAMPIRE_CLOAK_BLUE.get(), ModItems.VAMPIRE_CLOAK_BROWN.get(), ModItems.VAMPIRE_CLOAK_CYAN.get(), ModItems.VAMPIRE_CLOAK_GRAY.get(), ModItems.VAMPIRE_CLOAK_GREEN.get(), ModItems.VAMPIRE_CLOAK_LIGHT_BLUE.get(), ModItems.VAMPIRE_CLOAK_LIGHT_GRAY.get(), ModItems.VAMPIRE_CLOAK_LIME.get(), ModItems.VAMPIRE_CLOAK_MAGENTA.get(), ModItems.VAMPIRE_CLOAK_ORANGE.get(), ModItems.VAMPIRE_CLOAK_PINK.get(), ModItems.VAMPIRE_CLOAK_PURPLE.get(), ModItems.VAMPIRE_CLOAK_RED.get(), ModItems.VAMPIRE_CLOAK_YELLOW.get(), ModItems.VAMPIRE_CLOAK_WHITE.get());

    public static final Map<DyeColor, Item> DYED_WOOL = Util.make(Maps.newEnumMap(DyeColor.class), map -> {
        map.put(DyeColor.WHITE, Items.WHITE_WOOL);
        map.put(DyeColor.ORANGE, Items.ORANGE_WOOL);
        map.put(DyeColor.MAGENTA, Items.MAGENTA_WOOL);
        map.put(DyeColor.LIGHT_BLUE, Items.LIGHT_BLUE_WOOL);
        map.put(DyeColor.YELLOW, Items.YELLOW_WOOL);
        map.put(DyeColor.LIME, Items.LIME_WOOL);
        map.put(DyeColor.PINK, Items.PINK_WOOL);
        map.put(DyeColor.GRAY, Items.GRAY_WOOL);
        map.put(DyeColor.LIGHT_GRAY, Items.LIGHT_GRAY_WOOL);
        map.put(DyeColor.CYAN, Items.CYAN_WOOL);
        map.put(DyeColor.PURPLE, Items.PURPLE_WOOL);
        map.put(DyeColor.BLUE, Items.BLUE_WOOL);
        map.put(DyeColor.BROWN, Items.BROWN_WOOL);
        map.put(DyeColor.GREEN, Items.GREEN_WOOL);
        map.put(DyeColor.RED, Items.RED_WOOL);
        map.put(DyeColor.BLACK, Items.BLACK_WOOL);
    });

    public static final Map<DyeColor, Item> DYE_ITEMS = Util.make(Maps.newEnumMap(DyeColor.class), map -> {
        map.put(DyeColor.WHITE, Items.WHITE_DYE);
        map.put(DyeColor.ORANGE, Items.ORANGE_DYE);
        map.put(DyeColor.MAGENTA, Items.MAGENTA_DYE);
        map.put(DyeColor.LIGHT_BLUE, Items.LIGHT_BLUE_DYE);
        map.put(DyeColor.YELLOW, Items.YELLOW_DYE);
        map.put(DyeColor.LIME, Items.LIME_DYE);
        map.put(DyeColor.PINK, Items.PINK_DYE);
        map.put(DyeColor.GRAY, Items.GRAY_DYE);
        map.put(DyeColor.LIGHT_GRAY, Items.LIGHT_GRAY_DYE);
        map.put(DyeColor.CYAN, Items.CYAN_DYE);
        map.put(DyeColor.PURPLE, Items.PURPLE_DYE);
        map.put(DyeColor.BLUE, Items.BLUE_DYE);
        map.put(DyeColor.BROWN, Items.BROWN_DYE);
        map.put(DyeColor.GREEN, Items.GREEN_DYE);
        map.put(DyeColor.RED, Items.RED_DYE);
        map.put(DyeColor.BLACK, Items.BLACK_DYE);
    });

    public static final List<Item> DYES = List.of(Items.BLACK_DYE, Items.BLUE_DYE, Items.BROWN_DYE, Items.CYAN_DYE, Items.GRAY_DYE, Items.GREEN_DYE, Items.LIGHT_BLUE_DYE, Items.LIGHT_GRAY_DYE, Items.LIME_DYE, Items.MAGENTA_DYE, Items.ORANGE_DYE, Items.PINK_DYE, Items.PURPLE_DYE, Items.RED_DYE, Items.YELLOW_DYE, Items.WHITE_DYE);

    public static ItemStack[] itemListToItemStacks(List<? extends Item> list) {
        ItemStack[] array = new ItemStack[list.size()];
        for (int i = 0; i < array.length; i++) {
            array[i] = new ItemStack(list.get(i));
        }
        return array;
    }
}
