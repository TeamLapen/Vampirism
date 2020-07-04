package de.teamlapen.vampirism.data;

import de.teamlapen.vampirism.core.*;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.data.*;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraftforge.common.Tags;

import javax.annotation.Nonnull;

public class TagGenerator {

    public static void register(DataGenerator generator) {
        BlockTagsProvider blockTagsProvider = new ModBlockTagsProvider(generator);
        generator.addProvider(blockTagsProvider);
        generator.addProvider(new ModItemTagsProvider(generator, blockTagsProvider));
        generator.addProvider(new ModEntityTypeTagsProvider(generator));
        generator.addProvider(new ModFluidTagsProvider(generator));
    }

    public static class ModBlockTagsProvider extends BlockTagsProvider {
        public ModBlockTagsProvider(DataGenerator dataGenerator) {
            super(dataGenerator);
        }

        @Nonnull
        @Override
        public String getName() {
            return REFERENCE.MODID + " " + super.getName();
        }

        @Override
        protected void registerTags() {
            func_240522_a_(Tags.Blocks.DIRT).func_240534_a_(ModBlocks.cursed_earth);
            func_240522_a_(ModTags.Blocks.CURSEDEARTH).func_240534_a_(ModBlocks.cursed_earth);
            func_240522_a_(ModTags.Blocks.CASTLE_BLOCK).func_240534_a_(ModBlocks.castle_block_dark_brick, ModBlocks.castle_block_dark_brick_bloody, ModBlocks.castle_block_dark_stone, ModBlocks.castle_block_normal_brick, ModBlocks.castle_block_purple_brick);
            func_240522_a_(ModTags.Blocks.CASTLE_SLAPS).func_240534_a_(ModBlocks.castle_slab_dark_brick, ModBlocks.castle_slab_dark_stone, ModBlocks.castle_slab_purple_brick);
            func_240522_a_(ModTags.Blocks.CASTLE_STAIRS).func_240534_a_(ModBlocks.castle_stairs_dark_stone, ModBlocks.castle_stairs_dark_brick, ModBlocks.castle_stairs_purple_brick);
            func_240522_a_(BlockTags.STAIRS).func_240531_a_(ModTags.Blocks.CASTLE_STAIRS);
            func_240522_a_(BlockTags.SLABS).func_240531_a_(ModTags.Blocks.CASTLE_SLAPS);
            func_240522_a_(BlockTags.FLOWER_POTS).func_240534_a_(ModBlocks.potted_vampire_orchid);
        }
    }

    public static class ModItemTagsProvider extends ItemTagsProvider {
        public ModItemTagsProvider(DataGenerator dataGenerator, BlockTagsProvider blockTagsProvider) {
            super(dataGenerator, blockTagsProvider);
        }

        @Nonnull
        @Override
        public String getName() {
            return REFERENCE.MODID + " " + super.getName();
        }

        @Override
        protected void registerTags() {
            func_240521_a_(ModTags.Blocks.CASTLE_BLOCK, ModTags.Items.CASTLE_BLOCK);

            func_240522_a_(ModTags.Items.CROSSBOW_ARROW).func_240534_a_(ModItems.crossbow_arrow_normal, ModItems.crossbow_arrow_spitfire, ModItems.crossbow_arrow_vampire_killer);
            func_240522_a_(ModTags.Items.HUNTER_INTEL).func_240534_a_(ModItems.hunter_intel_0, ModItems.hunter_intel_1, ModItems.hunter_intel_2, ModItems.hunter_intel_3, ModItems.hunter_intel_4, ModItems.hunter_intel_5, ModItems.hunter_intel_6, ModItems.hunter_intel_7, ModItems.hunter_intel_8, ModItems.hunter_intel_9);
            func_240522_a_(ModTags.Items.PURE_BLOOD).func_240534_a_(ModItems.pure_blood_0, ModItems.pure_blood_1, ModItems.pure_blood_2, ModItems.pure_blood_3, ModItems.pure_blood_4);
            func_240522_a_(ModTags.Items.VAMPIRE_CLOAK).func_240534_a_(ModItems.vampire_cloak_black_blue, ModItems.vampire_cloak_black_red, ModItems.vampire_cloak_black_white, ModItems.vampire_cloak_red_black, ModItems.vampire_cloak_white_black);
            func_240522_a_(ItemTags.SMALL_FLOWERS).func_240534_a_(ModItems.vampire_orchid);
            func_240522_a_(ModTags.Items.GARLIC).func_240534_a_(ModItems.item_garlic);
            func_240522_a_(ModTags.Items.HOLY_WATER).func_240534_a_(ModItems.holy_water_bottle_normal, ModItems.holy_water_bottle_enhanced, ModItems.holy_water_bottle_ultimate);
            func_240522_a_(ModTags.Items.HOLY_WATER_SPLASH).func_240534_a_(ModItems.holy_water_splash_bottle_normal, ModItems.holy_water_splash_bottle_enhanced, ModItems.holy_water_splash_bottle_ultimate);
        }
    }

    public static class ModEntityTypeTagsProvider extends EntityTypeTagsProvider {
        public ModEntityTypeTagsProvider(DataGenerator dataGenerator) {
            super(dataGenerator);
        }

        @Override
        protected void registerTags() {
            func_240522_a_(ModTags.Entities.HUNTER).func_240534_a_(ModEntities.hunter, ModEntities.hunter_imob, ModEntities.advanced_hunter, ModEntities.advanced_hunter_imob, ModEntities.hunter_trainer);
            func_240522_a_(ModTags.Entities.VAMPIRE).func_240534_a_(ModEntities.vampire, ModEntities.vampire_imob, ModEntities.advanced_vampire, ModEntities.advanced_vampire_imob, ModEntities.vampire_baron);
        }
    }

    public static class ModFluidTagsProvider extends FluidTagsProvider {
        public ModFluidTagsProvider(DataGenerator generatorIn) {
            super(generatorIn);
        }

        @Override
        public String getName() {
            return REFERENCE.MODID + " " + super.getName();
        }

        @Override
        protected void registerTags() {
            func_240522_a_(ModTags.Fluids.BLOOD).func_240534_a_(ModFluids.blood);
            func_240522_a_(ModTags.Fluids.IMPURE_BLOOD).func_240534_a_(ModFluids.impure_blood);
        }
    }
}
