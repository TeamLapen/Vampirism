package de.teamlapen.vampirism.data.provider.tags;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.common.core.ModBlocks;
import de.teamlapen.vampirism.common.core.ModItems;
import de.teamlapen.vampirism.common.tags.ModBlockTags;
import de.teamlapen.vampirism.common.tags.ModItemTags;
import de.teamlapen.vampirism.common.util.ColorListsUtil;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagCopyingItemTagProvider;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class ModItemTagsProvider extends BlockTagCopyingItemTagProvider {

    public ModItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTagsProvider) {
        super(output, lookupProvider, blockTagsProvider, REFERENCE.MODID);
    }

    @NotNull
    @Override
    public String getName() {
        return REFERENCE.MODID + " " + super.getName();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void addTags(HolderLookup.@NotNull Provider holderProvider) {
        copy(ModBlockTags.CURSED_EARTH, ModItemTags.CURSED_EARTH);
        copy(ModBlockTags.DARK_SPRUCE_LOG, ModItemTags.DARK_SPRUCE_LOG);
        copy(ModBlockTags.CURSED_SPRUCE_LOG, ModItemTags.CURSED_SPRUCE_LOG);
        copy(ModBlockTags.DARK_STONE, ModItemTags.DARK_STONE);
        copy(ModBlockTags.DARK_STONE_BRICKS, ModItemTags.DARK_STONE_BRICKS);
        copy(ModBlockTags.POLISHED_DARK_STONE, ModItemTags.POLISHED_DARK_STONE);
        copy(ModBlockTags.COBBLED_DARK_STONE, ModItemTags.COBBLED_DARK_STONE);
        copy(ModBlockTags.DARK_STONE_TILES, ModItemTags.DARK_STONE_TILES);
        copy(ModBlockTags.NO_SPAWN, ModItemTags.NO_SPAWN);
        copy(ModBlockTags.VAMPIRE_SPAWN, ModItemTags.VAMPIRE_SPAWN);
        copy(ModBlockTags.CREEPER_REPELLENT, ModItemTags.CREEPER_REPELLENT);
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
        copy(ModBlockTags.GARLIC, ModItemTags.GARLIC);

        tag(ModItemTags.QUARRELS).add(ModItems.QUARREL_NORMAL.get(), ModItems.QUARREL_HEAVY.get(), ModItems.QUARREL_SPITFIRE.get(), ModItems.QUARREL_VAMPIRE_KILLER.get(), ModItems.QUARREL_TELEPORT.get(), ModItems.QUARREL_BLEEDING.get(), ModItems.QUARREL_GARLIC.get());
        tag(ModItemTags.HUNTER_INTEL).add(ModItems.HUNTER_INTEL_0.get(), ModItems.HUNTER_INTEL_1.get(), ModItems.HUNTER_INTEL_2.get(), ModItems.HUNTER_INTEL_3.get(), ModItems.HUNTER_INTEL_4.get(), ModItems.HUNTER_INTEL_5.get(), ModItems.HUNTER_INTEL_6.get(), ModItems.HUNTER_INTEL_7.get(), ModItems.HUNTER_INTEL_8.get(), ModItems.HUNTER_INTEL_9.get());
        tag(ModItemTags.PURE_BLOOD).add(ModItems.PURE_BLOOD_0.get(), ModItems.PURE_BLOOD_1.get(), ModItems.PURE_BLOOD_2.get(), ModItems.PURE_BLOOD_3.get(), ModItems.PURE_BLOOD_4.get());
        ColorListsUtil.VAMPIRE_CLOAKS.values().forEach(item -> tag(ModItemTags.VAMPIRE_CLOAK).add(item));
        tag(ModItemTags.DISABLES_CAPE).addTag(ModItemTags.VAMPIRE_CLOAK);
        tag(ItemTags.SMALL_FLOWERS).add(ModBlocks.VAMPIRE_ORCHID.get().asItem());
        tag(ModItemTags.HOLY_WATER).add(ModItems.HOLY_WATER_BOTTLE_NORMAL.get(), ModItems.HOLY_WATER_BOTTLE_ENHANCED.get(), ModItems.HOLY_WATER_BOTTLE_ULTIMATE.get());
        tag(ModItemTags.HOLY_WATER_SPLASH).add(ModItems.HOLY_WATER_SPLASH_BOTTLE_NORMAL.get(), ModItems.HOLY_WATER_SPLASH_BOTTLE_ENHANCED.get(), ModItems.HOLY_WATER_SPLASH_BOTTLE_ULTIMATE.get());
        tag(ItemTags.PIGLIN_LOVED).add(ModItems.VAMPIRE_CLOTHING_CROWN.get());
        tag(ModItemTags.HEART).add(ModItems.HUMAN_HEART.get(), ModItems.WEAK_HUMAN_HEART.get());
        tag(ItemTags.BOATS).add(ModItems.DARK_SPRUCE_BOAT.get(), ModItems.CURSED_SPRUCE_BOAT.get());
        tag(ModItemTags.APPLICABLE_OIL_ARMOR).addTag(ModItemTags.VAMPIRE_CLOAK).add(Items.LEATHER_BOOTS, Items.LEATHER_LEGGINGS, Items.LEATHER_HELMET, Items.LEATHER_CHESTPLATE, ModItems.VAMPIRE_CLOTHING_LEGS.get(), ModItems.VAMPIRE_CLOTHING_BOOTS.get(), ModItems.VAMPIRE_CLOTHING_CROWN.get(), ModItems.VAMPIRE_CLOTHING_HAT.get());
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
        tag(ModItemTags.CROSSBOW_ENCHANTABLE).addTags(ModItemTags.SINGLE_HUNTER_CROSSBOWS, ModItemTags.DOUBLE_HUNTER_CROSSBOWS);
        tag(ModItemTags.TECH_CROSSBOW_ENCHANTABLE).add(ModItems.BASIC_TECH_CROSSBOW.get(), ModItems.ENHANCED_TECH_CROSSBOW.get());
        tag(ModItemTags.HUNTER_CROSSBOWS).addTags(ModItemTags.BASIC_HUNTER_CROSSBOWS, ModItemTags.ENHANCED_HUNTER_CROSSBOWS, ModItemTags.SINGLE_HUNTER_CROSSBOWS, ModItemTags.DOUBLE_HUNTER_CROSSBOWS, ModItemTags.TECH_HUNTER_CROSSBOWS);
        tag(ModItemTags.BASIC_HUNTER_CROSSBOWS).add(ModItems.BASIC_CROSSBOW.get(), ModItems.BASIC_DOUBLE_CROSSBOW.get(), ModItems.BASIC_TECH_CROSSBOW.get());
        tag(ModItemTags.ENHANCED_HUNTER_CROSSBOWS).add(ModItems.ENHANCED_CROSSBOW.get(), ModItems.ENHANCED_DOUBLE_CROSSBOW.get(), ModItems.ENHANCED_TECH_CROSSBOW.get());
        tag(ModItemTags.SINGLE_HUNTER_CROSSBOWS).add(ModItems.BASIC_CROSSBOW.get(), ModItems.ENHANCED_CROSSBOW.get());
        tag(ModItemTags.DOUBLE_HUNTER_CROSSBOWS).add(ModItems.BASIC_DOUBLE_CROSSBOW.get(), ModItems.ENHANCED_DOUBLE_CROSSBOW.get());
        tag(ModItemTags.TECH_HUNTER_CROSSBOWS).add(ModItems.BASIC_TECH_CROSSBOW.get(), ModItems.ENHANCED_TECH_CROSSBOW.get());
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
        tag(ModItemTags.POWDER_SNOW_WALKABLE_BOOTS).addTags(ModItemTags.HUNTER_COAT, ModItemTags.ARMOR_OF_SWIFTNESS);
        tag(ModItemTags.VAMPIRE_CLOTHING).addTag(ModItemTags.VAMPIRE_CLOAK);
        tag(ModItemTags.HUNTER_ARMOR).addTags(ModItemTags.ARMOR_OF_SWIFTNESS, ModItemTags.HUNTER_COAT);
        tag(ModItemTags.ADVANCED_HUNTER_USABLE_QUARRELS).add(ModItems.QUARREL_NORMAL.get(), ModItems.QUARREL_GARLIC.get(), ModItems.QUARREL_BLEEDING.get(), ModItems.QUARREL_VAMPIRE_KILLER.get(), ModItems.QUARREL_SPITFIRE.get());
        tag(ItemTags.MEAT).add(ModItems.HUMAN_HEART.get(), ModItems.WEAK_HUMAN_HEART.get());
        tag(ModItemTags.CROSSBOW_REPAIRABLE).addTag(Tags.Items.STRINGS);
        tag(ItemTags.VILLAGER_PLANTABLE_SEEDS).add(ModBlocks.GARLIC.asItem());
        tag(ItemTags.BOOKSHELF_BOOKS).addTag(ModItemTags.HUNTER_INTEL).add(ModItems.VAMPIRE_BOOK.get());
        Optional.of(BuiltInRegistries.ITEM.getValue(REFERENCE.GUIDEBOOK_LOCATION)).ifPresent(book -> tag(ItemTags.BOOKSHELF_BOOKS).addOptional(book));
        tag(ItemTags.STONE_TOOL_MATERIALS).add(ModItems.COBBLED_DARK_STONE.get());
        tag(ItemTags.STONE_CRAFTING_MATERIALS).add(ModItems.COBBLED_DARK_STONE.get());
        tag(ModItemTags.HUNTER_AXE).add(ModItems.HUNTER_AXE_NORMAL.get(), ModItems.HUNTER_AXE_ENHANCED.get(), ModItems.HUNTER_AXE_ULTIMATE.get());
        tag(ItemTags.DURABILITY_ENCHANTABLE).addTags(ModItemTags.HUNTER_AXE, ModItemTags.HUNTER_CROSSBOWS).add(ModItems.STAKE.get());
        tag(ItemTags.SHARP_WEAPON_ENCHANTABLE).addTag(ModItemTags.HUNTER_AXE);
        tag(ItemTags.MELEE_WEAPON_ENCHANTABLE).addTag(ModItemTags.HUNTER_AXE);
        tag(ItemTags.SWEEPING_ENCHANTABLE).addTag(ModItemTags.HUNTER_AXE);
        tag(ItemTags.FIRE_ASPECT_ENCHANTABLE).addTag(ModItemTags.HUNTER_AXE);
        tag(ItemTags.MINING_ENCHANTABLE).addTag(ModItemTags.HUNTER_AXE);

        addCompatibilityTags();
    }

    private void addCompatibilityTags() {
        copy(ModBlockTags.Compatibility.SERENE_SEASONS_AUTUMN_CROPS, ModItemTags.Compatibility.SERENE_SEASONS_AUTUMN_CROPS);
        copy(ModBlockTags.Compatibility.SERENE_SEASONS_SUMMER_CROPS, ModItemTags.Compatibility.SERENE_SEASONS_SUMMER_CROPS);
    }
}
