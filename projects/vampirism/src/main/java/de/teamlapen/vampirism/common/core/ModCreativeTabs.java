package de.teamlapen.vampirism.common.core;

import de.teamlapen.factions.common.core.FactionItems;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.common.util.ColorListsUtil;
import de.teamlapen.vampirism.common.util.ItemDataUtils;
import de.teamlapen.vampirism.common.world.items.BaseDisplayItemGenerator;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

import static de.teamlapen.vampirism.common.core.ModBlocks.*;
import static de.teamlapen.vampirism.common.core.ModItems.*;

@SuppressWarnings("unused")
public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, REFERENCE.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> VAMPIRISM_TAB = CREATIVE_TABS.register(REFERENCE.MODID,
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.vampirism"))
                    .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
                    .icon(() -> VAMPIRE_FANG.get().getDefaultInstance())
                    .displayItems(new VampirismDisplayItemGenerator())
                    .build());

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> VAMPIRE_TAB = CREATIVE_TABS.register(REFERENCE.MODID + "_vampire", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.vampirism.vampire"))
            .withTabsBefore(VAMPIRISM_TAB.getKey())
            .icon(VAMPIRE_MINION_BINDING::toStack)
            .displayItems(new VampireDisplayItemGenerator())
            .build());
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> HUNTER_TAB = CREATIVE_TABS.register(REFERENCE.MODID + "_hunter", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.vampirism.hunter"))
            .withTabsBefore(VAMPIRISM_TAB.getKey())
            .icon(HUNTER_MINION_EQUIPMENT::toStack)
            .displayItems(new HunterDisplayItemGenerator())
            .build());

    public static class VampirismDisplayItemGenerator extends BaseDisplayItemGenerator {

        @Override
        protected void addAll() {
            addItems();
            addBlocks();
        }

        private void addItems() {
            add(GARLIC_BREAD);
            add(HUMAN_HEART);
            add(WEAK_HUMAN_HEART);

            add(SYRINGE_EMPTY);
            add(SYRINGE_BLOOD);
            add(INJECTION_GARLIC);
            add(INJECTION_SANGUINARE);

            add(SOUL_ORB_VAMPIRE);
            add(MOTHER_CORE);
            add(VAMPIRE_BLOOD_BOTTLE);
            addItemGen(VAMPIRE_BOOK);
            addIfPresent(VResourceLocation.loc(REFERENCE.GUIDEAPI_MODID, REFERENCE.GUIDEBOOK_ID));
            add(VAMPIRE_FANG);

            add(GARLIC_FINDER);

            add(DARK_SPRUCE_BOAT);
            add(CURSED_SPRUCE_BOAT);
            add(DARK_SPRUCE_CHEST_BOAT);
            add(CURSED_SPRUCE_CHEST_BOAT);
        }

        private void addBlocks() {
            addPlants();
            addBuildingBlocks();
            addDecorativeBlocks();
        }

        private void addPlants() {
            add(DARK_SPRUCE_LEAVES);

            add(DARK_SPRUCE_SAPLING);
            add(CURSED_SPRUCE_SAPLING);

            add(VAMPIRE_ORCHID);

            add(CURSED_ROOTS);
            add(CURSED_HANGING_ROOTS);
            add(DIRECT_CURSED_BARK);

            add(GARLIC);
        }

        private void addBuildingBlocks() {
            add(CURSED_GRASS);
            add(CURSED_EARTH);
            add(CURSED_EARTH_PATH);

            add(DARK_SPRUCE_LOG);
            add(DARK_SPRUCE_WOOD);
            add(STRIPPED_DARK_SPRUCE_LOG);
            add(STRIPPED_DARK_SPRUCE_WOOD);
            add(DARK_SPRUCE_PLANKS);
            add(DARK_SPRUCE_STAIRS);
            add(DARK_SPRUCE_SLAB);
            add(DARK_SPRUCE_FENCE);
            add(DARK_SPRUCE_FENCE_GATE);
            add(DARK_SPRUCE_DOOR);
            add(DARK_SPRUCE_TRAPDOOR);
            add(DARK_SPRUCE_PRESSURE_PLACE);
            add(DARK_SPRUCE_BUTTON);
            add(ModItems.DARK_SPRUCE_SIGN);
            add(ModItems.DARK_SPRUCE_HANGING_SIGN);

            add(CURSED_SPRUCE_LOG);
            add(CURSED_SPRUCE_WOOD);
            add(CURSED_SPRUCE_LOG.get().getActiveBlockItem());
            add(CURSED_SPRUCE_WOOD.get().getActiveBlockItem());
            add(STRIPPED_CURSED_SPRUCE_LOG);
            add(STRIPPED_CURSED_SPRUCE_WOOD);
            add(CURSED_SPRUCE_PLANKS);
            add(CURSED_SPRUCE_STAIRS);
            add(CURSED_SPRUCE_SLAB);
            add(CURSED_SPRUCE_FENCE);
            add(CURSED_SPRUCE_FENCE_GATE);
            add(CURSED_SPRUCE_DOOR);
            add(CURSED_SPRUCE_TRAPDOOR);
            add(CURSED_SPRUCE_PRESSURE_PLACE);
            add(CURSED_SPRUCE_BUTTON);
            add(ModItems.CURSED_SPRUCE_SIGN);
            add(ModItems.CURSED_SPRUCE_HANGING_SIGN);

            add(DARK_STONE);
            add(DARK_STONE_STAIRS);
            add(DARK_STONE_SLAB);
            add(DARK_STONE_WALL);
            add(INFESTED_DARK_STONE);

            add(DARK_STONE_BRICKS);
            add(DARK_STONE_BRICK_STAIRS);
            add(DARK_STONE_BRICK_SLAB);
            add(DARK_STONE_BRICK_WALL);
            add(CRACKED_DARK_STONE_BRICKS);
            add(CHISELED_DARK_STONE_BRICKS);
            add(BLOODY_DARK_STONE_BRICKS);

            add(COBBLED_DARK_STONE);
            add(COBBLED_DARK_STONE_STAIRS);
            add(COBBLED_DARK_STONE_SLAB);
            add(COBBLED_DARK_STONE_WALL);

            add(POLISHED_DARK_STONE);
            add(POLISHED_DARK_STONE_STAIRS);
            add(POLISHED_DARK_STONE_SLAB);
            add(POLISHED_DARK_STONE_WALL);

            add(DARK_STONE_TILES);
            add(DARK_STONE_TILES_STAIRS);
            add(DARK_STONE_TILES_SLAB);
            add(DARK_STONE_TILES_WALL);
            add(CRACKED_DARK_STONE_TILES);

            add(PURPLE_STONE_BRICKS);
            add(PURPLE_STONE_BRICK_STAIRS);
            add(PURPLE_STONE_BRICK_SLAB);
            add(PURPLE_STONE_BRICK_WALL);

            add(PURPLE_STONE_TILES);
            add(PURPLE_STONE_TILES_STAIRS);
            add(PURPLE_STONE_TILES_SLAB);
            add(PURPLE_STONE_TILES_WALL);

            add(BLOOD_INFUSED_IRON_BLOCK);
            add(BLOOD_INFUSED_ENHANCED_IRON_BLOCK);
        }

        private void addDecorativeBlocks() {
            add(FIRE_PLACE);
            addCandleHolders(ColorListsUtil.STANDING_AND_WALL_CANDLE_STICKS.stream().map(pair -> pair.getFirst().asItem()).toList());
            addCandleHolders(ColorListsUtil.STANDING_AND_WALL_CANDELABRAS.stream().map(pair -> pair.getFirst().asItem()).toList());
            addCandleHolders(ColorListsUtil.HANGING_CHANDELIERS.stream().map(Block::asItem).toList());
            add(VAMPIRE_SOUL_LANTERN);
            add(CROSS);
            add(TOMBSTONE1);
            add(TOMBSTONE2);
            add(TOMBSTONE3);
            add(GRAVE_CAGE);
            add(VAMPIRE_RACK);
            add(THRONE);
            add(BAT_CAGE);
            add(MOTHER_TROPHY);

            add(ITEM_TENT);
            add(ITEM_TENT_SPAWNER);

        }

        private void addCandleHolders(List<Item> allCandles) {
            for (int i = 0; i < allCandles.size(); i++) {
                output.accept(allCandles.get(i), i <= 1 ? CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS : CreativeModeTab.TabVisibility.SEARCH_TAB_ONLY);
            }
        }
    }

    public static class VampireDisplayItemGenerator extends BaseDisplayItemGenerator {

        @Override
        protected void addAll() {
            addItems();
            addBlocks();
        }

        private void addItems() {
            addWeapons();
            addArmor();

            addItemGen(BLOOD_BOTTLE);
            addItemGen(BLOOD_INFUSED_RAW_IRON);
            addItemGen(BLOOD_INFUSED_RAW_GOLD);
            addItemGen(BLOOD_INFUSED_IRON_INGOT);
            addItemGen(BLOOD_INFUSED_GOLD_INGOT);
            addItemGen(BLOOD_INFUSED_DIAMOND);
            addItemGen(BLOOD_INFUSED_NETHERITE_INGOT);

            add(PURE_BLOOD_0);
            add(PURE_BLOOD_1);
            add(PURE_BLOOD_2);
            add(PURE_BLOOD_3);
            add(PURE_BLOOD_4);

            add(VAMPIRE_MINION_BINDING);
            add(VAMPIRE_MINION_UPGRADE_SIMPLE);
            add(VAMPIRE_MINION_UPGRADE_ENHANCED);
            add(VAMPIRE_MINION_UPGRADE_SPECIAL);

            add(UMBRELLA);
            add(FABRIC_FILTER);
            add(FEEDING_ADAPTER);

            ModRegistries.OILS.listElements().filter(s -> !s.is(ModOils.EMPTY)).map(s -> ItemDataUtils.createOil(OIL_BOTTLE.get(), s)).forEach(this::add);
        }

        private void addWeapons() {
            addItemGen(HEART_SEEKER_NORMAL);
            addItemGen(HEART_SEEKER_ENHANCED);
            addItemGen(HEART_SEEKER_ULTIMATE);
            addItemGen(HEART_STRIKER_NORMAL);
            addItemGen(HEART_STRIKER_ENHANCED);
            addItemGen(HEART_STRIKER_ULTIMATE);
        }

        private void addArmor() {
            add(VAMPIRE_CLOTHING_CROWN);
            add(VAMPIRE_CLOTHING_HAT);

            ColorListsUtil.sortByTabOrder(ColorListsUtil.VAMPIRE_CLOAKS).forEach(this::add);

            add(VAMPIRE_CLOTHING_LEGS);
            add(VAMPIRE_CLOTHING_BOOTS);

            addItemGen(AMULET);
            addItemGen(RING);
            addItemGen(OBI_BELT);
        }

        private void addBlocks() {
            addFunctionalBlocks();
        }

        private void addFunctionalBlocks() {
            add(FOG_DIFFUSER);
            add(SUNSCREEN_BEACON);

            add(ALTAR_INSPIRATION);
            add(ALTAR_INFUSION);
            add(ALTAR_PILLAR);
            add(ALTAR_TIP);

            add(BLOOD_PEDESTAL);
            addBlockGen(BLOOD_CONTAINER);
            add(BLOOD_GRINDER);
            add(BLOOD_SIEVE);
            add(INFUSER);
        }
    }

    public static class HunterDisplayItemGenerator extends BaseDisplayItemGenerator {

        @Override
        protected void addAll() {
            addItems();
            addBlocks();
        }

        private void addWeapons() {
            addItemGen(HUNTER_AXE_NORMAL);
            addItemGen(HUNTER_AXE_ENHANCED);
            addItemGen(HUNTER_AXE_ULTIMATE);

            add(BASIC_CROSSBOW);
            add(BASIC_DOUBLE_CROSSBOW);
            add(ENHANCED_CROSSBOW);
            add(ENHANCED_DOUBLE_CROSSBOW);
            add(BASIC_TECH_CROSSBOW);
            add(ENHANCED_TECH_CROSSBOW);

            add(CROSSBOW_ARROW_NORMAL);
            add(CROSSBOW_ARROW_SPITFIRE);
            add(CROSSBOW_ARROW_GARLIC);
            add(CROSSBOW_ARROW_VAMPIRE_KILLER);
            add(CROSSBOW_ARROW_TELEPORT);
            add(CROSSBOW_ARROW_BLEEDING);

            add(ARROW_CLIP);
            add(QUARREL_POUCH);

            add(PITCHFORK);
            add(STAKE);

            add(CRUCIFIX_NORMAL);
            add(CRUCIFIX_ENHANCED);
            add(CRUCIFIX_ULTIMATE);
        }

        private void addArmor() {
            add(ARMOR_OF_SWIFTNESS_HEAD_NORMAL);
            add(ARMOR_OF_SWIFTNESS_CHEST_NORMAL);
            add(ARMOR_OF_SWIFTNESS_LEGS_NORMAL);
            add(ARMOR_OF_SWIFTNESS_FEET_NORMAL);
            add(ARMOR_OF_SWIFTNESS_HEAD_ENHANCED);
            add(ARMOR_OF_SWIFTNESS_CHEST_ENHANCED);
            add(ARMOR_OF_SWIFTNESS_LEGS_ENHANCED);
            add(ARMOR_OF_SWIFTNESS_FEET_ENHANCED);
            add(ARMOR_OF_SWIFTNESS_HEAD_ULTIMATE);
            add(ARMOR_OF_SWIFTNESS_CHEST_ULTIMATE);
            add(ARMOR_OF_SWIFTNESS_LEGS_ULTIMATE);
            add(ARMOR_OF_SWIFTNESS_FEET_ULTIMATE);

            add(HUNTER_COAT_HEAD_NORMAL);
            add(HUNTER_COAT_CHEST_NORMAL);
            add(HUNTER_COAT_LEGS_NORMAL);
            add(HUNTER_COAT_FEET_NORMAL);
            add(HUNTER_COAT_HEAD_ENHANCED);
            add(HUNTER_COAT_CHEST_ENHANCED);
            add(HUNTER_COAT_LEGS_ENHANCED);
            add(HUNTER_COAT_FEET_ENHANCED);
            add(HUNTER_COAT_HEAD_ULTIMATE);
            add(HUNTER_COAT_CHEST_ULTIMATE);
            add(HUNTER_COAT_LEGS_ULTIMATE);
            add(HUNTER_COAT_FEET_ULTIMATE);

            add(HUNTER_HAT_TALL);
            add(HUNTER_HAT_BROAD);
        }

        private void addItems() {
            addWeapons();
            addArmor();

            add(GARLIC_DIFFUSER_CORE);
            add(GARLIC_DIFFUSER_CORE_IMPROVED);

            add(HOLY_WATER_BOTTLE_NORMAL);
            add(HOLY_WATER_BOTTLE_ENHANCED);
            add(HOLY_WATER_BOTTLE_ULTIMATE);
            add(HOLY_WATER_SPLASH_BOTTLE_NORMAL);
            add(HOLY_WATER_SPLASH_BOTTLE_ENHANCED);
            add(HOLY_WATER_SPLASH_BOTTLE_ULTIMATE);

            add(HUNTER_INTEL_0);
            add(HUNTER_INTEL_1);
            add(HUNTER_INTEL_2);
            add(HUNTER_INTEL_3);
            add(HUNTER_INTEL_4);
            add(HUNTER_INTEL_5);
            add(HUNTER_INTEL_6);
            add(HUNTER_INTEL_7);
            add(HUNTER_INTEL_8);
            add(HUNTER_INTEL_9);

            add(PURIFIED_GARLIC);
            add(PURE_SALT);
            add(PURE_SALT_WATER);

            add(HUNTER_MINION_EQUIPMENT);
            add(HUNTER_MINION_UPGRADE_SIMPLE);
            add(HUNTER_MINION_UPGRADE_ENHANCED);
            add(HUNTER_MINION_UPGRADE_SPECIAL);

            add(ITEM_ALCHEMICAL_FIRE);
        }

        private void addBlocks() {
            addFunctionalBlocks();

            ColorListsUtil.COFFINS.forEach(this::add);
        }

        private void addFunctionalBlocks() {
            add(HUNTER_TABLE);
            add(WEAPON_TABLE);
            add(ALCHEMICAL_CAULDRON);
            add(POTION_TABLE);
            add(ALCHEMY_TABLE);
            add(MED_CHAIR);
            add(ALTAR_CLEANSING);

            add(GARLIC_DIFFUSER_NORMAL);
            add(GARLIC_DIFFUSER_WEAK);
            add(GARLIC_DIFFUSER_IMPROVED);

            add(VAMPIRE_BEACON);
        }
    }

    public static void addToExistingCreativeTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().equals(CreativeModeTabs.SPAWN_EGGS)) {
            insert(VAMPIRE_SPAWN_EGG, event);
            insert(ADVANCED_VAMPIRE_SPAWN_EGG, event);
            insert(VAMPIRE_BARON_SPAWN_EGG, event);
            insert(TASK_MASTER_VAMPIRE_SPAWN_EGG, event);

            insert(VAMPIRE_HUNTER_SPAWN_EGG, event);
            insert(ADVANCED_VAMPIRE_HUNTER_SPAWN_EGG, event);
            insert(HUNTER_TRAINER_SPAWN_EGG, event);
            insert(TASK_MASTER_HUNTER_SPAWN_EGG, event);

            insert(GHOST_SPAWN_EGG, event);
        } else if (event.getTabKey().equals(CreativeModeTabs.TOOLS_AND_UTILITIES)) {
            insertAfter(BLOOD_BUCKET, Items.MILK_BUCKET, event);
        }
    }

    private static void insert(ItemLike item, BuildCreativeModeTabContentsEvent event) {
        event.accept(item);
    }

    @SuppressWarnings("SameParameterValue")
    private static void insertAfter(ItemLike item, ItemLike insertAfterItem, BuildCreativeModeTabContentsEvent event) {
        event.insertAfter(new ItemStack(insertAfterItem), new ItemStack(item), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
    }

    public static void register(IEventBus bus) {
        CREATIVE_TABS.register(bus);
    }
}
