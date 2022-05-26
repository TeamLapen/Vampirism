package de.teamlapen.vampirism.data;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.core.*;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.*;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.Biomes;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class TagGenerator {

    public static void register(DataGenerator generator, ExistingFileHelper helper) {
        BlockTagsProvider blockTagsProvider = new ModBlockTagsProvider(generator, helper);
        generator.addProvider(blockTagsProvider);
        generator.addProvider(new ModItemTagsProvider(generator, blockTagsProvider, helper));
        generator.addProvider(new ModEntityTypeTagsProvider(generator, helper));
        generator.addProvider(new ModFluidTagsProvider(generator, helper));
        generator.addProvider(new ModBiomeTagsProvider(generator, helper));
    }

    public static class ModBlockTagsProvider extends BlockTagsProvider {
        public ModBlockTagsProvider(DataGenerator dataGenerator, ExistingFileHelper helper) {
            super(dataGenerator, REFERENCE.MODID, helper);
        }

        @Nonnull
        @Override
        public String getName() {
            return REFERENCE.MODID + " " + super.getName();
        }

        @Override
        protected void addTags() {
            tag(BlockTags.DIRT).add(ModBlocks.cursed_earth.get(), ModBlocks.cursed_grass_block.get());
            tag(ModTags.Blocks.CURSEDEARTH).add(ModBlocks.cursed_earth.get(), ModBlocks.cursed_grass_block.get());
            tag(ModTags.Blocks.CASTLE_BLOCK).add(ModBlocks.castle_block_dark_brick.get(), ModBlocks.castle_block_dark_brick_bloody.get(), ModBlocks.castle_block_dark_stone.get(), ModBlocks.castle_block_normal_brick.get(), ModBlocks.castle_block_purple_brick.get());
            tag(ModTags.Blocks.CASTLE_SLAPS).add(ModBlocks.castle_slab_dark_brick.get(), ModBlocks.castle_slab_dark_stone.get(), ModBlocks.castle_slab_purple_brick.get());
            tag(ModTags.Blocks.CASTLE_STAIRS).add(ModBlocks.castle_stairs_dark_stone.get(), ModBlocks.castle_stairs_dark_brick.get(), ModBlocks.castle_stairs_purple_brick.get());
            tag(BlockTags.STAIRS).addTag(ModTags.Blocks.CASTLE_STAIRS);
            tag(BlockTags.SLABS).addTag(ModTags.Blocks.CASTLE_SLAPS);
            tag(BlockTags.FLOWER_POTS).add(ModBlocks.potted_vampire_orchid.get());
            tag(BlockTags.SPRUCE_LOGS).add(ModBlocks.bloody_spruce_log.get());
            tag(BlockTags.LEAVES).add(ModBlocks.vampire_spruce_leaves.get(), ModBlocks.bloody_spruce_leaves.get());
            tag(BlockTags.SAPLINGS).add(ModBlocks.bloody_spruce_sapling.get(), ModBlocks.vampire_spruce_sapling.get());

            //Tool types
            tag(BlockTags.MINEABLE_WITH_SHOVEL).add(ModBlocks.cursed_earth.get(), ModBlocks.cursed_grass_block.get());
            //noinspection unchecked
            tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.altar_inspiration.get(), ModBlocks.altar_pillar.get(), ModBlocks.altar_tip.get(), ModBlocks.blood_pedestal.get(), ModBlocks.altar_infusion.get(), ModBlocks.grave_cage.get(), ModBlocks.tombstone1.get(), ModBlocks.tombstone2.get(), ModBlocks.tombstone3.get()).addTags(ModTags.Blocks.CASTLE_BLOCK, ModTags.Blocks.CASTLE_SLAPS, ModTags.Blocks.CASTLE_STAIRS);

            tag(BlockTags.NEEDS_STONE_TOOL).add(ModBlocks.altar_inspiration.get(), ModBlocks.altar_tip.get());
            tag(BlockTags.NEEDS_IRON_TOOL).add(ModBlocks.blood_pedestal.get(), ModBlocks.altar_infusion.get());
        }
    }

    public static class ModItemTagsProvider extends ItemTagsProvider {
        public ModItemTagsProvider(DataGenerator dataGenerator, BlockTagsProvider blockTagsProvider, ExistingFileHelper helper) {
            super(dataGenerator, blockTagsProvider, REFERENCE.MODID, helper);
        }

        @Nonnull
        @Override
        public String getName() {
            return REFERENCE.MODID + " " + super.getName();
        }

        @Override
        protected void addTags() {
            copy(ModTags.Blocks.CASTLE_BLOCK, ModTags.Items.CASTLE_BLOCK);
            copy(ModTags.Blocks.CASTLE_STAIRS, ModTags.Items.CASTLE_STAIRS);
            copy(ModTags.Blocks.CASTLE_SLAPS, ModTags.Items.CASTLE_SLAPS);
            copy(ModTags.Blocks.CURSEDEARTH, ModTags.Items.CURSEDEARTH);

            tag(ModTags.Items.CROSSBOW_ARROW).add(ModItems.crossbow_arrow_normal.get(), ModItems.crossbow_arrow_spitfire.get(), ModItems.crossbow_arrow_vampire_killer.get());
            tag(ModTags.Items.HUNTER_INTEL).add(ModItems.hunter_intel_0.get(), ModItems.hunter_intel_1.get(), ModItems.hunter_intel_2.get(), ModItems.hunter_intel_3.get(), ModItems.hunter_intel_4.get(), ModItems.hunter_intel_5.get(), ModItems.hunter_intel_6.get(), ModItems.hunter_intel_7.get(), ModItems.hunter_intel_8.get(), ModItems.hunter_intel_9.get());
            tag(ModTags.Items.PURE_BLOOD).add(ModItems.pure_blood_0.get(), ModItems.pure_blood_1.get(), ModItems.pure_blood_2.get(), ModItems.pure_blood_3.get(), ModItems.pure_blood_4.get());
            tag(ModTags.Items.VAMPIRE_CLOAK).add(ModItems.vampire_cloak_black_blue.get(), ModItems.vampire_cloak_black_red.get(), ModItems.vampire_cloak_black_white.get(), ModItems.vampire_cloak_red_black.get(), ModItems.vampire_cloak_white_black.get());
            tag(ItemTags.SMALL_FLOWERS).add(ModBlocks.vampire_orchid.get().asItem());
            tag(ModTags.Items.GARLIC).add(ModItems.item_garlic.get());
            tag(ModTags.Items.HOLY_WATER).add(ModItems.holy_water_bottle_normal.get(), ModItems.holy_water_bottle_enhanced.get(), ModItems.holy_water_bottle_ultimate.get());
            tag(ModTags.Items.HOLY_WATER_SPLASH).add(ModItems.holy_water_splash_bottle_normal.get(), ModItems.holy_water_splash_bottle_enhanced.get(), ModItems.holy_water_splash_bottle_ultimate.get());
            tag(ItemTags.STAIRS).addTag(ModTags.Items.CASTLE_STAIRS);
            tag(ItemTags.SLABS).addTag(ModTags.Items.CASTLE_SLAPS);
            tag(ItemTags.SPRUCE_LOGS).add(ModBlocks.bloody_spruce_log.get().asItem());
            tag(ItemTags.LEAVES).add(ModBlocks.vampire_spruce_leaves.get().asItem(), ModBlocks.bloody_spruce_leaves.get().asItem());
            tag(ItemTags.SAPLINGS).add(ModBlocks.bloody_spruce_sapling.get().asItem(), ModBlocks.vampire_spruce_sapling.get().asItem());
        }
    }

    public static class ModEntityTypeTagsProvider extends EntityTypeTagsProvider {
        public ModEntityTypeTagsProvider(DataGenerator dataGenerator, ExistingFileHelper helper) {
            super(dataGenerator, REFERENCE.MODID, helper);
        }

        @Override
        protected void addTags() {
            tag(ModTags.Entities.HUNTER).add(ModEntities.hunter, ModEntities.hunter_imob, ModEntities.advanced_hunter, ModEntities.advanced_hunter_imob, ModEntities.hunter_trainer, ModEntities.hunter_trainer, ModEntities.hunter_trainer_dummy, ModEntities.task_master_hunter);
            tag(ModTags.Entities.VAMPIRE).add(ModEntities.vampire, ModEntities.vampire_imob, ModEntities.advanced_vampire, ModEntities.advanced_vampire_imob, ModEntities.vampire_baron, ModEntities.task_master_vampire);
            tag(ModTags.Entities.ADVANCED_HUNTER).add(ModEntities.advanced_hunter, ModEntities.advanced_hunter_imob);
            tag(ModTags.Entities.ADVANCED_VAMPIRE).add(ModEntities.advanced_vampire, ModEntities.advanced_vampire_imob);
            tag(ModTags.Entities.ZOMBIES).add(EntityType.ZOMBIE, EntityType.HUSK, EntityType.DROWNED, EntityType.ZOMBIE_VILLAGER, EntityType.ZOMBIE_HORSE);
        }
    }

    public static class ModFluidTagsProvider extends FluidTagsProvider {
        public ModFluidTagsProvider(DataGenerator generatorIn, ExistingFileHelper helper) {
            super(generatorIn, REFERENCE.MODID, helper);
        }

        @Nonnull
        @Override
        public String getName() {
            return REFERENCE.MODID + " " + super.getName();
        }

        @Override
        protected void addTags() {
            tag(ModTags.Fluids.BLOOD).add(ModFluids.blood.get());
            tag(ModTags.Fluids.IMPURE_BLOOD).add(ModFluids.impure_blood.get());
        }
    }

    public static class ModBiomeTagsProvider extends BiomeTagsProvider {

        public ModBiomeTagsProvider(DataGenerator generator, @Nullable ExistingFileHelper existingFileHelper) {
            super(generator, REFERENCE.MODID, existingFileHelper);
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void addTags() {
            tag(ModTags.Biomes.HAS_HUNTER_TENT).addTags(BiomeTags.IS_BADLANDS, BiomeTags.IS_FOREST, BiomeTags.IS_TAIGA).add(Biomes.PLAINS, Biomes.DESERT, Biomes.MEADOW, Biomes.SNOWY_PLAINS, Biomes.SPARSE_JUNGLE);
            tag(ModTags.Biomes.IS_FACTION_BIOME).addTags(ModTags.Biomes.IS_VAMPIRE_BIOME);
            tag(ModTags.Biomes.IS_VAMPIRE_BIOME).add(ModBiomes.VAMPIRE_FOREST.getKey()());
            tag(BiomeTags.IS_FOREST).add(ModBiomes.VAMPIRE_FOREST.getKey()());
        }
    }
}
