package de.teamlapen.vampirism.data;

import de.teamlapen.vampirism.core.*;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.data.*;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nonnull;

public class TagGenerator {

    public static void register(DataGenerator generator, ExistingFileHelper helper) {
        BlockTagsProvider blockTagsProvider = new ModBlockTagsProvider(generator,helper);
        generator.addProvider(blockTagsProvider);
        generator.addProvider(new ModItemTagsProvider(generator, blockTagsProvider, helper));
        generator.addProvider(new ModEntityTypeTagsProvider(generator, helper));
        generator.addProvider(new ModFluidTagsProvider(generator, helper));
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
        protected void registerTags() {
            getOrCreateBuilder(Tags.Blocks.DIRT).add(ModBlocks.cursed_earth);
            getOrCreateBuilder(ModTags.Blocks.CURSEDEARTH).add(ModBlocks.cursed_earth);
            getOrCreateBuilder(ModTags.Blocks.CASTLE_BLOCK).add(ModBlocks.castle_block_dark_brick, ModBlocks.castle_block_dark_brick_bloody, ModBlocks.castle_block_dark_stone, ModBlocks.castle_block_normal_brick, ModBlocks.castle_block_purple_brick);
            getOrCreateBuilder(ModTags.Blocks.CASTLE_SLAPS).add(ModBlocks.castle_slab_dark_brick, ModBlocks.castle_slab_dark_stone, ModBlocks.castle_slab_purple_brick);
            getOrCreateBuilder(ModTags.Blocks.CASTLE_STAIRS).add(ModBlocks.castle_stairs_dark_stone, ModBlocks.castle_stairs_dark_brick, ModBlocks.castle_stairs_purple_brick);
            getOrCreateBuilder(BlockTags.STAIRS).addTag(ModTags.Blocks.CASTLE_STAIRS);
            getOrCreateBuilder(BlockTags.SLABS).addTag(ModTags.Blocks.CASTLE_SLAPS);
            getOrCreateBuilder(BlockTags.FLOWER_POTS).add(ModBlocks.potted_vampire_orchid);
            getOrCreateBuilder(BlockTags.SPRUCE_LOGS).add(ModBlocks.bloody_spruce_log);
            getOrCreateBuilder(BlockTags.LEAVES).add(ModBlocks.vampire_spruce_leaves, ModBlocks.bloody_spruce_leaves);
            getOrCreateBuilder(BlockTags.SAPLINGS).add(ModBlocks.bloody_spruce_sapling);
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
        protected void registerTags() {
            copy(ModTags.Blocks.CASTLE_BLOCK, ModTags.Items.CASTLE_BLOCK);
            copy(ModTags.Blocks.CASTLE_STAIRS, ModTags.Items.CASTLE_STAIRS);
            copy(ModTags.Blocks.CASTLE_SLAPS, ModTags.Items.CASTLE_SLAPS);
            copy(ModTags.Blocks.CURSEDEARTH, ModTags.Items.CURSEDEARTH);

            getOrCreateBuilder(ModTags.Items.CROSSBOW_ARROW).add(ModItems.crossbow_arrow_normal, ModItems.crossbow_arrow_spitfire, ModItems.crossbow_arrow_vampire_killer);
            getOrCreateBuilder(ModTags.Items.HUNTER_INTEL).add(ModItems.hunter_intel_0, ModItems.hunter_intel_1, ModItems.hunter_intel_2, ModItems.hunter_intel_3, ModItems.hunter_intel_4, ModItems.hunter_intel_5, ModItems.hunter_intel_6, ModItems.hunter_intel_7, ModItems.hunter_intel_8, ModItems.hunter_intel_9);
            getOrCreateBuilder(ModTags.Items.PURE_BLOOD).add(ModItems.pure_blood_0, ModItems.pure_blood_1, ModItems.pure_blood_2, ModItems.pure_blood_3, ModItems.pure_blood_4);
            getOrCreateBuilder(ModTags.Items.VAMPIRE_CLOAK).add(ModItems.vampire_cloak_black_blue, ModItems.vampire_cloak_black_red, ModItems.vampire_cloak_black_white, ModItems.vampire_cloak_red_black, ModItems.vampire_cloak_white_black);
            getOrCreateBuilder(ItemTags.SMALL_FLOWERS).add(ModItems.vampire_orchid);
            getOrCreateBuilder(ModTags.Items.GARLIC).add(ModItems.item_garlic);
            getOrCreateBuilder(ModTags.Items.HOLY_WATER).add(ModItems.holy_water_bottle_normal, ModItems.holy_water_bottle_enhanced, ModItems.holy_water_bottle_ultimate);
            getOrCreateBuilder(ModTags.Items.HOLY_WATER_SPLASH).add(ModItems.holy_water_splash_bottle_normal, ModItems.holy_water_splash_bottle_enhanced, ModItems.holy_water_splash_bottle_ultimate);
            getOrCreateBuilder(ItemTags.STAIRS).addTag(ModTags.Items.CASTLE_STAIRS);
            getOrCreateBuilder(ItemTags.SLABS).addTag(ModTags.Items.CASTLE_SLAPS);
            getOrCreateBuilder(ItemTags.SPRUCE_LOGS).add(ModBlocks.bloody_spruce_log.asItem());
            getOrCreateBuilder(ItemTags.LEAVES).add(ModBlocks.vampire_spruce_leaves.asItem(), ModBlocks.bloody_spruce_leaves.asItem());
            getOrCreateBuilder(ItemTags.SAPLINGS).add(ModBlocks.bloody_spruce_sapling.asItem());
        }
    }

    public static class ModEntityTypeTagsProvider extends EntityTypeTagsProvider {
        public ModEntityTypeTagsProvider(DataGenerator dataGenerator, ExistingFileHelper helper) {
            super(dataGenerator, REFERENCE.MODID, helper);
        }

        @Override
        protected void registerTags() {
            getOrCreateBuilder(ModTags.Entities.HUNTER).add(ModEntities.hunter, ModEntities.hunter_imob, ModEntities.advanced_hunter, ModEntities.advanced_hunter_imob, ModEntities.hunter_trainer, ModEntities.hunter_trainer, ModEntities.hunter_trainer_dummy, ModEntities.task_master_hunter);
            getOrCreateBuilder(ModTags.Entities.VAMPIRE).add(ModEntities.vampire, ModEntities.vampire_imob, ModEntities.advanced_vampire, ModEntities.advanced_vampire_imob, ModEntities.vampire_baron, ModEntities.task_master_vampire);
            getOrCreateBuilder(ModTags.Entities.ADVANCED_HUNTER).add(ModEntities.advanced_hunter, ModEntities.advanced_hunter_imob);
            getOrCreateBuilder(ModTags.Entities.ADVANCED_VAMPIRE).add(ModEntities.advanced_vampire, ModEntities.advanced_vampire_imob);
        }
    }

    public static class ModFluidTagsProvider extends FluidTagsProvider {
        public ModFluidTagsProvider(DataGenerator generatorIn, ExistingFileHelper helper) {
            super(generatorIn, REFERENCE.MODID, helper);
        }

        @Override
        public String getName() {
            return REFERENCE.MODID + " " + super.getName();
        }

        @Override
        protected void registerTags() {
            getOrCreateBuilder(ModTags.Fluids.BLOOD).add(ModFluids.blood);
            getOrCreateBuilder(ModTags.Fluids.IMPURE_BLOOD).add(ModFluids.impure_blood);
        }
    }
}
