package de.teamlapen.vampirism.data;

import de.teamlapen.vampirism.core.*;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.block.Blocks;
import net.minecraft.data.*;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraftforge.common.Tags;

import javax.annotation.Nonnull;

public class TagGenerator {

    public static void register(DataGenerator generator) {
        generator.addProvider(new ModBlockTagsProvider(generator));
        generator.addProvider(new ModItemTagsProvider(generator));
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
            getBuilder(Tags.Blocks.DIRT).add(ModBlocks.cursed_earth);
            getBuilder(ModTags.Blocks.CURSEDEARTH).add(ModBlocks.cursed_earth);
            getBuilder(ModTags.Blocks.CASTLE_BLOCK).add(ModBlocks.castle_block_dark_brick, ModBlocks.castle_block_dark_brick_bloody, ModBlocks.castle_block_dark_stone, ModBlocks.castle_block_normal_brick, ModBlocks.castle_block_purple_brick);
            getBuilder(ModTags.Blocks.CASTLE_SLAPS).add(ModBlocks.castle_slab_dark_brick, ModBlocks.castle_slab_dark_stone, ModBlocks.castle_slab_purple_brick);
            getBuilder(ModTags.Blocks.CASTLE_STAIRS).add(ModBlocks.castle_stairs_dark_stone, ModBlocks.castle_stairs_dark_brick, ModBlocks.castle_stairs_purple_brick);
            getBuilder(BlockTags.STAIRS).add(ModTags.Blocks.CASTLE_STAIRS);
            getBuilder(BlockTags.SLABS).add(ModTags.Blocks.CASTLE_SLAPS);
            getBuilder(BlockTags.FLOWER_POTS).add(ModBlocks.potted_vampire_orchid);
        }
    }

    public static class ModItemTagsProvider extends ItemTagsProvider {
        public ModItemTagsProvider(DataGenerator dataGenerator) {
            super(dataGenerator);
        }

        @Nonnull
        @Override
        public String getName() {
            return REFERENCE.MODID + " " + super.getName();
        }

        @Override
        protected void registerTags() {
            copy(ModTags.Blocks.CASTLE_BLOCK, ModTags.Items.CASTLE_BLOCK);

            getBuilder(ModTags.Items.CROSSBOW_ARROW).add(ModItems.crossbow_arrow_normal, ModItems.crossbow_arrow_spitfire, ModItems.crossbow_arrow_vampire_killer);
            getBuilder(ModTags.Items.HUNTER_INTEL).add(ModItems.hunter_intel_0, ModItems.hunter_intel_1, ModItems.hunter_intel_2, ModItems.hunter_intel_3, ModItems.hunter_intel_4, ModItems.hunter_intel_5, ModItems.hunter_intel_6, ModItems.hunter_intel_7, ModItems.hunter_intel_8, ModItems.hunter_intel_9);
            getBuilder(ModTags.Items.PURE_BLOOD).add(ModItems.pure_blood_0, ModItems.pure_blood_1, ModItems.pure_blood_2, ModItems.pure_blood_3, ModItems.pure_blood_4);
            getBuilder(ModTags.Items.VAMPIRE_CLOAK).add(ModItems.vampire_cloak_black_blue, ModItems.vampire_cloak_black_red, ModItems.vampire_cloak_black_white, ModItems.vampire_cloak_red_black, ModItems.vampire_cloak_white_black);
            getBuilder(ItemTags.SMALL_FLOWERS).add(ModItems.vampire_orchid);
            getBuilder(ModTags.Items.GARLIC).add(ModItems.item_garlic);
            getBuilder(ModTags.Items.HOLY_WATER).add(ModItems.holy_water_bottle_normal,ModItems.holy_water_bottle_enhanced,ModItems.holy_water_bottle_ultimate);
            getBuilder(ModTags.Items.HOLY_WATER_SPLASH).add(ModItems.holy_water_splash_bottle_normal,ModItems.holy_water_splash_bottle_enhanced,ModItems.holy_water_splash_bottle_ultimate);
        }
    }

    public static class ModEntityTypeTagsProvider extends EntityTypeTagsProvider{
        public ModEntityTypeTagsProvider(DataGenerator dataGenerator) {
            super(dataGenerator);
        }

        @Override
        protected void registerTags() {
            getBuilder(ModTags.Entities.HUNTER).add(ModEntities.hunter, ModEntities.hunter_imob, ModEntities.advanced_hunter, ModEntities.advanced_hunter_imob, ModEntities.hunter_trainer);
            getBuilder(ModTags.Entities.VAMPIRE).add(ModEntities.vampire, ModEntities.vampire_imob, ModEntities.advanced_vampire, ModEntities.advanced_vampire_imob, ModEntities.vampire_baron);
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
            getBuilder(ModTags.Fluids.BLOOD).add(ModFluids.blood);
            getBuilder(ModTags.Fluids.IMPURE_BLOOD).add(ModFluids.impure_blood);
        }
    }
}
