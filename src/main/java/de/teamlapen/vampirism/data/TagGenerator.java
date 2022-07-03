package de.teamlapen.vampirism.data;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.core.*;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.*;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.level.biome.Biomes;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class TagGenerator {

    public static void register(GatherDataEvent event, DataGenerator generator) {
        BlockTagsProvider blockTagsProvider = new ModBlockTagsProvider(generator, event.getExistingFileHelper());
        generator.addProvider(event.includeServer(), blockTagsProvider);
        generator.addProvider(event.includeServer(), new ModItemTagsProvider(generator, blockTagsProvider, event.getExistingFileHelper()));
        generator.addProvider(event.includeServer(), new ModEntityTypeTagsProvider(generator, event.getExistingFileHelper()));
        generator.addProvider(event.includeServer(), new ModFluidTagsProvider(generator, event.getExistingFileHelper()));
        generator.addProvider(event.includeServer(), new ModBiomeTagsProvider(generator, event.getExistingFileHelper()));
        generator.addProvider(event.includeServer(), new ModPoiTypeProvider(generator, event.getExistingFileHelper()));
        generator.addProvider(event.includeServer(), new ModVillageProfessionProvider(generator, event.getExistingFileHelper()));
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
            tag(BlockTags.DIRT).add(ModBlocks.CURSED_EARTH.get(), ModBlocks.CURSED_GRASS_BLOCK.get());
            tag(ModTags.Blocks.CURSEDEARTH).add(ModBlocks.CURSED_EARTH.get(), ModBlocks.CURSED_GRASS_BLOCK.get());
            tag(ModTags.Blocks.CASTLE_BLOCK).add(ModBlocks.CASTLE_BLOCK_DARK_BRICK.get(), ModBlocks.CASTLE_BLOCK_DARK_BRICK_BLOODY.get(), ModBlocks.CASTLE_BLOCK_DARK_STONE.get(), ModBlocks.CASTLE_BLOCK_NORMAL_BRICK.get(), ModBlocks.CASTLE_BLOCK_PURPLE_BRICK.get());
            tag(ModTags.Blocks.CASTLE_SLAPS).add(ModBlocks.CASTLE_SLAB_DARK_BRICK.get(), ModBlocks.CASTLE_SLAB_DARK_STONE.get(), ModBlocks.CASTLE_SLAB_PURPLE_BRICK.get());
            tag(ModTags.Blocks.CASTLE_STAIRS).add(ModBlocks.CASTLE_STAIRS_DARK_STONE.get(), ModBlocks.CASTLE_STAIRS_DARK_BRICK.get(), ModBlocks.CASTLE_STAIRS_PURPLE_BRICK.get());
            tag(BlockTags.STAIRS).addTag(ModTags.Blocks.CASTLE_STAIRS);
            tag(BlockTags.SLABS).addTag(ModTags.Blocks.CASTLE_SLAPS);
            tag(BlockTags.FLOWER_POTS).add(ModBlocks.POTTED_VAMPIRE_ORCHID.get());
            tag(BlockTags.SPRUCE_LOGS).add(ModBlocks.BLOODY_SPRUCE_LOG.get());
            tag(BlockTags.LEAVES).add(ModBlocks.VAMPIRE_SPRUCE_LEAVES.get(), ModBlocks.BLOODY_SPRUCE_LEAVES.get());
            tag(BlockTags.SAPLINGS).add(ModBlocks.BLOODY_SPRUCE_SAPLING.get(), ModBlocks.VAMPIRE_SPRUCE_SAPLING.get());

            //Tool types
            tag(BlockTags.MINEABLE_WITH_SHOVEL).add(ModBlocks.CURSED_EARTH.get(), ModBlocks.CURSED_GRASS_BLOCK.get());
            //noinspection unchecked
            tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.ALTAR_INSPIRATION.get(), ModBlocks.ALTAR_PILLAR.get(), ModBlocks.ALTAR_TIP.get(), ModBlocks.BLOOD_PEDESTAL.get(), ModBlocks.ALTAR_INFUSION.get(), ModBlocks.GRAVE_CAGE.get(), ModBlocks.TOMBSTONE1.get(), ModBlocks.TOMBSTONE2.get(), ModBlocks.TOMBSTONE3.get()).addTags(ModTags.Blocks.CASTLE_BLOCK, ModTags.Blocks.CASTLE_SLAPS, ModTags.Blocks.CASTLE_STAIRS);

            tag(BlockTags.NEEDS_STONE_TOOL).add(ModBlocks.ALTAR_INSPIRATION.get(), ModBlocks.ALTAR_TIP.get());
            tag(BlockTags.NEEDS_IRON_TOOL).add(ModBlocks.BLOOD_PEDESTAL.get(), ModBlocks.ALTAR_INFUSION.get());
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

            tag(ModTags.Items.CROSSBOW_ARROW).add(ModItems.CROSSBOW_ARROW_NORMAL.get(), ModItems.CROSSBOW_ARROW_SPITFIRE.get(), ModItems.CROSSBOW_ARROW_VAMPIRE_KILLER.get());
            tag(ModTags.Items.HUNTER_INTEL).add(ModItems.HUNTER_INTEL_0.get(), ModItems.HUNTER_INTEL_1.get(), ModItems.HUNTER_INTEL_2.get(), ModItems.HUNTER_INTEL_3.get(), ModItems.HUNTER_INTEL_4.get(), ModItems.HUNTER_INTEL_5.get(), ModItems.HUNTER_INTEL_6.get(), ModItems.HUNTER_INTEL_7.get(), ModItems.HUNTER_INTEL_8.get(), ModItems.HUNTER_INTEL_9.get());
            tag(ModTags.Items.PURE_BLOOD).add(ModItems.PURE_BLOOD_0.get(), ModItems.PURE_BLOOD_1.get(), ModItems.PURE_BLOOD_2.get(), ModItems.PURE_BLOOD_3.get(), ModItems.PURE_BLOOD_4.get());
            tag(ModTags.Items.VAMPIRE_CLOAK).add(ModItems.VAMPIRE_CLOAK_BLACK_BLUE.get(), ModItems.VAMPIRE_CLOAK_BLACK_RED.get(), ModItems.VAMPIRE_CLOAK_BLACK_WHITE.get(), ModItems.VAMPIRE_CLOAK_RED_BLACK.get(), ModItems.VAMPIRE_CLOAK_WHITE_BLACK.get());
            tag(ItemTags.SMALL_FLOWERS).add(ModBlocks.VAMPIRE_ORCHID.get().asItem());
            tag(ModTags.Items.GARLIC).add(ModItems.ITEM_GARLIC.get());
            tag(ModTags.Items.HOLY_WATER).add(ModItems.HOLY_WATER_BOTTLE_NORMAL.get(), ModItems.HOLY_WATER_BOTTLE_ENHANCED.get(), ModItems.HOLY_WATER_BOTTLE_ULTIMATE.get());
            tag(ModTags.Items.HOLY_WATER_SPLASH).add(ModItems.HOLY_WATER_SPLASH_BOTTLE_NORMAL.get(), ModItems.HOLY_WATER_SPLASH_BOTTLE_ENHANCED.get(), ModItems.HOLY_WATER_SPLASH_BOTTLE_ULTIMATE.get());
            tag(ItemTags.STAIRS).addTag(ModTags.Items.CASTLE_STAIRS);
            tag(ItemTags.SLABS).addTag(ModTags.Items.CASTLE_SLAPS);
            tag(ItemTags.SPRUCE_LOGS).add(ModBlocks.BLOODY_SPRUCE_LOG.get().asItem());
            tag(ItemTags.LEAVES).add(ModBlocks.VAMPIRE_SPRUCE_LEAVES.get().asItem(), ModBlocks.BLOODY_SPRUCE_LEAVES.get().asItem());
            tag(ItemTags.SAPLINGS).add(ModBlocks.BLOODY_SPRUCE_SAPLING.get().asItem(), ModBlocks.VAMPIRE_SPRUCE_SAPLING.get().asItem());
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

        @Nonnull
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

    public static class ModBiomeTagsProvider extends BiomeTagsProvider {

        public ModBiomeTagsProvider(DataGenerator generator, @Nullable ExistingFileHelper existingFileHelper) {
            super(generator, REFERENCE.MODID, existingFileHelper);
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void addTags() {
            tag(ModTags.Biomes.HAS_HUNTER_TENT).addTags(BiomeTags.IS_BADLANDS, BiomeTags.IS_FOREST, BiomeTags.IS_TAIGA).add(Biomes.PLAINS, Biomes.DESERT, Biomes.MEADOW, Biomes.SNOWY_PLAINS, Biomes.SPARSE_JUNGLE);
            tag(ModTags.Biomes.IS_FACTION_BIOME).addTags(ModTags.Biomes.IS_VAMPIRE_BIOME);
            tag(ModTags.Biomes.IS_VAMPIRE_BIOME).add(ModBiomes.VAMPIRE_FOREST.getKey());
            tag(BiomeTags.IS_FOREST).add(ModBiomes.VAMPIRE_FOREST.getKey());
            tag(Tags.Biomes.IS_OVERWORLD).add(ModBiomes.VAMPIRE_FOREST.getKey());
            tag(Tags.Biomes.IS_DENSE).add(ModBiomes.VAMPIRE_FOREST.getKey());
            tag(Tags.Biomes.IS_MAGICAL).add(ModBiomes.VAMPIRE_FOREST.getKey());
            tag(Tags.Biomes.IS_SPOOKY).add(ModBiomes.VAMPIRE_FOREST.getKey());
            tag(ModTags.Biomes.HAS_VAMPIRE_DUNGEON).addTags(BiomeTags.IS_OVERWORLD).remove(BiomeTags.IS_OCEAN); //TODO check
            tag(ModTags.Biomes.HAS_VAMPIRE_SPAWN).addTags(BiomeTags.IS_OVERWORLD); //TODO check
            tag(ModTags.Biomes.HAS_ADVANCED_VAMPIRE_SPAWN).addTags(BiomeTags.IS_OVERWORLD); //TODO check
            tag(ModTags.Biomes.HAS_HUNTER_SPAWN).addTags(BiomeTags.IS_OVERWORLD); //TODO check
            tag(ModTags.Biomes.HAS_ADVANCED_HUNTER_SPAWN).addTags(BiomeTags.IS_OVERWORLD); //TODO check
        }
    }

    public static class ModPoiTypeProvider extends PoiTypeTagsProvider {

        public ModPoiTypeProvider(DataGenerator p_236434_, @Nullable ExistingFileHelper existingFileHelper) {
            super(p_236434_, REFERENCE.MODID, existingFileHelper);
        }

        @Override
        protected void addTags() {
            tag(ModTags.POI_TYPES.HAS_FACTION).add(ModVillage.NO_FACTION.get(), ModVillage.HUNTER_FACTION.get(), ModVillage.VAMPIRE_FACTION.get());
            tag(ModTags.POI_TYPES.IS_HUNTER).add(ModVillage.HUNTER_FACTION.get());
            tag(ModTags.POI_TYPES.IS_VAMPIRE).add(ModVillage.VAMPIRE_FACTION.get());
        }
    }

    public static class ModVillageProfessionProvider extends TagsProvider<VillagerProfession> {

        public ModVillageProfessionProvider(DataGenerator p_236434_, @Nullable ExistingFileHelper existingFileHelper) {
            super(p_236434_, Registry.VILLAGER_PROFESSION, REFERENCE.MODID, existingFileHelper);
        }

        @Override
        protected void addTags() {
            tag(ModTags.Professions.HAS_FACTION).add(ModVillage.HUNTER_EXPERT.get(), ModVillage.VAMPIRE_EXPERT.get());
            tag(ModTags.Professions.IS_VAMPIRE).add(ModVillage.VAMPIRE_EXPERT.get());
            tag(ModTags.Professions.IS_HUNTER).add(ModVillage.HUNTER_EXPERT.get());
        }
    }
}
