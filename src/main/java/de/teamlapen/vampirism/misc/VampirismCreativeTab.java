package de.teamlapen.vampirism.misc;

import de.teamlapen.lib.lib.util.ModDisplayItemGenerator;
import de.teamlapen.vampirism.api.items.oil.IOil;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.core.ModOils;
import de.teamlapen.vampirism.core.ModRegistries;
import de.teamlapen.vampirism.util.OilUtils;
import de.teamlapen.vampirism.util.RegUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.ItemLike;

import java.util.Set;

import static de.teamlapen.vampirism.core.ModBlocks.*;
import static de.teamlapen.vampirism.core.ModItems.*;

public class VampirismCreativeTab {

    public static CreativeModeTab.Builder builder(Set<ItemLike> allItems) {
        return CreativeModeTab.builder()
                .title(Component.translatable("itemGroup.vampirism"))
                .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
                .icon(() -> VAMPIRE_FANG.get().getDefaultInstance())
                .displayItems(new VampirismDisplayItemGenerator(allItems));
    }

    public static class VampirismDisplayItemGenerator extends ModDisplayItemGenerator {

        public VampirismDisplayItemGenerator(Set<ItemLike> allItems) {
            super(allItems);
        }

        @Override
        protected void addItemsToOutput() {
            addItems();
            addBlocks();
        }

        private void addItems() {

            addWeapons();
            addArmor();

            addItemGen(BLOOD_BOTTLE);
            addItem(BLOOD_INFUSED_IRON_INGOT);
            addItem(BLOOD_INFUSED_ENHANCED_IRON_INGOT);

            addItem(GARLIC_DIFFUSER_CORE);
            addItem(GARLIC_DIFFUSER_CORE_IMPROVED);

            addItem(HOLY_WATER_BOTTLE_NORMAL);
            addItem(HOLY_WATER_BOTTLE_ENHANCED);
            addItem(HOLY_WATER_BOTTLE_ULTIMATE);
            addItem(HOLY_WATER_SPLASH_BOTTLE_NORMAL);
            addItem(HOLY_WATER_SPLASH_BOTTLE_ENHANCED);
            addItem(HOLY_WATER_SPLASH_BOTTLE_ULTIMATE);

            addItem(HUNTER_INTEL_0);
            addItem(HUNTER_INTEL_1);
            addItem(HUNTER_INTEL_2);
            addItem(HUNTER_INTEL_3);
            addItem(HUNTER_INTEL_4);
            addItem(HUNTER_INTEL_5);
            addItem(HUNTER_INTEL_6);
            addItem(HUNTER_INTEL_7);
            addItem(HUNTER_INTEL_8);
            addItem(HUNTER_INTEL_9);

            addItem(PURE_BLOOD_0);
            addItem(PURE_BLOOD_1);
            addItem(PURE_BLOOD_2);
            addItem(PURE_BLOOD_3);
            addItem(PURE_BLOOD_4);

            addItem(GARLIC_BREAD);
            addItem(HUMAN_HEART);
            addItem(WEAK_HUMAN_HEART);

            addItem(INJECTION_EMPTY);
            addItem(INJECTION_GARLIC);
            addItem(INJECTION_SANGUINARE);

            addItem(ITEM_ALCHEMICAL_FIRE);
            addItem(ITEM_TENT);
            addItem(ITEM_TENT_SPAWNER);

            addItem(ITEM_GARLIC);
            addItem(PURIFIED_GARLIC);
            addItem(PURE_SALT_WATER);
            addItem(PURE_SALT);

            addItem(SOUL_ORB_VAMPIRE);
            addItem(MOTHER_CORE);
            addItem(VAMPIRE_BLOOD_BOTTLE);
            addItemGen(VAMPIRE_BOOK);
            addItem(VAMPIRE_FANG);
            addItem(UMBRELLA);
            addItem(HUNTER_MINION_EQUIPMENT);
            addItem(HUNTER_MINION_UPGRADE_SIMPLE);
            addItem(HUNTER_MINION_UPGRADE_ENHANCED);
            addItem(HUNTER_MINION_UPGRADE_SPECIAL);
            addItem(VAMPIRE_MINION_BINDING);
            addItem(VAMPIRE_MINION_UPGRADE_SIMPLE);
            addItem(VAMPIRE_MINION_UPGRADE_ENHANCED);
            addItem(VAMPIRE_MINION_UPGRADE_SPECIAL);
            addItem(FEEDING_ADAPTER);
            addItem(OBLIVION_POTION);

            addItem(GARLIC_FINDER);
            addItem(DARK_SPRUCE_BOAT);
            addItem(CURSED_SPRUCE_BOAT);
            addItem(DARK_SPRUCE_CHEST_BOAT);
            addItem(CURSED_SPRUCE_CHEST_BOAT);

            for (IOil value : RegUtil.values(ModRegistries.OILS)) {
                if (value == ModOils.EMPTY.get()) continue;
                add(OilUtils.setOil(OIL_BOTTLE.get().getDefaultInstance(), value));
            }
        }

        private void addBlocks() {
            addBlock(ALCHEMICAL_CAULDRON);
            addBlock(ALTAR_INFUSION);
            addBlock(ALTAR_INSPIRATION);
            addBlock(ALTAR_PILLAR);
            addBlock(ALTAR_TIP);
            addBlockGen(BLOOD_CONTAINER);
            addBlock(BLOOD_GRINDER);
            addBlock(BLOOD_PEDESTAL);
            addBlock(BLOOD_SIEVE);
            addBlock(ALTAR_CLEANSING);
            addBlock(GARLIC_DIFFUSER_NORMAL);
            addBlock(GARLIC_DIFFUSER_WEAK);
            addBlock(GARLIC_DIFFUSER_IMPROVED);
            addBlock(FOG_DIFFUSER);
            addBlock(HUNTER_TABLE);
            addBlock(SUNSCREEN_BEACON);
            addBlock(VAMPIRE_BEACON);
            addBlock(TOTEM_BASE);
            addBlock(TOTEM_TOP);
            addBlock(TOTEM_TOP_CRAFTED);
            addBlock(WEAPON_TABLE);
            addBlock(POTION_TABLE);
            addBlock(MED_CHAIR);
            addBlock(ALCHEMY_TABLE);
            addBlock(VAMPIRE_BEACON);

            addBuildingBlocks();
            addPlantBlocks();
            addDecorativeBlocks();
        }

        private void addPlantBlocks() {
            addBlock(DARK_SPRUCE_SAPLING);
            addBlock(CURSED_SPRUCE_SAPLING);
            addBlock(CURSED_ROOTS);
            addBlock(CURSED_HANGING_ROOTS);
            addBlock(VAMPIRE_ORCHID);
            addBlock(DARK_SPRUCE_LEAVES);
            addBlock(DIRECT_CURSED_BARK);
        }

        private void addBuildingBlocks() {
            addBlock(CURSED_GRASS);
            addBlock(CURSED_EARTH);
            addBlock(CURSED_EARTH_PATH);

            addBlock(DARK_SPRUCE_LOG);
            addBlock(DARK_SPRUCE_WOOD);
            addBlock(STRIPPED_DARK_SPRUCE_LOG);
            addBlock(STRIPPED_DARK_SPRUCE_WOOD);
            addBlock(DARK_SPRUCE_PLANKS);
            addBlock(DARK_SPRUCE_STAIRS);
            addBlock(DARK_SPRUCE_SLAB);
            addBlock(DARK_SPRUCE_FENCE);
            addBlock(DARK_SPRUCE_FENCE_GATE);
            addBlock(DARK_SPRUCE_DOOR);
            addBlock(DARK_SPRUCE_TRAPDOOR);
            addBlock(DARK_SPRUCE_PRESSURE_PLACE);
            addBlock(DARK_SPRUCE_BUTTON);
            addItem(ModItems.DARK_SPRUCE_SIGN);
            addItem(ModItems.DARK_SPRUCE_HANGING_SIGN);

            addBlock(CURSED_SPRUCE_LOG);
            addBlock(CURSED_SPRUCE_LOG_CURED);
            addBlock(CURSED_SPRUCE_WOOD);
            addBlock(CURSED_SPRUCE_WOOD_CURED);
            addBlock(STRIPPED_CURSED_SPRUCE_LOG);
            addBlock(STRIPPED_CURSED_SPRUCE_WOOD);
            addBlock(CURSED_SPRUCE_PLANKS);
            addBlock(CURSED_SPRUCE_STAIRS);
            addBlock(CURSED_SPRUCE_SLAB);
            addBlock(CURSED_SPRUCE_FENCE);
            addBlock(CURSED_SPRUCE_FENCE_GATE);
            addBlock(CURSED_SPRUCE_DOOR);
            addBlock(CURSED_SPRUCE_TRAPDOOR);
            addBlock(CURSED_SPRUCE_PRESSURE_PLACE);
            addBlock(CURSED_SPRUCE_BUTTON);
            addItem(ModItems.CURSED_SPRUCE_SIGN);
            addItem(ModItems.CURSED_SPRUCE_HANGING_SIGN);

            addBlock(DARK_STONE);
            addBlock(DARK_STONE_STAIRS);
            addBlock(DARK_STONE_SLAB);
            addBlock(DARK_STONE_WALL);
            addBlock(INFESTED_DARK_STONE);
            addBlock(DARK_STONE_BRICKS);
            addBlock(DARK_STONE_BRICK_STAIRS);
            addBlock(DARK_STONE_BRICK_SLAB);
            addBlock(DARK_STONE_BRICK_WALL);
            addBlock(CRACKED_DARK_STONE_BRICKS);
            addBlock(CHISELED_DARK_STONE_BRICKS);
            addBlock(BLOODY_DARK_STONE_BRICKS);
            addBlock(COBBLED_DARK_STONE);
            addBlock(COBBLED_DARK_STONE_STAIRS);
            addBlock(COBBLED_DARK_STONE_SLAB);
            addBlock(COBBLED_DARK_STONE_WALL);
            addBlock(POLISHED_DARK_STONE);
            addBlock(POLISHED_DARK_STONE_STAIRS);
            addBlock(POLISHED_DARK_STONE_SLAB);
            addBlock(POLISHED_DARK_STONE_WALL);
            addBlock(DARK_STONE_TILES);
            addBlock(DARK_STONE_TILES_STAIRS);
            addBlock(DARK_STONE_TILES_SLAB);
            addBlock(DARK_STONE_TILES_WALL);
            addBlock(CRACKED_DARK_STONE_TILES);
            addBlock(PURPLE_STONE_BRICKS);
            addBlock(PURPLE_STONE_BRICK_STAIRS);
            addBlock(PURPLE_STONE_BRICK_SLAB);
            addBlock(PURPLE_STONE_BRICK_WALL);
            addBlock(PURPLE_STONE_TILES);
            addBlock(PURPLE_STONE_TILES_STAIRS);
            addBlock(PURPLE_STONE_TILES_SLAB);
            addBlock(PURPLE_STONE_TILES_WALL);
            addBlock(BLOOD_INFUSED_IRON_BLOCK);
            addBlock(BLOOD_INFUSED_ENHANCED_IRON_BLOCK);
        }

        private void addDecorativeBlocks() {
            addBlock(FIRE_PLACE);
            addBlock(CHANDELIER);
            addBlock(CROSS);
            addBlock(TOMBSTONE1);
            addBlock(TOMBSTONE2);
            addBlock(TOMBSTONE3);
            addBlock(GRAVE_CAGE);
            addBlock(VAMPIRE_RACK);
            addBlock(THRONE);
            addItem(ITEM_CANDELABRA);
            addBlock(BAT_CAGE);
            addBlock(MOTHER_TROPHY);

            addBlock(COFFIN_WHITE);
            addBlock(COFFIN_LIGHT_GRAY);
            addBlock(COFFIN_GRAY);
            addBlock(COFFIN_BLACK);
            addBlock(COFFIN_BROWN);
            addBlock(COFFIN_RED);
            addBlock(COFFIN_ORANGE);
            addBlock(COFFIN_YELLOW);
            addBlock(COFFIN_LIME);
            addBlock(COFFIN_GREEN);
            addBlock(COFFIN_LIGHT_BLUE);
            addBlock(COFFIN_CYAN);
            addBlock(COFFIN_BLUE);
            addBlock(COFFIN_PURPLE);
            addBlock(COFFIN_MAGENTA);
            addBlock(COFFIN_PINK);
        }

        private void addArmor() {
            addItem(ARMOR_OF_SWIFTNESS_HEAD_NORMAL);
            addItem(ARMOR_OF_SWIFTNESS_CHEST_NORMAL);
            addItem(ARMOR_OF_SWIFTNESS_LEGS_NORMAL);
            addItem(ARMOR_OF_SWIFTNESS_FEET_NORMAL);
            addItem(ARMOR_OF_SWIFTNESS_HEAD_ENHANCED);
            addItem(ARMOR_OF_SWIFTNESS_CHEST_ENHANCED);
            addItem(ARMOR_OF_SWIFTNESS_LEGS_ENHANCED);
            addItem(ARMOR_OF_SWIFTNESS_FEET_ENHANCED);
            addItem(ARMOR_OF_SWIFTNESS_HEAD_ULTIMATE);
            addItem(ARMOR_OF_SWIFTNESS_CHEST_ULTIMATE);
            addItem(ARMOR_OF_SWIFTNESS_LEGS_ULTIMATE);
            addItem(ARMOR_OF_SWIFTNESS_FEET_ULTIMATE);

            addItem(HUNTER_COAT_HEAD_NORMAL);
            addItem(HUNTER_COAT_CHEST_NORMAL);
            addItem(HUNTER_COAT_LEGS_NORMAL);
            addItem(HUNTER_COAT_FEET_NORMAL);
            addItem(HUNTER_COAT_HEAD_ENHANCED);
            addItem(HUNTER_COAT_CHEST_ENHANCED);
            addItem(HUNTER_COAT_LEGS_ENHANCED);
            addItem(HUNTER_COAT_FEET_ENHANCED);
            addItem(HUNTER_COAT_HEAD_ULTIMATE);
            addItem(HUNTER_COAT_CHEST_ULTIMATE);
            addItem(HUNTER_COAT_LEGS_ULTIMATE);
            addItem(HUNTER_COAT_FEET_ULTIMATE);

            addItem(HUNTER_HAT_HEAD_0);
            addItem(HUNTER_HAT_HEAD_1);


            addItem(VAMPIRE_CLOTHING_CROWN);
            addItem(VAMPIRE_CLOTHING_HAT);
            addItem(VAMPIRE_CLOAK_BLACK_BLUE);
            addItem(VAMPIRE_CLOAK_BLACK_RED);
            addItem(VAMPIRE_CLOAK_BLACK_WHITE);
            addItem(VAMPIRE_CLOAK_RED_BLACK);
            addItem(VAMPIRE_CLOAK_WHITE_BLACK);
            addItem(VAMPIRE_CLOTHING_LEGS);
            addItem(VAMPIRE_CLOTHING_BOOTS);

            addItemGen(AMULET);
            addItemGen(RING);
            addItemGen(OBI_BELT);
        }

        private void addWeapons() {
            addItem(HEART_SEEKER_NORMAL);
            addItem(HEART_SEEKER_ENHANCED);
            addItem(HEART_SEEKER_ULTIMATE);
            addItem(HEART_STRIKER_NORMAL);
            addItem(HEART_STRIKER_ENHANCED);
            addItem(HEART_STRIKER_ULTIMATE);

            addItemGen(HUNTER_AXE_NORMAL);
            addItemGen(HUNTER_AXE_ENHANCED);
            addItemGen(HUNTER_AXE_ULTIMATE);

            addItem(BASIC_CROSSBOW);
            addItem(BASIC_DOUBLE_CROSSBOW);
            addItem(ENHANCED_CROSSBOW);
            addItem(ENHANCED_DOUBLE_CROSSBOW);
            addItem(BASIC_TECH_CROSSBOW);
            addItem(ENHANCED_TECH_CROSSBOW);

            addItem(CROSSBOW_ARROW_NORMAL);
            addItem(CROSSBOW_ARROW_SPITFIRE);
            addItem(CROSSBOW_ARROW_GARLIC);
            addItem(CROSSBOW_ARROW_VAMPIRE_KILLER);
            addItem(CROSSBOW_ARROW_TELEPORT);
            addItem(CROSSBOW_ARROW_BLEEDING);
            addItem(ARROW_CLIP);

            addItem(PITCHFORK);
            addItem(STAKE);

            addItem(CRUCIFIX_NORMAL);
            addItem(CRUCIFIX_ENHANCED);
            addItem(CRUCIFIX_ULTIMATE);
        }
    }
}
