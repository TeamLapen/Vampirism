package de.teamlapen.vampirism.data.provider.tags;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.core.tags.ModBlockTags;
import de.teamlapen.vampirism.core.tags.ModItemTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class ModItemTagsProvider extends ItemTagsProvider {
    public ModItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTagsProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTagsProvider, REFERENCE.MODID, existingFileHelper);
    }

    @NotNull
    @Override
    public String getName() {
        return REFERENCE.MODID + " " + super.getName();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void addTags(HolderLookup.@NotNull Provider holderProvider) {
        copy(ModBlockTags.CURSED_EARTH, ModItemTags.CURSEDEARTH);
        copy(ModBlockTags.DARK_SPRUCE_LOG, ModItemTags.DARK_SPRUCE_LOG);
        copy(ModBlockTags.CURSED_SPRUCE_LOG, ModItemTags.CURSED_SPRUCE_LOG);
        copy(ModBlockTags.DARK_STONE, ModItemTags.DARK_STONE);
        copy(ModBlockTags.DARK_STONE_BRICKS, ModItemTags.DARK_STONE_BRICKS);
        copy(ModBlockTags.POLISHED_DARK_STONE, ModItemTags.POLISHED_DARK_STONE);
        copy(ModBlockTags.COBBLED_DARK_STONE, ModItemTags.COBBLED_DARK_STONE);
        copy(ModBlockTags.DARK_STONE_TILES, ModItemTags.DARK_STONE_TILES);
        copy(ModBlockTags.NO_SPAWN, ModItemTags.NO_SPAWN);
        copy(ModBlockTags.VAMPIRE_SPAWN, ModItemTags.VAMPIRE_SPAWN);
        copy(BlockTags.LOGS_THAT_BURN, ItemTags.LOGS_THAT_BURN);
        copy(BlockTags.LOGS, ItemTags.LOGS);
        copy(BlockTags.WOODEN_TRAPDOORS, ItemTags.WOODEN_TRAPDOORS);
        copy(BlockTags.WOODEN_STAIRS, ItemTags.WOODEN_STAIRS);
        copy(BlockTags.WOODEN_SLABS, ItemTags.WOODEN_SLABS);
        copy(BlockTags.WOODEN_PRESSURE_PLATES, ItemTags.WOODEN_PRESSURE_PLATES);
        copy(BlockTags.WOODEN_FENCES, ItemTags.WOODEN_FENCES);
        copy(BlockTags.WOODEN_DOORS, ItemTags.WOODEN_DOORS);
        copy(BlockTags.WOODEN_BUTTONS, ItemTags.WOODEN_BUTTONS);
        copy(BlockTags.PLANKS, ItemTags.PLANKS);
        copy(BlockTags.SAPLINGS, ItemTags.SAPLINGS);
        copy(BlockTags.LEAVES, ItemTags.LEAVES);
        copy(BlockTags.STAIRS, ItemTags.STAIRS);
        copy(BlockTags.SLABS, ItemTags.SLABS);
        copy(BlockTags.WALLS, ItemTags.WALLS);

        tag(ModItemTags.CROSSBOW_ARROW).add(ModItems.CROSSBOW_ARROW_NORMAL.get(), ModItems.CROSSBOW_ARROW_SPITFIRE.get(), ModItems.CROSSBOW_ARROW_VAMPIRE_KILLER.get(), ModItems.CROSSBOW_ARROW_TELEPORT.get(), ModItems.CROSSBOW_ARROW_BLEEDING.get(), ModItems.CROSSBOW_ARROW_GARLIC.get());
        tag(ModItemTags.HUNTER_INTEL).add(ModItems.HUNTER_INTEL_0.get(), ModItems.HUNTER_INTEL_1.get(), ModItems.HUNTER_INTEL_2.get(), ModItems.HUNTER_INTEL_3.get(), ModItems.HUNTER_INTEL_4.get(), ModItems.HUNTER_INTEL_5.get(), ModItems.HUNTER_INTEL_6.get(), ModItems.HUNTER_INTEL_7.get(), ModItems.HUNTER_INTEL_8.get(), ModItems.HUNTER_INTEL_9.get());
        tag(ModItemTags.PURE_BLOOD).add(ModItems.PURE_BLOOD_0.get(), ModItems.PURE_BLOOD_1.get(), ModItems.PURE_BLOOD_2.get(), ModItems.PURE_BLOOD_3.get(), ModItems.PURE_BLOOD_4.get());
        tag(ModItemTags.VAMPIRE_CLOAK).add(ModItems.VAMPIRE_CLOAK_BLACK_BLUE.get(), ModItems.VAMPIRE_CLOAK_BLACK_RED.get(), ModItems.VAMPIRE_CLOAK_BLACK_WHITE.get(), ModItems.VAMPIRE_CLOAK_RED_BLACK.get(), ModItems.VAMPIRE_CLOAK_WHITE_BLACK.get());
        tag(ItemTags.SMALL_FLOWERS).add(ModBlocks.VAMPIRE_ORCHID.get().asItem());
        tag(ModItemTags.GARLIC).add(ModItems.ITEM_GARLIC.get());
        tag(ModItemTags.HOLY_WATER).add(ModItems.HOLY_WATER_BOTTLE_NORMAL.get(), ModItems.HOLY_WATER_BOTTLE_ENHANCED.get(), ModItems.HOLY_WATER_BOTTLE_ULTIMATE.get());
        tag(ModItemTags.HOLY_WATER_SPLASH).add(ModItems.HOLY_WATER_SPLASH_BOTTLE_NORMAL.get(), ModItems.HOLY_WATER_SPLASH_BOTTLE_ENHANCED.get(), ModItems.HOLY_WATER_SPLASH_BOTTLE_ULTIMATE.get());
        tag(ItemTags.PIGLIN_LOVED).add(ModItems.VAMPIRE_CLOTHING_CROWN.get());
        tag(ModItemTags.HEART).add(ModItems.HUMAN_HEART.get(), ModItems.WEAK_HUMAN_HEART.get());
        tag(ItemTags.BOATS).add(ModItems.DARK_SPRUCE_BOAT.get(), ModItems.CURSED_SPRUCE_BOAT.get());
        tag(ModItemTags.APPLICABLE_OIL_ARMOR).add(Items.LEATHER_BOOTS, Items.LEATHER_LEGGINGS, Items.LEATHER_HELMET, Items.LEATHER_CHESTPLATE, ModItems.VAMPIRE_CLOTHING_LEGS.get(), ModItems.VAMPIRE_CLOTHING_BOOTS.get(), ModItems.VAMPIRE_CLOTHING_CROWN.get(), ModItems.VAMPIRE_CLOTHING_HAT.get(), ModItems.VAMPIRE_CLOAK_RED_BLACK.get(), ModItems.VAMPIRE_CLOAK_BLACK_RED.get(), ModItems.VAMPIRE_CLOAK_BLACK_WHITE.get(), ModItems.VAMPIRE_CLOAK_WHITE_BLACK.get(), ModItems.VAMPIRE_CLOAK_BLACK_BLUE.get());
        tag(ModItemTags.APPLICABLE_OIL_PICKAXE);
        tag(ModItemTags.APPLICABLE_OIL_SWORD);
        tag(ItemTags.SIGNS).add(ModItems.DARK_SPRUCE_SIGN.get(), ModItems.CURSED_SPRUCE_SIGN.get());
        tag(ItemTags.HANGING_SIGNS).add(ModItems.DARK_SPRUCE_HANGING_SIGN.get(), ModItems.CURSED_SPRUCE_HANGING_SIGN.get());
        tag(ItemTags.FREEZE_IMMUNE_WEARABLES).addTag(ModItemTags.HUNTER_COAT);
        tag(ModItemTags.VAMPIRE_BEACON_PAYMENT_ITEM).addTags(ModItemTags.PURE_BLOOD, ModItemTags.HEART).add(ModItems.SOUL_ORB_VAMPIRE.get());
        tag(ModItemTags.HEART_SEEKER).add(ModItems.HEART_SEEKER_NORMAL.get(), ModItems.HEART_SEEKER_ENHANCED.get(), ModItems.HEART_SEEKER_ULTIMATE.get());
        tag(ModItemTags.HEART_STRIKER).add(ModItems.HEART_STRIKER_NORMAL.get(), ModItems.HEART_STRIKER_ENHANCED.get(), ModItems.HEART_STRIKER_ULTIMATE.get());
        tag(ItemTags.SWORDS).addTags(ModItemTags.HEART_STRIKER, ModItemTags.HEART_SEEKER);
        tag(ModItemTags.VAMPIRE_SLAYER_ITEMS).addTag(ItemTags.SWORDS).add(ModItems.PITCHFORK.get());
        tag(ItemTags.SHARP_WEAPON_ENCHANTABLE).add(ModItems.PITCHFORK.get());
        tag(ModItemTags.CROSSBOW_ENCHANTABLE).addTag(ModItemTags.CROSSBOWS);
        tag(ModItemTags.CROSSBOWS).addTags(ModItemTags.BASIC_CROSSBOWS, ModItemTags.ENHANCED_CROSSBOWS, ModItemTags.SINGLE_CROSSBOWS, ModItemTags.DOUBLE_CROSSBOWS, ModItemTags.TECH_CROSSBOWS);
        tag(ModItemTags.BASIC_CROSSBOWS).add(ModItems.BASIC_CROSSBOW.get(), ModItems.BASIC_DOUBLE_CROSSBOW.get(), ModItems.BASIC_TECH_CROSSBOW.get());
        tag(ModItemTags.ENHANCED_CROSSBOWS).add(ModItems.ENHANCED_CROSSBOW.get(), ModItems.ENHANCED_DOUBLE_CROSSBOW.get(), ModItems.ENHANCED_TECH_CROSSBOW.get());
        tag(ModItemTags.SINGLE_CROSSBOWS).add(ModItems.BASIC_CROSSBOW.get(), ModItems.ENHANCED_CROSSBOW.get());
        tag(ModItemTags.DOUBLE_CROSSBOWS).add(ModItems.BASIC_DOUBLE_CROSSBOW.get(), ModItems.ENHANCED_DOUBLE_CROSSBOW.get());
        tag(ModItemTags.TECH_CROSSBOWS).add(ModItems.BASIC_TECH_CROSSBOW.get(), ModItems.ENHANCED_TECH_CROSSBOW.get());
        tag(ModItemTags.ARMOR_OF_SWIFTNESS).addTags(ModItemTags.ARMOR_OF_SWIFTNESS_NORMAL, ModItemTags.ARMOR_OF_SWIFTNESS_ENHANCED, ModItemTags.ARMOR_OF_SWIFTNESS_ULTIMATE);
        tag(ModItemTags.ARMOR_OF_SWIFTNESS_NORMAL).add(ModItems.ARMOR_OF_SWIFTNESS_HEAD_NORMAL.get(), ModItems.ARMOR_OF_SWIFTNESS_CHEST_NORMAL.get(), ModItems.ARMOR_OF_SWIFTNESS_LEGS_NORMAL.get(), ModItems.ARMOR_OF_SWIFTNESS_FEET_NORMAL.get());
        tag(ModItemTags.ARMOR_OF_SWIFTNESS_ENHANCED).add(ModItems.ARMOR_OF_SWIFTNESS_HEAD_ENHANCED.get(), ModItems.ARMOR_OF_SWIFTNESS_CHEST_ENHANCED.get(), ModItems.ARMOR_OF_SWIFTNESS_LEGS_ENHANCED.get(), ModItems.ARMOR_OF_SWIFTNESS_FEET_ENHANCED.get());
        tag(ModItemTags.ARMOR_OF_SWIFTNESS_ULTIMATE).add(ModItems.ARMOR_OF_SWIFTNESS_HEAD_ULTIMATE.get(), ModItems.ARMOR_OF_SWIFTNESS_CHEST_ULTIMATE.get(), ModItems.ARMOR_OF_SWIFTNESS_LEGS_ULTIMATE.get(), ModItems.ARMOR_OF_SWIFTNESS_FEET_ULTIMATE.get());
        tag(ModItemTags.HUNTER_COAT).addTags(ModItemTags.HUNTER_COAT_NORMAL, ModItemTags.HUNTER_COAT_ENHANCED, ModItemTags.HUNTER_COAT_ULTIMATE);
        tag(ModItemTags.HUNTER_COAT_NORMAL).add(ModItems.HUNTER_COAT_HEAD_NORMAL.get(), ModItems.HUNTER_COAT_CHEST_NORMAL.get(), ModItems.HUNTER_COAT_LEGS_NORMAL.get(), ModItems.HUNTER_COAT_FEET_NORMAL.get());
        tag(ModItemTags.HUNTER_COAT_ENHANCED).add(ModItems.HUNTER_COAT_HEAD_ENHANCED.get(), ModItems.HUNTER_COAT_CHEST_ENHANCED.get(), ModItems.HUNTER_COAT_LEGS_ENHANCED.get(), ModItems.HUNTER_COAT_FEET_ENHANCED.get());
        tag(ModItemTags.HUNTER_COAT_ULTIMATE).add(ModItems.HUNTER_COAT_HEAD_ULTIMATE.get(), ModItems.HUNTER_COAT_CHEST_ULTIMATE.get(), ModItems.HUNTER_COAT_LEGS_ULTIMATE.get(), ModItems.HUNTER_COAT_FEET_ULTIMATE.get());
        tag(ItemTags.HEAD_ARMOR).add(ModItems.HUNTER_COAT_HEAD_NORMAL.get(), ModItems.HUNTER_COAT_HEAD_ENHANCED.get(), ModItems.HUNTER_COAT_HEAD_ULTIMATE.get(), ModItems.ARMOR_OF_SWIFTNESS_HEAD_NORMAL.get(), ModItems.ARMOR_OF_SWIFTNESS_HEAD_ENHANCED.get(), ModItems.ARMOR_OF_SWIFTNESS_HEAD_ULTIMATE.get(), ModItems.VAMPIRE_CLOTHING_CROWN.get(), ModItems.VAMPIRE_CLOTHING_HAT.get());
        tag(ItemTags.CHEST_ARMOR).add(ModItems.HUNTER_COAT_CHEST_NORMAL.get(), ModItems.HUNTER_COAT_CHEST_ENHANCED.get(), ModItems.HUNTER_COAT_CHEST_ULTIMATE.get(), ModItems.ARMOR_OF_SWIFTNESS_CHEST_NORMAL.get(), ModItems.ARMOR_OF_SWIFTNESS_CHEST_ENHANCED.get(), ModItems.ARMOR_OF_SWIFTNESS_CHEST_ULTIMATE.get()).addTag(ModItemTags.VAMPIRE_CLOAK);
        tag(ItemTags.LEG_ARMOR).add(ModItems.HUNTER_COAT_LEGS_NORMAL.get(), ModItems.HUNTER_COAT_LEGS_ENHANCED.get(), ModItems.HUNTER_COAT_LEGS_ULTIMATE.get(), ModItems.ARMOR_OF_SWIFTNESS_LEGS_NORMAL.get(), ModItems.ARMOR_OF_SWIFTNESS_LEGS_ENHANCED.get(), ModItems.ARMOR_OF_SWIFTNESS_LEGS_ULTIMATE.get(), ModItems.VAMPIRE_CLOTHING_LEGS.get());
        tag(ItemTags.FOOT_ARMOR).add(ModItems.HUNTER_COAT_FEET_NORMAL.get(), ModItems.HUNTER_COAT_FEET_ENHANCED.get(), ModItems.HUNTER_COAT_FEET_ULTIMATE.get(), ModItems.ARMOR_OF_SWIFTNESS_FEET_NORMAL.get(), ModItems.ARMOR_OF_SWIFTNESS_FEET_ENHANCED.get(), ModItems.ARMOR_OF_SWIFTNESS_FEET_ULTIMATE.get(), ModItems.VAMPIRE_CLOTHING_BOOTS.get());
        tag(ModItemTags.VAMPIRE_CLOTHING).addTag(ModItemTags.VAMPIRE_CLOAK);
        tag(ModItemTags.HUNTER_ARMOR).addTags(ModItemTags.ARMOR_OF_SWIFTNESS, ModItemTags.HUNTER_COAT);
        tag(ModItemTags.ADVANCED_HUNTER_CROSSBOW_ARROWS).add(ModItems.CROSSBOW_ARROW_NORMAL.get(), ModItems.CROSSBOW_ARROW_GARLIC.get(), ModItems.CROSSBOW_ARROW_BLEEDING.get(), ModItems.CROSSBOW_ARROW_VAMPIRE_KILLER.get(), ModItems.CROSSBOW_ARROW_SPITFIRE.get());
        tag(ItemTags.MEAT).add(ModItems.HUMAN_HEART.get(), ModItems.WEAK_HUMAN_HEART.get());
    }
}
