package de.teamlapen.vampirism.data;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.core.*;
import net.minecraft.data.*;
import net.minecraft.entity.EntityType;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nonnull;

public class TagGenerator {

    public static void register(DataGenerator generator, ExistingFileHelper helper) {
        BlockTagsProvider blockTagsProvider = new ModBlockTagsProvider(generator, helper);
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
        protected void addTags() {
            tag(Tags.Blocks.DIRT).add(ModBlocks.cursed_earth, ModBlocks.cursed_grass);
            tag(ModTags.Blocks.CURSEDEARTH).add(ModBlocks.cursed_earth, ModBlocks.cursed_grass);
            tag(ModTags.Blocks.CASTLE_BLOCK).add(ModBlocks.castle_block_dark_brick, ModBlocks.castle_block_dark_brick_bloody, ModBlocks.castle_block_dark_stone, ModBlocks.castle_block_normal_brick, ModBlocks.castle_block_purple_brick);
            tag(ModTags.Blocks.CASTLE_SLAPS).add(ModBlocks.castle_slab_dark_brick, ModBlocks.castle_slab_dark_stone, ModBlocks.castle_slab_purple_brick);
            tag(ModTags.Blocks.CASTLE_STAIRS).add(ModBlocks.castle_stairs_dark_stone, ModBlocks.castle_stairs_dark_brick, ModBlocks.castle_stairs_purple_brick);
            tag(BlockTags.STAIRS).addTag(ModTags.Blocks.CASTLE_STAIRS);
            tag(BlockTags.SLABS).addTag(ModTags.Blocks.CASTLE_SLAPS);
            tag(BlockTags.FLOWER_POTS).add(ModBlocks.potted_vampire_orchid);
            tag(ModTags.Blocks.DARK_SPRUCE_LOG).add(ModBlocks.dark_spruce_log, ModBlocks.stripped_dark_spruce_log, ModBlocks.dark_spruce_wood, ModBlocks.stripped_dark_spruce_wood);
            tag(ModTags.Blocks.CURSED_SPRUCE_LOG).add(ModBlocks.cursed_spruce_log, ModBlocks.stripped_cursed_spruce_log, ModBlocks.cursed_spruce_wood, ModBlocks.stripped_cursed_spruce_wood);
            tag(BlockTags.LEAVES).add(ModBlocks.dark_spruce_leaves);
            tag(BlockTags.SAPLINGS).add(ModBlocks.dark_spruce_sapling);
            tag(BlockTags.WOODEN_TRAPDOORS).add(ModBlocks.dark_spruce_trapdoor, ModBlocks.cursed_spruce_trapdoor);
            tag(BlockTags.WOODEN_DOORS).add(ModBlocks.dark_spruce_door, ModBlocks.cursed_spruce_door);
            tag(BlockTags.PLANKS).add(ModBlocks.dark_spruce_planks, ModBlocks.cursed_spruce_planks);
            tag(BlockTags.WOODEN_BUTTONS).add(ModBlocks.dark_spruce_button, ModBlocks.cursed_spruce_button);
            tag(BlockTags.WOODEN_STAIRS).add(ModBlocks.dark_spruce_stairs, ModBlocks.cursed_spruce_stairs);
            tag(BlockTags.WOODEN_SLABS).add(ModBlocks.dark_spruce_slab, ModBlocks.cursed_spruce_slab);
            tag(BlockTags.WOODEN_FENCES).add(ModBlocks.dark_spruce_fence, ModBlocks.cursed_spruce_fence);
            tag(BlockTags.LOGS_THAT_BURN).addTags(ModTags.Blocks.CURSED_SPRUCE_LOG, ModTags.Blocks.DARK_SPRUCE_LOG);
            tag(BlockTags.WOODEN_PRESSURE_PLATES).add(ModBlocks.dark_spruce_pressure_place, ModBlocks.cursed_spruce_pressure_place);
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
            copy(ModTags.Blocks.DARK_SPRUCE_LOG, ModTags.Items.DARK_SPRUCE_LOG);
            copy(ModTags.Blocks.CURSED_SPRUCE_LOG, ModTags.Items.CURSED_SPRUCE_LOG);

            tag(ModTags.Items.CROSSBOW_ARROW).add(ModItems.crossbow_arrow_normal, ModItems.crossbow_arrow_spitfire, ModItems.crossbow_arrow_vampire_killer);
            tag(ModTags.Items.HUNTER_INTEL).add(ModItems.hunter_intel_0, ModItems.hunter_intel_1, ModItems.hunter_intel_2, ModItems.hunter_intel_3, ModItems.hunter_intel_4, ModItems.hunter_intel_5, ModItems.hunter_intel_6, ModItems.hunter_intel_7, ModItems.hunter_intel_8, ModItems.hunter_intel_9);
            tag(ModTags.Items.PURE_BLOOD).add(ModItems.pure_blood_0, ModItems.pure_blood_1, ModItems.pure_blood_2, ModItems.pure_blood_3, ModItems.pure_blood_4);
            tag(ModTags.Items.VAMPIRE_CLOAK).add(ModItems.vampire_cloak_black_blue, ModItems.vampire_cloak_black_red, ModItems.vampire_cloak_black_white, ModItems.vampire_cloak_red_black, ModItems.vampire_cloak_white_black);
            tag(ItemTags.SMALL_FLOWERS).add(ModItems.vampire_orchid);
            tag(ModTags.Items.GARLIC).add(ModItems.item_garlic);
            tag(ModTags.Items.HOLY_WATER).add(ModItems.holy_water_bottle_normal, ModItems.holy_water_bottle_enhanced, ModItems.holy_water_bottle_ultimate);
            tag(ModTags.Items.HOLY_WATER_SPLASH).add(ModItems.holy_water_splash_bottle_normal, ModItems.holy_water_splash_bottle_enhanced, ModItems.holy_water_splash_bottle_ultimate);
            tag(ItemTags.STAIRS).addTag(ModTags.Items.CASTLE_STAIRS);
            tag(ItemTags.SLABS).addTag(ModTags.Items.CASTLE_SLAPS);
            tag(ItemTags.LEAVES).add(ModBlocks.dark_spruce_leaves.asItem());
            tag(ItemTags.SAPLINGS).add(ModBlocks.dark_spruce_sapling.asItem());
            tag(ItemTags.PLANKS).add(ModBlocks.dark_spruce_planks.asItem(), ModBlocks.cursed_spruce_planks.asItem());
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

        @Override
        public String getName() {
            return REFERENCE.MODID + " " + super.getName();
        }

        @Override
        protected void addTags() {
            tag(ModTags.Fluids.BLOOD).add(ModFluids.blood);
            tag(ModTags.Fluids.IMPURE_BLOOD).add(ModFluids.impure_blood);
        }
    }
}
