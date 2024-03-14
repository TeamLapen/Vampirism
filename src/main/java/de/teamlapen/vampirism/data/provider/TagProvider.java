package de.teamlapen.vampirism.data.provider;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.factions.ISkillTree;
import de.teamlapen.vampirism.api.entity.player.task.Task;
import de.teamlapen.vampirism.core.*;
import de.teamlapen.vampirism.entity.player.hunter.skills.HunterSkills;
import de.teamlapen.vampirism.entity.player.vampire.skills.VampireSkills;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.*;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class TagProvider {

    public static void register(DataGenerator gen, @NotNull GatherDataEvent event, PackOutput output, CompletableFuture<HolderLookup.Provider> future, ExistingFileHelper existingFileHelper) {
        BlockTagsProvider blockTagsProvider = new ModBlockTagsProvider(output, future, existingFileHelper);
        gen.addProvider(event.includeServer(), blockTagsProvider);
        gen.addProvider(event.includeServer(), new ModItemTagsProvider(output, future, blockTagsProvider.contentsGetter(), existingFileHelper));
        gen.addProvider(event.includeServer(), new ModEntityTypeTagsProvider(output, future, existingFileHelper));
        gen.addProvider(event.includeServer(), new ModFluidTagsProvider(output, future, existingFileHelper));
        gen.addProvider(event.includeServer(), new ModBiomeTagsProvider(output, future, existingFileHelper));
        gen.addProvider(event.includeServer(), new ModPoiTypeProvider(output, future, existingFileHelper));
        gen.addProvider(event.includeServer(), new ModVillageProfessionProvider(output, future, existingFileHelper));
        gen.addProvider(event.includeServer(), new ModDamageTypeProvider(output, future, existingFileHelper));
        gen.addProvider(event.includeServer(), new ModTasksProvider(output, future, existingFileHelper));
        gen.addProvider(event.includeServer(), new ModStructuresProvider(output, future, existingFileHelper));
        gen.addProvider(event.includeServer(), new ModSkillTreeProvider(output, future, existingFileHelper));
    }

    public static class ModBlockTagsProvider extends BlockTagsProvider {
        public ModBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
            super(output, lookupProvider, REFERENCE.MODID, existingFileHelper);
        }

        @NotNull
        @Override
        public String getName() {
            return REFERENCE.MODID + " " + super.getName();
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void addTags(HolderLookup.Provider holderLookup) {
            tag(BlockTags.DIRT).add(ModBlocks.CURSED_EARTH.get(), ModBlocks.CURSED_GRASS.get());
            tag(ModTags.Blocks.CURSED_EARTH).add(ModBlocks.CURSED_EARTH.get(), ModBlocks.CURSED_GRASS.get());
            tag(BlockTags.FLOWER_POTS).add(ModBlocks.POTTED_VAMPIRE_ORCHID.get());
            tag(ModTags.Blocks.REMAINS).add(ModBlocks.ACTIVE_VULNERABLE_REMAINS.get(), ModBlocks.VULNERABLE_REMAINS.get(), ModBlocks.REMAINS.get(), ModBlocks.INCAPACITATED_VULNERABLE_REMAINS.get());
            tag(ModTags.Blocks.ACTIVE_REMAINS).add(ModBlocks.ACTIVE_VULNERABLE_REMAINS.get(), ModBlocks.VULNERABLE_REMAINS.get());
            tag(ModTags.Blocks.VULNERABLE_REMAINS).addTag(ModTags.Blocks.ACTIVE_REMAINS).add(ModBlocks.INCAPACITATED_VULNERABLE_REMAINS.get());
            // Tool Types
            tag(BlockTags.MINEABLE_WITH_SHOVEL)
                    .add(ModBlocks.CURSED_EARTH.get())
                    .add(ModBlocks.CURSED_GRASS.get())
                    .add(ModBlocks.CURSED_EARTH_PATH.get())
            ;
            tag(BlockTags.MINEABLE_WITH_PICKAXE)
                    .add(ModBlocks.ALTAR_PILLAR.get())
                    .add(ModBlocks.ALTAR_TIP.get())
                    .add(ModBlocks.TOTEM_BASE.get())
                    .add(ModBlocks.BLOOD_PEDESTAL.get())
                    .add(ModBlocks.ALTAR_INFUSION.get())
                    .add(ModBlocks.WEAPON_TABLE.get())
                    .add(ModBlocks.GRAVE_CAGE.get())
                    .add(ModBlocks.TOMBSTONE1.get())
                    .add(ModBlocks.TOMBSTONE2.get())
                    .add(ModBlocks.TOMBSTONE3.get())
                    .add(ModBlocks.BLOOD_GRINDER.get())
                    .add(ModBlocks.FIRE_PLACE.get())
                    .add(ModBlocks.GRAVE_CAGE.get())
                    .add(ModBlocks.ALCHEMICAL_CAULDRON.get())
                    .add(ModBlocks.MED_CHAIR.get())
                    .add(ModBlocks.GARLIC_DIFFUSER_WEAK.get())
                    .add(ModBlocks.GARLIC_DIFFUSER_NORMAL.get())
                    .add(ModBlocks.GARLIC_DIFFUSER_IMPROVED.get())
                    .add(ModBlocks.FOG_DIFFUSER.get())
                    .add(ModBlocks.CHANDELIER.get())
                    .add(ModBlocks.CANDELABRA.get())
                    .add(ModBlocks.CANDELABRA_WALL.get())
                    .add(ModBlocks.ALCHEMY_TABLE.get())
                    .add(ModBlocks.BLOOD_INFUSED_IRON_BLOCK.get())
                    .add(ModBlocks.BLOOD_INFUSED_ENHANCED_IRON_BLOCK.get())
                    .addTag(ModTags.Blocks.TOTEM_TOP)
                    .addTag(ModTags.Blocks.DARK_STONE)
                    .addTag(ModTags.Blocks.DARK_STONE_BRICKS)
                    .addTag(ModTags.Blocks.DARK_STONE_TILES)
                    .addTag(ModTags.Blocks.POLISHED_DARK_STONE)
                    .addTag(ModTags.Blocks.COBBLED_DARK_STONE)
            ;
            tag(BlockTags.MINEABLE_WITH_AXE)
                    .add(ModBlocks.ALTAR_INSPIRATION.get())
                    .add(ModBlocks.HUNTER_TABLE.get())
                    .add(ModBlocks.BLOOD_SIEVE.get())
                    .add(ModBlocks.ALTAR_CLEANSING.get())
                    .add(ModBlocks.DARK_SPRUCE_SIGN.get())
                    .add(ModBlocks.DARK_SPRUCE_WALL_SIGN.get())
                    .add(ModBlocks.CURSED_SPRUCE_SIGN.get())
                    .add(ModBlocks.CURSED_SPRUCE_WALL_SIGN.get())
                    .add(ModBlocks.BLOOD_CONTAINER.get())
                    .add(ModBlocks.POTION_TABLE.get())
                    .add(ModBlocks.CROSS.get())
                    .add(ModBlocks.DARK_SPRUCE_DOOR.get())
                    .add(ModBlocks.CURSED_SPRUCE_DOOR.get())
                    .add(ModBlocks.DARK_SPRUCE_TRAPDOOR.get())
                    .add(ModBlocks.CURSED_SPRUCE_TRAPDOOR.get())
                    .add(ModBlocks.DARK_SPRUCE_FENCE_GATE.get())
                    .add(ModBlocks.CURSED_SPRUCE_FENCE_GATE.get())
                    .add(ModBlocks.VAMPIRE_RACK.get())
                    .add(ModBlocks.THRONE.get())
                    .addTag(ModTags.Blocks.COFFIN)
            ;

            // Tool Tiers
            tag(BlockTags.NEEDS_STONE_TOOL)
                    .add(ModBlocks.ALTAR_TIP.get())
                    .add(ModBlocks.GRAVE_CAGE.get())
                    .add(ModBlocks.MED_CHAIR.get())
                    .add(ModBlocks.MED_CHAIR.get())
                    .add(ModBlocks.CHANDELIER.get())
            ;
            tag(BlockTags.NEEDS_IRON_TOOL)
                    .add(ModBlocks.BLOOD_PEDESTAL.get())
                    .add(ModBlocks.BLOOD_GRINDER.get())
                    .add(ModBlocks.WEAPON_TABLE.get())
                    .add(ModBlocks.ALTAR_INFUSION.get())
                    .add(ModBlocks.ALCHEMICAL_CAULDRON.get())
                    .add(ModBlocks.CANDELABRA.get())
                    .add(ModBlocks.CANDELABRA_WALL.get())
                    .add(ModBlocks.ALCHEMY_TABLE.get())
                    .add(ModBlocks.BLOOD_INFUSED_IRON_BLOCK.get())
                    .add(ModBlocks.BLOOD_INFUSED_ENHANCED_IRON_BLOCK.get())
            ;
            tag(BlockTags.NEEDS_DIAMOND_TOOL)
                    .add(ModBlocks.TOTEM_BASE.get())
                    .add(ModBlocks.ALTAR_INFUSION.get())
                    .add(ModBlocks.GARLIC_DIFFUSER_WEAK.get())
                    .add(ModBlocks.GARLIC_DIFFUSER_NORMAL.get())
                    .add(ModBlocks.GARLIC_DIFFUSER_IMPROVED.get())
                    .add(ModBlocks.FOG_DIFFUSER.get())
                    .addTag(ModTags.Blocks.TOTEM_TOP_CRAFTED)
            ;

            tag(BlockTags.REPLACEABLE_BY_TREES).add(ModBlocks.CURSED_HANGING_ROOTS.get());
            tag(ModTags.Blocks.DARK_SPRUCE_LOG).add(ModBlocks.DARK_SPRUCE_LOG.get(), ModBlocks.STRIPPED_DARK_SPRUCE_LOG.get(), ModBlocks.DARK_SPRUCE_WOOD.get(), ModBlocks.STRIPPED_DARK_SPRUCE_WOOD.get());
            tag(ModTags.Blocks.CURSED_SPRUCE_LOG).add(ModBlocks.CURSED_SPRUCE_LOG.get(), ModBlocks.CURSED_SPRUCE_LOG_CURED.get(), ModBlocks.STRIPPED_CURSED_SPRUCE_LOG.get(), ModBlocks.CURSED_SPRUCE_WOOD.get(), ModBlocks.CURSED_SPRUCE_WOOD_CURED.get(), ModBlocks.STRIPPED_CURSED_SPRUCE_WOOD.get());
            tag(BlockTags.LEAVES).add(ModBlocks.DARK_SPRUCE_LEAVES.get());
            tag(BlockTags.SAPLINGS).add(ModBlocks.DARK_SPRUCE_SAPLING.get());
            tag(BlockTags.WOODEN_TRAPDOORS).add(ModBlocks.DARK_SPRUCE_TRAPDOOR.get(), ModBlocks.CURSED_SPRUCE_TRAPDOOR.get());
            tag(BlockTags.WOODEN_DOORS).add(ModBlocks.DARK_SPRUCE_DOOR.get(), ModBlocks.CURSED_SPRUCE_DOOR.get());
            tag(BlockTags.PLANKS).add(ModBlocks.DARK_SPRUCE_PLANKS.get(), ModBlocks.CURSED_SPRUCE_PLANKS.get());
            tag(BlockTags.WOODEN_BUTTONS).add(ModBlocks.DARK_SPRUCE_BUTTON.get(), ModBlocks.CURSED_SPRUCE_BUTTON.get());
            tag(BlockTags.WOODEN_STAIRS).add(ModBlocks.DARK_SPRUCE_STAIRS.get(), ModBlocks.CURSED_SPRUCE_STAIRS.get());
            tag(BlockTags.WOODEN_SLABS).add(ModBlocks.DARK_SPRUCE_SLAB.get(), ModBlocks.CURSED_SPRUCE_SLAB.get());
            tag(BlockTags.WOODEN_FENCES).add(ModBlocks.DARK_SPRUCE_FENCE.get(), ModBlocks.CURSED_SPRUCE_FENCE.get());
            tag(BlockTags.LOGS_THAT_BURN).addTags(ModTags.Blocks.CURSED_SPRUCE_LOG, ModTags.Blocks.DARK_SPRUCE_LOG);
            tag(BlockTags.LOGS).addTags(ModTags.Blocks.CURSED_SPRUCE_LOG, ModTags.Blocks.DARK_SPRUCE_LOG);
            tag(BlockTags.WOODEN_PRESSURE_PLATES).add(ModBlocks.DARK_SPRUCE_PRESSURE_PLACE.get(), ModBlocks.CURSED_SPRUCE_PRESSURE_PLACE.get());
            tag(BlockTags.WOODEN_DOORS).add(ModBlocks.DARK_SPRUCE_DOOR.get(), ModBlocks.CURSED_SPRUCE_DOOR.get());
            tag(BlockTags.WOODEN_TRAPDOORS).add(ModBlocks.DARK_SPRUCE_TRAPDOOR.get(), ModBlocks.CURSED_SPRUCE_TRAPDOOR.get());
            tag(ModTags.Blocks.TOTEM_TOP_FRAGILE).add(ModBlocks.TOTEM_TOP.get(), ModBlocks.TOTEM_TOP_VAMPIRISM_HUNTER.get(), ModBlocks.TOTEM_TOP_VAMPIRISM_VAMPIRE.get());
            tag(ModTags.Blocks.TOTEM_TOP_CRAFTED).add(ModBlocks.TOTEM_TOP_CRAFTED.get(), ModBlocks.TOTEM_TOP_VAMPIRISM_HUNTER_CRAFTED.get(), ModBlocks.TOTEM_TOP_VAMPIRISM_VAMPIRE_CRAFTED.get());
            tag(ModTags.Blocks.TOTEM_TOP).addTag(ModTags.Blocks.TOTEM_TOP_FRAGILE).addTag(ModTags.Blocks.TOTEM_TOP_CRAFTED);
            tag(ModTags.Blocks.COFFIN).add(ModBlocks.COFFIN_RED.get()).add(ModBlocks.COFFIN_BLUE.get()).add(ModBlocks.COFFIN_GREEN.get()).add(ModBlocks.COFFIN_BROWN.get()).add(ModBlocks.COFFIN_BLACK.get()).add(ModBlocks.COFFIN_GRAY.get()).add(ModBlocks.COFFIN_LIGHT_BLUE.get()).add(ModBlocks.COFFIN_WHITE.get()).add(ModBlocks.COFFIN_LIGHT_GRAY.get()).add(ModBlocks.COFFIN_CYAN.get()).add(ModBlocks.COFFIN_PURPLE.get()).add(ModBlocks.COFFIN_PINK.get()).add(ModBlocks.COFFIN_LIME.get()).add(ModBlocks.COFFIN_YELLOW.get()).add(ModBlocks.COFFIN_ORANGE.get()).add(ModBlocks.COFFIN_MAGENTA.get());
            tag(BlockTags.CEILING_HANGING_SIGNS).add(ModBlocks.DARK_SPRUCE_HANGING_SIGN.get(), ModBlocks.CURSED_SPRUCE_HANGING_SIGN.get());
            tag(BlockTags.WALL_HANGING_SIGNS).add(ModBlocks.DARK_SPRUCE_WALL_HANGING_SIGN.get(), ModBlocks.CURSED_SPRUCE_WALL_HANGING_SIGN.get());
            tag(ModTags.Blocks.DARK_STONE).add(ModBlocks.DARK_STONE.get(), ModBlocks.DARK_STONE_STAIRS.get(), ModBlocks.DARK_STONE_WALL.get(), ModBlocks.DARK_STONE_SLAB.get(), ModBlocks.INFESTED_DARK_STONE.get());
            tag(ModTags.Blocks.DARK_STONE_BRICKS).add(ModBlocks.DARK_STONE_BRICKS.get(), ModBlocks.DARK_STONE_BRICK_WALL.get(), ModBlocks.DARK_STONE_BRICK_SLAB.get(), ModBlocks.DARK_STONE_BRICK_STAIRS.get(), ModBlocks.CHISELED_DARK_STONE_BRICKS.get(), ModBlocks.BLOODY_DARK_STONE_BRICKS.get(), ModBlocks.CRACKED_DARK_STONE_BRICKS.get());
            tag(ModTags.Blocks.PURPLE_STONE_BRICKS).add(ModBlocks.PURPLE_STONE_BRICKS.get(), ModBlocks.PURPLE_STONE_BRICK_WALL.get(), ModBlocks.PURPLE_STONE_BRICK_SLAB.get(), ModBlocks.PURPLE_STONE_BRICK_STAIRS.get());
            tag(ModTags.Blocks.POLISHED_DARK_STONE).add(ModBlocks.POLISHED_DARK_STONE.get(), ModBlocks.POLISHED_DARK_STONE_STAIRS.get(), ModBlocks.POLISHED_DARK_STONE_SLAB.get(), ModBlocks.POLISHED_DARK_STONE_WALL.get());
            tag(ModTags.Blocks.COBBLED_DARK_STONE).add(ModBlocks.COBBLED_DARK_STONE.get(), ModBlocks.COBBLED_DARK_STONE_STAIRS.get(), ModBlocks.COBBLED_DARK_STONE_SLAB.get(), ModBlocks.POLISHED_DARK_STONE_WALL.get());
            tag(ModTags.Blocks.DARK_STONE_TILES).add(ModBlocks.DARK_STONE_TILES.get(), ModBlocks.DARK_STONE_TILES_STAIRS.get(), ModBlocks.DARK_STONE_TILES_SLAB.get(), ModBlocks.DARK_STONE_TILES_WALL.get(), ModBlocks.CRACKED_DARK_STONE_TILES.get());
            tag(ModTags.Blocks.PURPLE_STONE_TILES).add(ModBlocks.PURPLE_STONE_TILES.get(), ModBlocks.PURPLE_STONE_TILES_STAIRS.get(), ModBlocks.PURPLE_STONE_TILES_SLAB.get(), ModBlocks.PURPLE_STONE_TILES_WALL.get());
            tag(ModTags.Blocks.NO_SPAWN).addTag(ModTags.Blocks.DARK_STONE);
            tag(ModTags.Blocks.VAMPIRE_SPAWN).addTags(ModTags.Blocks.DARK_STONE_BRICKS, ModTags.Blocks.COBBLED_DARK_STONE, ModTags.Blocks.POLISHED_DARK_STONE, ModTags.Blocks.DARK_STONE_TILES);
            tag(Tags.Blocks.STONE).add(ModBlocks.DARK_STONE.get(), ModBlocks.INFESTED_DARK_STONE.get(), ModBlocks.POLISHED_DARK_STONE.get());
            tag(BlockTags.WALLS).add(ModBlocks.DARK_STONE_BRICK_WALL.get(), ModBlocks.POLISHED_DARK_STONE_WALL.get(), ModBlocks.COBBLED_DARK_STONE_WALL.get(), ModBlocks.DARK_STONE_WALL.get(), ModBlocks.DARK_STONE_TILES_WALL.get(), ModBlocks.PURPLE_STONE_BRICK_WALL.get(), ModBlocks.PURPLE_STONE_TILES_WALL.get());
            tag(BlockTags.STAIRS).add(ModBlocks.DARK_STONE_BRICK_STAIRS.get(), ModBlocks.POLISHED_DARK_STONE_STAIRS.get(), ModBlocks.COBBLED_DARK_STONE_STAIRS.get(), ModBlocks.DARK_STONE_STAIRS.get(), ModBlocks.DARK_STONE_TILES_STAIRS.get(), ModBlocks.PURPLE_STONE_BRICK_STAIRS.get(), ModBlocks.PURPLE_STONE_TILES_STAIRS.get());
            tag(BlockTags.SLABS).add(ModBlocks.DARK_STONE_BRICK_SLAB.get(), ModBlocks.POLISHED_DARK_STONE_SLAB.get(), ModBlocks.COBBLED_DARK_STONE_SLAB.get(), ModBlocks.DARK_STONE_SLAB.get(), ModBlocks.DARK_STONE_TILES_SLAB.get(), ModBlocks.PURPLE_STONE_BRICK_SLAB.get(), ModBlocks.PURPLE_STONE_TILES_SLAB.get());
            tag(ModTags.Blocks.MOTHER_GROWS_ON).addTag(BlockTags.DIRT);
            tag(Tags.Blocks.STORAGE_BLOCKS).add(ModBlocks.BLOOD_INFUSED_IRON_BLOCK.get(), ModBlocks.BLOOD_INFUSED_ENHANCED_IRON_BLOCK.get());
            tag(ModTags.Blocks.VAMPIRE_BEACON_BASE_BLOCKS).add(ModBlocks.BLOOD_INFUSED_IRON_BLOCK.get(), ModBlocks.BLOOD_INFUSED_ENHANCED_IRON_BLOCK.get());
            tag(ModTags.Blocks.VAMPIRE_BEACON_BASE_ENHANCED_BLOCKS).add(ModBlocks.BLOOD_INFUSED_ENHANCED_IRON_BLOCK.get());
            tag(BlockTags.CANDLES).add(ModBlocks.CANDLE_STICK_NORMAL.get(), ModBlocks.WALL_CANDLE_STICK_NORMAL.get(), ModBlocks.CANDLE_STICK_WHITE.get(), ModBlocks.WALL_CANDLE_STICK_WHITE.get(), ModBlocks.CANDLE_STICK_ORANGE.get(), ModBlocks.WALL_CANDLE_STICK_ORANGE.get(), ModBlocks.CANDLE_STICK_MAGENTA.get(), ModBlocks.WALL_CANDLE_STICK_MAGENTA.get(), ModBlocks.CANDLE_STICK_LIGHT_BLUE.get(), ModBlocks.WALL_CANDLE_STICK_LIGHT_BLUE.get(), ModBlocks.CANDLE_STICK_YELLOW.get(), ModBlocks.WALL_CANDLE_STICK_YELLOW.get(), ModBlocks.CANDLE_STICK_LIME.get(), ModBlocks.WALL_CANDLE_STICK_LIME.get(), ModBlocks.CANDLE_STICK_PINK.get(), ModBlocks.WALL_CANDLE_STICK_PINK.get(), ModBlocks.CANDLE_STICK_GRAY.get(), ModBlocks.WALL_CANDLE_STICK_GRAY.get(), ModBlocks.CANDLE_STICK_LIGHT_GRAY.get(), ModBlocks.WALL_CANDLE_STICK_LIGHT_GRAY.get(), ModBlocks.CANDLE_STICK_CYAN.get(), ModBlocks.WALL_CANDLE_STICK_CYAN.get(), ModBlocks.CANDLE_STICK_PURPLE.get(), ModBlocks.WALL_CANDLE_STICK_PURPLE.get(), ModBlocks.CANDLE_STICK_BLUE.get(), ModBlocks.WALL_CANDLE_STICK_BLUE.get(), ModBlocks.CANDLE_STICK_BROWN.get(), ModBlocks.WALL_CANDLE_STICK_BROWN.get(), ModBlocks.CANDLE_STICK_GREEN.get(), ModBlocks.WALL_CANDLE_STICK_GREEN.get(), ModBlocks.CANDLE_STICK_RED.get(), ModBlocks.WALL_CANDLE_STICK_RED.get(), ModBlocks.CANDLE_STICK_BLACK.get(), ModBlocks.WALL_CANDLE_STICK_BLACK.get());
        }
    }

    public static class ModItemTagsProvider extends ItemTagsProvider {
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
            copy(ModTags.Blocks.CURSED_EARTH, ModTags.Items.CURSEDEARTH);
            copy(ModTags.Blocks.DARK_SPRUCE_LOG, ModTags.Items.DARK_SPRUCE_LOG);
            copy(ModTags.Blocks.CURSED_SPRUCE_LOG, ModTags.Items.CURSED_SPRUCE_LOG);
            copy(ModTags.Blocks.DARK_STONE, ModTags.Items.DARK_STONE);
            copy(ModTags.Blocks.DARK_STONE_BRICKS, ModTags.Items.DARK_STONE_BRICKS);
            copy(ModTags.Blocks.POLISHED_DARK_STONE, ModTags.Items.POLISHED_DARK_STONE);
            copy(ModTags.Blocks.COBBLED_DARK_STONE, ModTags.Items.COBBLED_DARK_STONE);
            copy(ModTags.Blocks.DARK_STONE_TILES, ModTags.Items.DARK_STONE_TILES);
            copy(ModTags.Blocks.NO_SPAWN, ModTags.Items.NO_SPAWN);
            copy(ModTags.Blocks.VAMPIRE_SPAWN, ModTags.Items.VAMPIRE_SPAWN);
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

            tag(ModTags.Items.CROSSBOW_ARROW).add(ModItems.CROSSBOW_ARROW_NORMAL.get(), ModItems.CROSSBOW_ARROW_SPITFIRE.get(), ModItems.CROSSBOW_ARROW_VAMPIRE_KILLER.get(), ModItems.CROSSBOW_ARROW_TELEPORT.get(), ModItems.CROSSBOW_ARROW_BLEEDING.get(), ModItems.CROSSBOW_ARROW_GARLIC.get());
            tag(ModTags.Items.HUNTER_INTEL).add(ModItems.HUNTER_INTEL_0.get(), ModItems.HUNTER_INTEL_1.get(), ModItems.HUNTER_INTEL_2.get(), ModItems.HUNTER_INTEL_3.get(), ModItems.HUNTER_INTEL_4.get(), ModItems.HUNTER_INTEL_5.get(), ModItems.HUNTER_INTEL_6.get(), ModItems.HUNTER_INTEL_7.get(), ModItems.HUNTER_INTEL_8.get(), ModItems.HUNTER_INTEL_9.get());
            tag(ModTags.Items.PURE_BLOOD).add(ModItems.PURE_BLOOD_0.get(), ModItems.PURE_BLOOD_1.get(), ModItems.PURE_BLOOD_2.get(), ModItems.PURE_BLOOD_3.get(), ModItems.PURE_BLOOD_4.get());
            tag(ModTags.Items.VAMPIRE_CLOAK).add(ModItems.VAMPIRE_CLOAK_BLACK_BLUE.get(), ModItems.VAMPIRE_CLOAK_BLACK_RED.get(), ModItems.VAMPIRE_CLOAK_BLACK_WHITE.get(), ModItems.VAMPIRE_CLOAK_RED_BLACK.get(), ModItems.VAMPIRE_CLOAK_WHITE_BLACK.get());
            tag(ItemTags.SMALL_FLOWERS).add(ModBlocks.VAMPIRE_ORCHID.get().asItem());
            tag(ModTags.Items.GARLIC).add(ModItems.ITEM_GARLIC.get());
            tag(ModTags.Items.HOLY_WATER).add(ModItems.HOLY_WATER_BOTTLE_NORMAL.get(), ModItems.HOLY_WATER_BOTTLE_ENHANCED.get(), ModItems.HOLY_WATER_BOTTLE_ULTIMATE.get());
            tag(ModTags.Items.HOLY_WATER_SPLASH).add(ModItems.HOLY_WATER_SPLASH_BOTTLE_NORMAL.get(), ModItems.HOLY_WATER_SPLASH_BOTTLE_ENHANCED.get(), ModItems.HOLY_WATER_SPLASH_BOTTLE_ULTIMATE.get());
            tag(ItemTags.PIGLIN_LOVED).add(ModItems.VAMPIRE_CLOTHING_CROWN.get());
            tag(ModTags.Items.HEART).add(ModItems.HUMAN_HEART.get(), ModItems.WEAK_HUMAN_HEART.get());
            tag(ItemTags.BOATS).add(ModItems.DARK_SPRUCE_BOAT.get(), ModItems.CURSED_SPRUCE_BOAT.get());
            tag(ModTags.Items.APPLICABLE_OIL_ARMOR).add(Items.LEATHER_BOOTS, Items.LEATHER_LEGGINGS, Items.LEATHER_HELMET, Items.LEATHER_CHESTPLATE, ModItems.VAMPIRE_CLOTHING_LEGS.get(), ModItems.VAMPIRE_CLOTHING_BOOTS.get(), ModItems.VAMPIRE_CLOTHING_CROWN.get(), ModItems.VAMPIRE_CLOTHING_HAT.get(), ModItems.VAMPIRE_CLOAK_RED_BLACK.get(), ModItems.VAMPIRE_CLOAK_BLACK_RED.get(), ModItems.VAMPIRE_CLOAK_BLACK_WHITE.get(), ModItems.VAMPIRE_CLOAK_WHITE_BLACK.get(), ModItems.VAMPIRE_CLOAK_BLACK_BLUE.get());
            tag(ModTags.Items.APPLICABLE_OIL_PICKAXE);
            tag(ModTags.Items.APPLICABLE_OIL_SWORD);
            tag(ItemTags.SIGNS).add(ModItems.DARK_SPRUCE_SIGN.get(), ModItems.CURSED_SPRUCE_SIGN.get());
            tag(ItemTags.HANGING_SIGNS).add(ModItems.DARK_SPRUCE_HANGING_SIGN.get(), ModItems.CURSED_SPRUCE_HANGING_SIGN.get());
            tag(ModTags.Items.HUNTER_COAT).add(ModItems.HUNTER_COAT_HEAD_NORMAL.get(),ModItems.HUNTER_COAT_HEAD_ENHANCED.get(),ModItems.HUNTER_COAT_HEAD_ULTIMATE.get(), ModItems.HUNTER_COAT_CHEST_NORMAL.get(),ModItems.HUNTER_COAT_CHEST_ENHANCED.get(),ModItems.HUNTER_COAT_CHEST_ULTIMATE.get(), ModItems.HUNTER_COAT_LEGS_NORMAL.get(),ModItems.HUNTER_COAT_LEGS_ENHANCED.get(),ModItems.HUNTER_COAT_LEGS_ULTIMATE.get(), ModItems.HUNTER_COAT_FEET_NORMAL.get(),ModItems.HUNTER_COAT_FEET_ENHANCED.get(),ModItems.HUNTER_COAT_FEET_ULTIMATE.get());
            tag(ItemTags.FREEZE_IMMUNE_WEARABLES).addTag(ModTags.Items.HUNTER_COAT);
            tag(ModTags.Items.VAMPIRE_BEACON_PAYMENT_ITEM).addTags(ModTags.Items.PURE_BLOOD, ModTags.Items.HEART).add(ModItems.SOUL_ORB_VAMPIRE.get());
            tag(ModTags.Items.SWIFTNESS_ARMOR).add(ModItems.ARMOR_OF_SWIFTNESS_FEET_NORMAL.get(), ModItems.ARMOR_OF_SWIFTNESS_FEET_ENHANCED.get(), ModItems.ARMOR_OF_SWIFTNESS_FEET_ULTIMATE.get(), ModItems.ARMOR_OF_SWIFTNESS_LEGS_NORMAL.get(), ModItems.ARMOR_OF_SWIFTNESS_LEGS_ENHANCED.get(), ModItems.ARMOR_OF_SWIFTNESS_LEGS_ULTIMATE.get(), ModItems.ARMOR_OF_SWIFTNESS_CHEST_NORMAL.get(), ModItems.ARMOR_OF_SWIFTNESS_CHEST_ENHANCED.get(), ModItems.ARMOR_OF_SWIFTNESS_CHEST_ULTIMATE.get(), ModItems.ARMOR_OF_SWIFTNESS_HEAD_NORMAL.get(), ModItems.ARMOR_OF_SWIFTNESS_HEAD_ENHANCED.get(), ModItems.ARMOR_OF_SWIFTNESS_HEAD_ULTIMATE.get());
            tag(ModTags.Items.HUNTER_ARMOR).addTags(ModTags.Items.SWIFTNESS_ARMOR, ModTags.Items.HUNTER_COAT);
        }
    }

    public static class ModEntityTypeTagsProvider extends EntityTypeTagsProvider {
        public ModEntityTypeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
            super(output, lookupProvider, REFERENCE.MODID, existingFileHelper);
        }

        @Override
        protected void addTags(HolderLookup.Provider holderLookup) {
            tag(ModTags.Entities.HUNTER).add(ModEntities.HUNTER.get(), ModEntities.HUNTER_IMOB.get(), ModEntities.ADVANCED_HUNTER.get(), ModEntities.ADVANCED_HUNTER_IMOB.get(), ModEntities.HUNTER_TRAINER.get(), ModEntities.HUNTER_TRAINER.get(), ModEntities.HUNTER_TRAINER_DUMMY.get(), ModEntities.TASK_MASTER_HUNTER.get());
            tag(ModTags.Entities.VAMPIRE).add(ModEntities.VAMPIRE.get(), ModEntities.VAMPIRE_IMOB.get(), ModEntities.ADVANCED_VAMPIRE.get(), ModEntities.ADVANCED_VAMPIRE_IMOB.get(), ModEntities.VAMPIRE_BARON.get(), ModEntities.TASK_MASTER_VAMPIRE.get());
            tag(ModTags.Entities.ADVANCED_HUNTER).add(ModEntities.ADVANCED_HUNTER.get(), ModEntities.ADVANCED_HUNTER_IMOB.get());
            tag(ModTags.Entities.ADVANCED_VAMPIRE).add(ModEntities.ADVANCED_VAMPIRE.get(), ModEntities.ADVANCED_VAMPIRE_IMOB.get());
            tag(ModTags.Entities.ZOMBIES).add(EntityType.ZOMBIE, EntityType.HUSK, EntityType.DROWNED, EntityType.ZOMBIE_VILLAGER, EntityType.ZOMBIE_HORSE);
            tag(ModTags.Entities.IGNORE_VAMPIRE_SWORD_FINISHER).add(ModEntities.VULNERABLE_REMAINS_DUMMY.get(), ModEntities.GHOST.get());
        }
    }

    public static class ModFluidTagsProvider extends FluidTagsProvider {
        public ModFluidTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
            super(output, lookupProvider, REFERENCE.MODID, existingFileHelper);
        }

        @NotNull
        @Override
        public String getName() {
            return REFERENCE.MODID + " " + super.getName();
        }

        @Override
        protected void addTags(HolderLookup.Provider holderLookup) {
            tag(ModTags.Fluids.BLOOD).add(ModFluids.BLOOD.get());
            tag(ModTags.Fluids.IMPURE_BLOOD).add(ModFluids.IMPURE_BLOOD.get());
        }
    }

    public static class ModBiomeTagsProvider extends BiomeTagsProvider {

        public ModBiomeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
            super(output, lookupProvider, REFERENCE.MODID, existingFileHelper);
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void addTags(HolderLookup.@NotNull Provider holderProvider) {
            tag(ModTags.Biomes.HasStructure.HUNTER_TENT).addTags(BiomeTags.IS_BADLANDS, BiomeTags.IS_FOREST, BiomeTags.IS_TAIGA).add(Biomes.PLAINS, Biomes.DESERT, Biomes.MEADOW, Biomes.SNOWY_PLAINS, Biomes.SPARSE_JUNGLE);
            tag(ModTags.Biomes.IS_FACTION_BIOME).addTags(ModTags.Biomes.IS_VAMPIRE_BIOME, ModTags.Biomes.IS_HUNTER_BIOME);
            tag(ModTags.Biomes.IS_VAMPIRE_BIOME).add(ModBiomes.VAMPIRE_FOREST);
            tag(ModTags.Biomes.IS_HUNTER_BIOME);
            tag(ModTags.Biomes.HasStructure.VAMPIRE_ALTAR).addTags(Tags.Biomes.IS_WASTELAND, Tags.Biomes.IS_PLATEAU, Tags.Biomes.IS_RARE, Tags.Biomes.IS_SPOOKY, Tags.Biomes.IS_SWAMP, ModTags.Biomes.IS_VAMPIRE_BIOME);
            tag(ModTags.Biomes.HasStructure.HUNTER_OUTPOST_PLAINS).addTags(Tags.Biomes.IS_PLAINS, BiomeTags.IS_FOREST);
            tag(ModTags.Biomes.HasStructure.HUNTER_OUTPOST_DESERT).addTags(Tags.Biomes.IS_DESERT);
            tag(ModTags.Biomes.HasStructure.HUNTER_OUTPOST_VAMPIRE_FOREST).add(ModBiomes.VAMPIRE_FOREST);
            tag(ModTags.Biomes.HasStructure.HUNTER_OUTPOST_BADLANDS).addTags(BiomeTags.IS_BADLANDS);
            tag(BiomeTags.IS_FOREST).add(ModBiomes.VAMPIRE_FOREST);
            tag(BiomeTags.IS_OVERWORLD).add(ModBiomes.VAMPIRE_FOREST);
            tag(Tags.Biomes.IS_DENSE_OVERWORLD).add(ModBiomes.VAMPIRE_FOREST);
            tag(Tags.Biomes.IS_MAGICAL).add(ModBiomes.VAMPIRE_FOREST);
            tag(Tags.Biomes.IS_SPOOKY).add(ModBiomes.VAMPIRE_FOREST);
            tag(ModTags.Biomes.HasStructure.VAMPIRE_DUNGEON).addTags(BiomeTags.IS_OVERWORLD);
            tag(ModTags.Biomes.HasSpawn.VAMPIRE).addTags(BiomeTags.IS_OVERWORLD);
            tag(ModTags.Biomes.NoSpawn.VAMPIRE).addTags(ModTags.Biomes.IS_FACTION_BIOME, Tags.Biomes.IS_UNDERGROUND, Tags.Biomes.IS_MUSHROOM);
            tag(ModTags.Biomes.HasSpawn.ADVANCED_VAMPIRE).addTags(BiomeTags.IS_OVERWORLD);
            tag(ModTags.Biomes.NoSpawn.ADVANCED_VAMPIRE).addTags(ModTags.Biomes.IS_FACTION_BIOME, Tags.Biomes.IS_UNDERGROUND, Tags.Biomes.IS_MUSHROOM);
            tag(ModTags.Biomes.HasSpawn.HUNTER).addTags(BiomeTags.IS_OVERWORLD);
            tag(ModTags.Biomes.NoSpawn.HUNTER).addTags(ModTags.Biomes.IS_FACTION_BIOME);
            tag(ModTags.Biomes.HasSpawn.ADVANCED_HUNTER).addTags(BiomeTags.IS_OVERWORLD);
            tag(ModTags.Biomes.NoSpawn.ADVANCED_HUNTER).addTags(ModTags.Biomes.IS_FACTION_BIOME);
            tag(ModTags.Biomes.HasStructure.VAMPIRE_HUT).addTags(ModTags.Biomes.IS_VAMPIRE_BIOME);
            tag(ModTags.Biomes.HasStructure.MOTHER).addTag(ModTags.Biomes.IS_VAMPIRE_BIOME);
            tag(ModTags.Biomes.HasStructure.CRYPT).addTag(ModTags.Biomes.IS_VAMPIRE_BIOME);
        }
    }

    public static class ModPoiTypeProvider extends PoiTypeTagsProvider {

        public ModPoiTypeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
            super(output, lookupProvider, REFERENCE.MODID, existingFileHelper);
        }

        @Override
        protected void addTags(HolderLookup.@NotNull Provider holderProvider) {
            tag(ModTags.PoiTypes.HAS_FACTION).add(ModVillage.NO_FACTION_TOTEM.getKey(), ModVillage.HUNTER_TOTEM.getKey(), ModVillage.VAMPIRE_TOTEM.getKey());
            tag(ModTags.PoiTypes.IS_HUNTER).add(ModVillage.HUNTER_TOTEM.getKey());
            tag(ModTags.PoiTypes.IS_VAMPIRE).add(ModVillage.VAMPIRE_TOTEM.getKey());
            tag(PoiTypeTags.ACQUIRABLE_JOB_SITE).add(ModVillage.HUNTER_TOTEM.getKey(), ModVillage.VAMPIRE_TOTEM.getKey(), ModVillage.ALTAR_CLEANSING.getKey());
            tag(PoiTypeTags.VILLAGE).add(ModVillage.NO_FACTION_TOTEM.getKey(), ModVillage.HUNTER_TOTEM.getKey(), ModVillage.VAMPIRE_TOTEM.getKey(), ModVillage.ALTAR_CLEANSING.getKey());
        }
    }

    public static class ModVillageProfessionProvider extends TagsProvider<VillagerProfession> {

        public ModVillageProfessionProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
            super(output, Registries.VILLAGER_PROFESSION, lookupProvider, REFERENCE.MODID, existingFileHelper);
        }

        @Override
        protected void addTags(HolderLookup.@NotNull Provider holderProvider) {
            tag(ModTags.Professions.HAS_FACTION).add(ModVillage.HUNTER_EXPERT.getKey(), ModVillage.VAMPIRE_EXPERT.getKey());
            tag(ModTags.Professions.IS_VAMPIRE).add(ModVillage.VAMPIRE_EXPERT.getKey());
            tag(ModTags.Professions.IS_HUNTER).add(ModVillage.HUNTER_EXPERT.getKey());
        }
    }

    public static class ModDamageTypeProvider extends TagsProvider<DamageType> {

        public ModDamageTypeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, @Nullable ExistingFileHelper existingFileHelper) {
            super(output, Registries.DAMAGE_TYPE, provider, REFERENCE.MODID, existingFileHelper);
        }

        @Override
        protected void addTags(HolderLookup.@NotNull Provider pProvider) {
            this.tag(DamageTypeTags.BYPASSES_ARMOR).add(ModDamageTypes.SUN_DAMAGE, ModDamageTypes.NO_BLOOD, ModDamageTypes.VAMPIRE_ON_FIRE, ModDamageTypes.DBNO, ModDamageTypes.MOTHER, ModDamageTypes.STAKE);
            this.tag(DamageTypeTags.BYPASSES_EFFECTS).add(ModDamageTypes.DBNO, ModDamageTypes.STAKE);
            this.tag(DamageTypeTags.IS_FIRE).add(ModDamageTypes.VAMPIRE_ON_FIRE, ModDamageTypes.VAMPIRE_IN_FIRE);
            this.tag(DamageTypeTags.WITCH_RESISTANT_TO).add(ModDamageTypes.SUN_DAMAGE, ModDamageTypes.VAMPIRE_ON_FIRE, ModDamageTypes.VAMPIRE_IN_FIRE, ModDamageTypes.NO_BLOOD, ModDamageTypes.HOLY_WATER);
            this.tag(ModTags.DamageTypes.ENTITY_PHYSICAL).add(DamageTypes.PLAYER_ATTACK, DamageTypes.MOB_ATTACK, DamageTypes.MOB_ATTACK_NO_AGGRO, DamageTypes.MOB_PROJECTILE, DamageTypes.ARROW, DamageTypes.STING, DamageTypes.THORNS);
            this.tag(ModTags.DamageTypes.REMAINS_INVULNERABLE).add(DamageTypes.IN_WALL, DamageTypes.DROWN);
            this.tag(ModTags.DamageTypes.MOTHER_RESISTANT_TO).add(DamageTypes.ON_FIRE, DamageTypes.IN_FIRE, ModDamageTypes.HOLY_WATER, DamageTypes.FREEZE, DamageTypes.MAGIC, DamageTypes.INDIRECT_MAGIC);
            this.tag(ModTags.DamageTypes.VAMPIRE_IMMORTAL).add(DamageTypes.PLAYER_ATTACK, DamageTypes.MOB_ATTACK, DamageTypes.DROWN, DamageTypes.ON_FIRE, DamageTypes.CRAMMING, DamageTypes.FALL, DamageTypes.FLY_INTO_WALL, DamageTypes.MAGIC, DamageTypes.MAGIC, DamageTypes.WITHER, DamageTypes.FALLING_ANVIL, DamageTypes.FALLING_BLOCK, DamageTypes.DRAGON_BREATH, DamageTypes.SWEET_BERRY_BUSH, DamageTypes.TRIDENT, DamageTypes.ARROW, DamageTypes.FIREWORKS, DamageTypes.FIREBALL, DamageTypes.WITHER_SKULL, DamageTypes.EXPLOSION, DamageTypes.PLAYER_EXPLOSION, DamageTypes.THROWN, DamageTypes.INDIRECT_MAGIC, ModDamageTypes.VAMPIRE_ON_FIRE, DamageTypes.STING, DamageTypes.FALLING_STALACTITE, DamageTypes.STALAGMITE, DamageTypes.FREEZE)
                    .addOptional(new ResourceLocation("mekanism", "radiation"));
        }
    }

    public static class ModTasksProvider extends TagsProvider<Task> {

        protected ModTasksProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, @Nullable ExistingFileHelper existingFileHelper) {
            super(output, VampirismRegistries.Keys.TASK, provider, REFERENCE.MODID, existingFileHelper);
        }

        @Override
        protected void addTags(HolderLookup.@NotNull Provider pProvider) {
            this.tag(ModTags.Tasks.HAS_FACTION).addTags(ModTags.Tasks.IS_VAMPIRE, ModTags.Tasks.IS_HUNTER);
            this.tag(ModTags.Tasks.IS_VAMPIRE).add(
                    ModTasks.FEEDING_ADAPTER,
                    ModTasks.VAMPIRE_LORD_1,
                    ModTasks.VAMPIRE_LORD_2,
                    ModTasks.VAMPIRE_LORD_3,
                    ModTasks.VAMPIRE_LORD_4,
                    ModTasks.VAMPIRE_LORD_5,
                    ModTasks.FIRE_RESISTANCE_1,
                    ModTasks.FIRE_RESISTANCE_2,
                    ModTasks.VAMPIRE_MINION_BINDING,
                    ModTasks.VAMPIRE_MINION_UPGRADE_SIMPLE,
                    ModTasks.VAMPIRE_MINION_UPGRADE_ENHANCED,
                    ModTasks.VAMPIRE_MINION_UPGRADE_SPECIAL,
                    ModTasks.V_INFECT_1,
                    ModTasks.V_INFECT_2,
                    ModTasks.V_INFECT_3,
                    ModTasks.V_CAPTURE_1,
                    ModTasks.V_CAPTURE_2,
                    ModTasks.V_KILL_1,
                    ModTasks.V_KILL_2,
                    ModTasks.RANDOM_REFINEMENT_1,
                    ModTasks.RANDOM_REFINEMENT_2,
                    ModTasks.RANDOM_REFINEMENT_3,
                    ModTasks.RANDOM_RARE_REFINEMENT
            );
            this.tag(ModTags.Tasks.IS_HUNTER).add(
                    ModTasks.HUNTER_LORD_1,
                    ModTasks.HUNTER_LORD_2,
                    ModTasks.HUNTER_LORD_3,
                    ModTasks.HUNTER_LORD_4,
                    ModTasks.HUNTER_LORD_5,
                    ModTasks.HUNTER_MINION_EQUIPMENT,
                    ModTasks.HUNTER_MINION_UPGRADE_SIMPLE,
                    ModTasks.HUNTER_MINION_UPGRADE_ENHANCED,
                    ModTasks.HUNTER_MINION_UPGRADE_SPECIAL,
                    ModTasks.H_KILL_1,
                    ModTasks.H_KILL_2,
                    ModTasks.H_CAPTURE_1
            );
            this.tag(ModTags.Tasks.AWARDS_LORD_LEVEL).add(
                    ModTasks.HUNTER_LORD_1,
                    ModTasks.HUNTER_LORD_2,
                    ModTasks.HUNTER_LORD_3,
                    ModTasks.HUNTER_LORD_4,
                    ModTasks.HUNTER_LORD_5,
                    ModTasks.VAMPIRE_LORD_1,
                    ModTasks.VAMPIRE_LORD_2,
                    ModTasks.VAMPIRE_LORD_3,
                    ModTasks.VAMPIRE_LORD_4,
                    ModTasks.VAMPIRE_LORD_5
            );
            this.tag(ModTags.Tasks.IS_UNIQUE)
                    .addTag(ModTags.Tasks.AWARDS_LORD_LEVEL)
            ;
        }
    }

    public static class ModStructuresProvider extends TagsProvider<Structure> {

        protected ModStructuresProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, @Nullable ExistingFileHelper existingFileHelper) {
            super(output, Registries.STRUCTURE, provider, REFERENCE.MODID, existingFileHelper);
        }

        @Override
        protected void addTags(HolderLookup.@NotNull Provider pProvider) {
            this.tag(ModTags.Structures.HUNTER_OUTPOST).add(ModStructures.HUNTER_OUTPOST_BADLANDS, ModStructures.HUNTER_OUTPOST_DESERT, ModStructures.HUNTER_OUTPOST_PLAINS, ModStructures.HUNTER_OUTPOST_VAMPIRE_FOREST);
        }
    }

    public static class ModSkillTreeProvider extends TagsProvider<ISkillTree> {

        protected ModSkillTreeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, @Nullable ExistingFileHelper existingFileHelper) {
            super(output, VampirismRegistries.Keys.SKILL_TREE, provider, REFERENCE.MODID, existingFileHelper);
        }

        @Override
        protected void addTags(HolderLookup.@NotNull Provider pProvider) {
            this.tag(ModTags.SkillTrees.HUNTER).add(HunterSkills.Trees.LEVEL, HunterSkills.Trees.LORD);
            this.tag(ModTags.SkillTrees.VAMPIRE).add(VampireSkills.Trees.LEVEL, VampireSkills.Trees.LORD);
            this.tag(ModTags.SkillTrees.LEVEL).add(HunterSkills.Trees.LEVEL, VampireSkills.Trees.LEVEL);
            this.tag(ModTags.SkillTrees.LORD).add(HunterSkills.Trees.LORD, VampireSkills.Trees.LORD);
        }
    }
}
