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
            tag(Tags.Blocks.DIRT).add(ModBlocks.CURSED_EARTH.get(), ModBlocks.CURSED_GRASS.get());
            tag(ModTags.Blocks.CURSEDEARTH).add(ModBlocks.CURSED_EARTH.get(), ModBlocks.CURSED_GRASS.get());
            tag(ModTags.Blocks.CASTLE_BLOCK).add(ModBlocks.CASTLE_BLOCK_DARK_BRICK.get(), ModBlocks.CASTLE_BLOCK_DARK_BRICK_BLOODY.get(), ModBlocks.CASTLE_BLOCK_DARK_STONE.get(), ModBlocks.CASTLE_BLOCK_NORMAL_BRICK.get(), ModBlocks.CASTLE_BLOCK_PURPLE_BRICK.get());
            tag(ModTags.Blocks.CASTLE_SLAPS).add(ModBlocks.CASTLE_SLAB_DARK_BRICK.get(), ModBlocks.CASTLE_SLAB_DARK_STONE.get(), ModBlocks.CASTLE_SLAB_PURPLE_BRICK.get());
            tag(ModTags.Blocks.CASTLE_STAIRS).add(ModBlocks.CASTLE_STAIRS_DARK_STONE.get(), ModBlocks.CASTLE_STAIRS_DARK_BRICK.get(), ModBlocks.CASTLE_STAIRS_PURPLE_BRICK.get());
            tag(BlockTags.STAIRS).addTag(ModTags.Blocks.CASTLE_STAIRS);
            tag(BlockTags.SLABS).addTag(ModTags.Blocks.CASTLE_SLAPS);
            tag(BlockTags.FLOWER_POTS).add(ModBlocks.POTTED_VAMPIRE_ORCHID.get());
            tag(ModTags.Blocks.DARK_SPRUCE_LOG).add(ModBlocks.DARK_SPRUCE_LOG.get(), ModBlocks.STRIPPED_DARK_SPRUCE_LOG.get(), ModBlocks.DARK_SPRUCE_WOOD.get(), ModBlocks.STRIPPED_DARK_SPRUCE_WOOD.get());
            tag(ModTags.Blocks.CURSED_SPRUCE_LOG).add(ModBlocks.CURSED_SPRUCE_LOG.get(), ModBlocks.STRIPPED_CURSED_SPRUCE_LOG.get(), ModBlocks.CURSED_SPRUCE_WOOD.get(), ModBlocks.STRIPPED_CURSED_SPRUCE_WOOD.get());
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

            tag(ModTags.Items.CROSSBOW_ARROW).add(ModItems.CROSSBOW_ARROW_NORMAL.get(), ModItems.CROSSBOW_ARROW_SPITFIRE.get(), ModItems.CROSSBOW_ARROW_VAMPIRE_KILLER.get());
            tag(ModTags.Items.HUNTER_INTEL).add(ModItems.HUNTER_INTEL_0.get(), ModItems.HUNTER_INTEL_1.get(), ModItems.HUNTER_INTEL_2.get(), ModItems.HUNTER_INTEL_3.get(), ModItems.HUNTER_INTEL_4.get(), ModItems.HUNTER_INTEL_5.get(), ModItems.HUNTER_INTEL_6.get(), ModItems.HUNTER_INTEL_7.get(), ModItems.HUNTER_INTEL_8.get(), ModItems.HUNTER_INTEL_9.get());
            tag(ModTags.Items.PURE_BLOOD).add(ModItems.PURE_BLOOD_0.get(), ModItems.PURE_BLOOD_1.get(), ModItems.PURE_BLOOD_2.get(), ModItems.PURE_BLOOD_3.get(), ModItems.PURE_BLOOD_4.get());
            tag(ModTags.Items.VAMPIRE_CLOAK).add(ModItems.VAMPIRE_CLOAK_BLACK_BLUE.get(), ModItems.VAMPIRE_CLOAK_BLACK_RED.get(), ModItems.VAMPIRE_CLOAK_BLACK_WHITE.get(), ModItems.VAMPIRE_CLOAK_RED_BLACK.get(), ModItems.VAMPIRE_CLOAK_WHITE_BLACK.get());
            tag(ItemTags.SMALL_FLOWERS).add(ModItems.VAMPIRE_ORCHID.get());
            tag(ModTags.Items.GARLIC).add(ModItems.ITEM_GARLIC.get());
            tag(ModTags.Items.HOLY_WATER).add(ModItems.HOLY_WATER_BOTTLE_NORMAL.get(), ModItems.HOLY_WATER_BOTTLE_ENHANCED.get(), ModItems.HOLY_WATER_BOTTLE_ULTIMATE.get());
            tag(ModTags.Items.HOLY_WATER_SPLASH).add(ModItems.HOLY_WATER_SPLASH_BOTTLE_NORMAL.get(), ModItems.HOLY_WATER_SPLASH_BOTTLE_ENHANCED.get(), ModItems.HOLY_WATER_SPLASH_BOTTLE_ULTIMATE.get());
            tag(ItemTags.STAIRS).addTag(ModTags.Items.CASTLE_STAIRS);
            tag(ItemTags.SLABS).addTag(ModTags.Items.CASTLE_SLAPS);
            tag(ItemTags.PIGLIN_LOVED).add(ModItems.VAMPIRE_CLOTHING_CROWN.get());
            tag(ModTags.Items.HEART).add(ModItems.HUMAN_HEART.get(), ModItems.WEAK_HUMAN_HEART.get());
            tag(ItemTags.BOATS).add(ModItems.DARK_SPRUCE_BOAT.get(), ModItems.CURSED_SPRUCE_BOAT.get());
        }
    }

    public static class ModEntityTypeTagsProvider extends EntityTypeTagsProvider {
        public ModEntityTypeTagsProvider(DataGenerator dataGenerator, ExistingFileHelper helper) {
            super(dataGenerator, REFERENCE.MODID, helper);
        }

        @Override
        protected void addTags() {
            tag(ModTags.Entities.HUNTER).add(ModEntities.HUNTER.get(), ModEntities.HUNTER_IMOB.get(), ModEntities.ADVANCED_HUNTER.get(), ModEntities.ADVANCED_HUNTER_IMOB.get(), ModEntities.HUNTER_TRAINER.get(), ModEntities.HUNTER_TRAINER.get(), ModEntities.HUNTER_TRAINER_DUMMY.get(), ModEntities.TASK_MASTER_HUNTER.get());
            tag(ModTags.Entities.VAMPIRE).add(ModEntities.VAMPIRE.get(), ModEntities.VAMPIRE_IMOB.get(), ModEntities.ADVANCED_VAMPIRE.get(), ModEntities.ADVANCED_VAMPIRE_IMOB.get(), ModEntities.VAMPIRE_BARON.get(), ModEntities.TASK_MASTER_VAMPIRE.get());
            tag(ModTags.Entities.ADVANCED_HUNTER).add(ModEntities.ADVANCED_HUNTER.get(), ModEntities.ADVANCED_HUNTER_IMOB.get());
            tag(ModTags.Entities.ADVANCED_VAMPIRE).add(ModEntities.ADVANCED_VAMPIRE.get(), ModEntities.ADVANCED_VAMPIRE_IMOB.get());
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
            tag(ModTags.Fluids.BLOOD).add(ModFluids.BLOOD.get());
            tag(ModTags.Fluids.IMPURE_BLOOD).add(ModFluids.IMPURE_BLOOD.get());
        }
    }
}
