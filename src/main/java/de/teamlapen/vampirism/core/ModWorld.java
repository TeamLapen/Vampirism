package de.teamlapen.vampirism.core;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.util.ASMHooks;
import de.teamlapen.vampirism.util.REFERENCE;
import de.teamlapen.vampirism.util.SRGNAMES;
import de.teamlapen.vampirism.world.gen.util.BiomeTopBlockProcessor;
import de.teamlapen.vampirism.world.gen.util.RandomBlockState;
import de.teamlapen.vampirism.world.gen.util.RandomStructureProcessor;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.PaneBlock;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.feature.jigsaw.JigsawManager;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraft.world.gen.feature.jigsaw.SingleJigsawPiece;
import net.minecraft.world.gen.feature.structure.*;
import net.minecraft.world.gen.feature.template.*;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.registries.ObjectHolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ObjectHolder(REFERENCE.MODID)
public class ModWorld {
    private static final Logger LOGGER = LogManager.getLogger();
    public static boolean debug = false;


    static void modifyVillageSize(GenerationSettings settings) {

        if (!VampirismConfig.SERVER.villageModify.get()) {
            LOGGER.trace("Not modifying village");
            return;
        }
        try {
            ObfuscationReflectionHelper.setPrivateValue(GenerationSettings.class, settings, VampirismConfig.SERVER.villageDistance.get(), SRGNAMES.GenerationSettings_villageDistance);
        } catch (ObfuscationReflectionHelper.UnableToAccessFieldException e) {
            LOGGER.error("Could not modify field 'villageDistance' in GenerationSettings", e);
        }


        try {
            ObfuscationReflectionHelper.setPrivateValue(GenerationSettings.class, settings, VampirismConfig.SERVER.villageSeparation.get(), SRGNAMES.GenerationSettings_villageSeparation);
        } catch (ObfuscationReflectionHelper.UnableToAccessFieldException e) {
            LOGGER.error("Could not modify field for villageSeparation in GenerationSettings", e);
        }


        LOGGER.debug("Modified MapGenVillage fields.");

    }

    public static void addVillageStructures() {
        //ensure single generation of following structures
        ASMHooks.addSingleInstanceStructure(Lists.newArrayList("Single[vampirism:village/totem]", "Single[vampirism:village/desert/houses/hunter_trainer]", "Single[vampirism:village/plains/houses/hunter_trainer]", "Single[vampirism:village/snowy/houses/hunter_trainer]", "Single[vampirism:village/savanna/houses/hunter_trainer]", "Single[vampirism:village/taiga/houses/hunter_trainer]"));

        //init pools for modification
        PlainsVillagePools.init();
        SnowyVillagePools.init();
        SavannaVillagePools.init();
        DesertVillagePools.init();
        TaigaVillagePools.init();

        //hunter trainer JigsawPattern
        JigsawManager.REGISTRY.register(new JigsawPattern(new ResourceLocation(REFERENCE.MODID, "village/entities/hunter_trainer"), new ResourceLocation("empty"), Lists.newArrayList(Pair.of(singleJigsawPiece("village/entities/hunter_trainer"), 1)), JigsawPattern.PlacementBehaviour.RIGID));

        Map<String, JigsawPattern> patterns = new HashMap<String, JigsawPattern>() {{
            put("plains", JigsawManager.REGISTRY.get(new ResourceLocation("village/plains/houses")));
            put("desert", JigsawManager.REGISTRY.get(new ResourceLocation("village/desert/houses")));
            put("savanna", JigsawManager.REGISTRY.get(new ResourceLocation("village/savanna/houses")));
            put("taiga", JigsawManager.REGISTRY.get(new ResourceLocation("village/taiga/houses")));
            put("snowy", JigsawManager.REGISTRY.get(new ResourceLocation("village/snowy/houses")));
            put("plains_zombie", JigsawManager.REGISTRY.get(new ResourceLocation("village/plains/zombie/houses")));
            put("desert_zombie", JigsawManager.REGISTRY.get(new ResourceLocation("village/desert/zombie/houses")));
            put("savanna_zombie", JigsawManager.REGISTRY.get(new ResourceLocation("village/savanna/zombie/houses")));
            put("taiga_zombie", JigsawManager.REGISTRY.get(new ResourceLocation("village/taiga/zombie/houses")));
            put("snowy_zombie", JigsawManager.REGISTRY.get(new ResourceLocation("village/snowy/zombie/houses")));
        }};

        //all structureProcessors are copied from PlainsVillagePools, DesertVillagePools, TaigaVillagePools, SavannaVillagePools, SnowyVillagePools
        ImmutableList<StructureProcessor> plainsProcessor = ImmutableList.of(new RuleStructureProcessor(ImmutableList.of(new RuleEntry(new RandomBlockMatchRuleTest(Blocks.COBBLESTONE, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.MOSSY_COBBLESTONE.getDefaultState()))));
        ImmutableList<StructureProcessor> taigaProcessor = ImmutableList.of(new RuleStructureProcessor(ImmutableList.of(new RuleEntry(new RandomBlockMatchRuleTest(Blocks.COBBLESTONE, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.MOSSY_COBBLESTONE.getDefaultState()))));
        ImmutableList<StructureProcessor> plainsZombieProcessor = ImmutableList.of(new RuleStructureProcessor(ImmutableList.of(new RuleEntry(new RandomBlockMatchRuleTest(Blocks.COBBLESTONE, 0.8F), AlwaysTrueRuleTest.INSTANCE, Blocks.MOSSY_COBBLESTONE.getDefaultState()), new RuleEntry(new TagMatchRuleTest(BlockTags.DOORS), AlwaysTrueRuleTest.INSTANCE, Blocks.AIR.getDefaultState()), new RuleEntry(new BlockMatchRuleTest(Blocks.TORCH), AlwaysTrueRuleTest.INSTANCE, Blocks.AIR.getDefaultState()), new RuleEntry(new BlockMatchRuleTest(Blocks.WALL_TORCH), AlwaysTrueRuleTest.INSTANCE, Blocks.AIR.getDefaultState()), new RuleEntry(new RandomBlockMatchRuleTest(Blocks.COBBLESTONE, 0.07F), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.getDefaultState()), new RuleEntry(new RandomBlockMatchRuleTest(Blocks.MOSSY_COBBLESTONE, 0.07F), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.getDefaultState()), new RuleEntry(new RandomBlockMatchRuleTest(Blocks.WHITE_TERRACOTTA, 0.07F), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.getDefaultState()), new RuleEntry(new RandomBlockMatchRuleTest(Blocks.OAK_LOG, 0.05F), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.getDefaultState()), new RuleEntry(new RandomBlockMatchRuleTest(Blocks.OAK_PLANKS, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.getDefaultState()), new RuleEntry(new RandomBlockMatchRuleTest(Blocks.OAK_STAIRS, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.getDefaultState()), new RuleEntry(new RandomBlockMatchRuleTest(Blocks.STRIPPED_OAK_LOG, 0.02F), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.getDefaultState()), new RuleEntry(new RandomBlockMatchRuleTest(Blocks.GLASS_PANE, 0.5F), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.getDefaultState()), new RuleEntry(new BlockStateMatchRuleTest(Blocks.GLASS_PANE.getDefaultState().with(PaneBlock.NORTH, true).with(PaneBlock.SOUTH, true)), AlwaysTrueRuleTest.INSTANCE, Blocks.BROWN_STAINED_GLASS_PANE.getDefaultState().with(PaneBlock.NORTH, true).with(PaneBlock.SOUTH, true)), new RuleEntry(new BlockStateMatchRuleTest(Blocks.GLASS_PANE.getDefaultState().with(PaneBlock.EAST, true).with(PaneBlock.WEST, true)), AlwaysTrueRuleTest.INSTANCE, Blocks.BROWN_STAINED_GLASS_PANE.getDefaultState().with(PaneBlock.EAST, true).with(PaneBlock.WEST, true)), new RuleEntry(new RandomBlockMatchRuleTest(Blocks.WHEAT, 0.3F), AlwaysTrueRuleTest.INSTANCE, Blocks.CARROTS.getDefaultState()), new RuleEntry(new RandomBlockMatchRuleTest(Blocks.WHEAT, 0.2F), AlwaysTrueRuleTest.INSTANCE, Blocks.POTATOES.getDefaultState()), new RuleEntry(new RandomBlockMatchRuleTest(Blocks.WHEAT, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.BEETROOTS.getDefaultState()))));
        ImmutableList<StructureProcessor> desertZombieProcessor = ImmutableList.of(new RuleStructureProcessor(ImmutableList.of(new RuleEntry(new TagMatchRuleTest(BlockTags.DOORS), AlwaysTrueRuleTest.INSTANCE, Blocks.AIR.getDefaultState()), new RuleEntry(new BlockMatchRuleTest(Blocks.TORCH), AlwaysTrueRuleTest.INSTANCE, Blocks.AIR.getDefaultState()), new RuleEntry(new BlockMatchRuleTest(Blocks.WALL_TORCH), AlwaysTrueRuleTest.INSTANCE, Blocks.AIR.getDefaultState()), new RuleEntry(new RandomBlockMatchRuleTest(Blocks.SMOOTH_SANDSTONE, 0.08F), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.getDefaultState()), new RuleEntry(new RandomBlockMatchRuleTest(Blocks.CUT_SANDSTONE, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.getDefaultState()), new RuleEntry(new RandomBlockMatchRuleTest(Blocks.TERRACOTTA, 0.08F), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.getDefaultState()), new RuleEntry(new RandomBlockMatchRuleTest(Blocks.SMOOTH_SANDSTONE_STAIRS, 0.08F), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.getDefaultState()), new RuleEntry(new RandomBlockMatchRuleTest(Blocks.SMOOTH_SANDSTONE_SLAB, 0.08F), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.getDefaultState()), new RuleEntry(new RandomBlockMatchRuleTest(Blocks.WHEAT, 0.2F), AlwaysTrueRuleTest.INSTANCE, Blocks.BEETROOTS.getDefaultState()), new RuleEntry(new RandomBlockMatchRuleTest(Blocks.WHEAT, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.MELON_STEM.getDefaultState()))));
        ImmutableList<StructureProcessor> savannaZombieProcessor = ImmutableList.of(new RuleStructureProcessor(ImmutableList.of(new RuleEntry(new TagMatchRuleTest(BlockTags.DOORS), AlwaysTrueRuleTest.INSTANCE, Blocks.AIR.getDefaultState()), new RuleEntry(new BlockMatchRuleTest(Blocks.TORCH), AlwaysTrueRuleTest.INSTANCE, Blocks.AIR.getDefaultState()), new RuleEntry(new BlockMatchRuleTest(Blocks.WALL_TORCH), AlwaysTrueRuleTest.INSTANCE, Blocks.AIR.getDefaultState()), new RuleEntry(new RandomBlockMatchRuleTest(Blocks.ACACIA_PLANKS, 0.2F), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.getDefaultState()), new RuleEntry(new RandomBlockMatchRuleTest(Blocks.ACACIA_STAIRS, 0.2F), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.getDefaultState()), new RuleEntry(new RandomBlockMatchRuleTest(Blocks.ACACIA_LOG, 0.05F), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.getDefaultState()), new RuleEntry(new RandomBlockMatchRuleTest(Blocks.ACACIA_WOOD, 0.05F), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.getDefaultState()), new RuleEntry(new RandomBlockMatchRuleTest(Blocks.ORANGE_TERRACOTTA, 0.05F), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.getDefaultState()), new RuleEntry(new RandomBlockMatchRuleTest(Blocks.YELLOW_TERRACOTTA, 0.05F), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.getDefaultState()), new RuleEntry(new RandomBlockMatchRuleTest(Blocks.RED_TERRACOTTA, 0.05F), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.getDefaultState()), new RuleEntry(new RandomBlockMatchRuleTest(Blocks.GLASS_PANE, 0.5F), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.getDefaultState()), new RuleEntry(new BlockStateMatchRuleTest(Blocks.GLASS_PANE.getDefaultState().with(PaneBlock.NORTH, true).with(PaneBlock.SOUTH, true)), AlwaysTrueRuleTest.INSTANCE, Blocks.BROWN_STAINED_GLASS_PANE.getDefaultState().with(PaneBlock.NORTH, true).with(PaneBlock.SOUTH, true)), new RuleEntry(new BlockStateMatchRuleTest(Blocks.GLASS_PANE.getDefaultState().with(PaneBlock.EAST, true).with(PaneBlock.WEST, true)), AlwaysTrueRuleTest.INSTANCE, Blocks.BROWN_STAINED_GLASS_PANE.getDefaultState().with(PaneBlock.EAST, true).with(PaneBlock.WEST, true)), new RuleEntry(new RandomBlockMatchRuleTest(Blocks.WHEAT, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.MELON_STEM.getDefaultState()))));
        ImmutableList<StructureProcessor> taigaZombieProcessor = ImmutableList.of(new RuleStructureProcessor(ImmutableList.of(new RuleEntry(new RandomBlockMatchRuleTest(Blocks.COBBLESTONE, 0.8F), AlwaysTrueRuleTest.INSTANCE, Blocks.MOSSY_COBBLESTONE.getDefaultState()), new RuleEntry(new TagMatchRuleTest(BlockTags.DOORS), AlwaysTrueRuleTest.INSTANCE, Blocks.AIR.getDefaultState()), new RuleEntry(new BlockMatchRuleTest(Blocks.TORCH), AlwaysTrueRuleTest.INSTANCE, Blocks.AIR.getDefaultState()), new RuleEntry(new BlockMatchRuleTest(Blocks.WALL_TORCH), AlwaysTrueRuleTest.INSTANCE, Blocks.AIR.getDefaultState()), new RuleEntry(new BlockMatchRuleTest(Blocks.CAMPFIRE), AlwaysTrueRuleTest.INSTANCE, Blocks.CAMPFIRE.getDefaultState().with(CampfireBlock.LIT, false)), new RuleEntry(new RandomBlockMatchRuleTest(Blocks.COBBLESTONE, 0.08F), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.getDefaultState()), new RuleEntry(new RandomBlockMatchRuleTest(Blocks.SPRUCE_LOG, 0.08F), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.getDefaultState()), new RuleEntry(new RandomBlockMatchRuleTest(Blocks.GLASS_PANE, 0.5F), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.getDefaultState()), new RuleEntry(new BlockStateMatchRuleTest(Blocks.GLASS_PANE.getDefaultState().with(PaneBlock.NORTH, true).with(PaneBlock.SOUTH, true)), AlwaysTrueRuleTest.INSTANCE, Blocks.BROWN_STAINED_GLASS_PANE.getDefaultState().with(PaneBlock.NORTH, true).with(PaneBlock.SOUTH, true)), new RuleEntry(new BlockStateMatchRuleTest(Blocks.GLASS_PANE.getDefaultState().with(PaneBlock.EAST, true).with(PaneBlock.WEST, true)), AlwaysTrueRuleTest.INSTANCE, Blocks.BROWN_STAINED_GLASS_PANE.getDefaultState().with(PaneBlock.EAST, true).with(PaneBlock.WEST, true)), new RuleEntry(new RandomBlockMatchRuleTest(Blocks.WHEAT, 0.3F), AlwaysTrueRuleTest.INSTANCE, Blocks.PUMPKIN_STEM.getDefaultState()), new RuleEntry(new RandomBlockMatchRuleTest(Blocks.WHEAT, 0.2F), AlwaysTrueRuleTest.INSTANCE, Blocks.POTATOES.getDefaultState()))));
        ImmutableList<StructureProcessor> snowyZombieProcessor = ImmutableList.of(new RuleStructureProcessor(ImmutableList.of(new RuleEntry(new TagMatchRuleTest(BlockTags.DOORS), AlwaysTrueRuleTest.INSTANCE, Blocks.AIR.getDefaultState()), new RuleEntry(new BlockMatchRuleTest(Blocks.TORCH), AlwaysTrueRuleTest.INSTANCE, Blocks.AIR.getDefaultState()), new RuleEntry(new BlockMatchRuleTest(Blocks.WALL_TORCH), AlwaysTrueRuleTest.INSTANCE, Blocks.AIR.getDefaultState()), new RuleEntry(new BlockMatchRuleTest(Blocks.LANTERN), AlwaysTrueRuleTest.INSTANCE, Blocks.AIR.getDefaultState()), new RuleEntry(new RandomBlockMatchRuleTest(Blocks.SPRUCE_PLANKS, 0.2F), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.getDefaultState()), new RuleEntry(new RandomBlockMatchRuleTest(Blocks.SPRUCE_SLAB, 0.4F), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.getDefaultState()), new RuleEntry(new RandomBlockMatchRuleTest(Blocks.STRIPPED_SPRUCE_LOG, 0.05F), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.getDefaultState()), new RuleEntry(new RandomBlockMatchRuleTest(Blocks.STRIPPED_SPRUCE_WOOD, 0.05F), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.getDefaultState()), new RuleEntry(new RandomBlockMatchRuleTest(Blocks.GLASS_PANE, 0.5F), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.getDefaultState()), new RuleEntry(new BlockStateMatchRuleTest(Blocks.GLASS_PANE.getDefaultState().with(PaneBlock.NORTH, true).with(PaneBlock.SOUTH, true)), AlwaysTrueRuleTest.INSTANCE, Blocks.BROWN_STAINED_GLASS_PANE.getDefaultState().with(PaneBlock.NORTH, true).with(PaneBlock.SOUTH, true)), new RuleEntry(new BlockStateMatchRuleTest(Blocks.GLASS_PANE.getDefaultState().with(PaneBlock.EAST, true).with(PaneBlock.WEST, true)), AlwaysTrueRuleTest.INSTANCE, Blocks.BROWN_STAINED_GLASS_PANE.getDefaultState().with(PaneBlock.EAST, true).with(PaneBlock.WEST, true)), new RuleEntry(new RandomBlockMatchRuleTest(Blocks.WHEAT, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.CARROTS.getDefaultState()), new RuleEntry(new RandomBlockMatchRuleTest(Blocks.WHEAT, 0.8F), AlwaysTrueRuleTest.INSTANCE, Blocks.POTATOES.getDefaultState()))));

        //biome string -> JigsawPattern of the biome
        //toString() of the replaced JigsawPiece -> modified JigsawPiece
        Map<String, Map<String, JigsawPiece>> temples = new HashMap<String, Map<String, JigsawPiece>>() {{
            put("plains", ImmutableMap.of("Single[minecraft:village/plains/houses/plains_temple_3]", singleJigsawPiece("village/plains/houses/plains_temple_3", plainsProcessor), "Single[minecraft:village/plains/houses/plains_temple_4]", singleJigsawPiece("village/plains/houses/plains_temple_4", plainsProcessor)));
            put("desert", ImmutableMap.of("Single[minecraft:village/desert/houses/desert_temple_1]", singleJigsawPiece("village/desert/houses/desert_temple_1"), "Single[minecraft:village/desert/houses/desert_temple_2]", singleJigsawPiece("village/desert/houses/desert_temple_2")));
            put("savanna", ImmutableMap.of("Single[minecraft:village/savanna/houses/savanna_temple_1]", singleJigsawPiece("village/savanna/houses/savanna_temple_1"), "Single[minecraft:village/savanna/houses/savanna_temple_2]", singleJigsawPiece("village/savanna/houses/savanna_temple_2")));
            put("taiga", ImmutableMap.of("Single[minecraft:village/taiga/houses/taiga_temple_1]", singleJigsawPiece("village/taiga/houses/taiga_temple_1", taigaProcessor)));
            put("snowy", ImmutableMap.of("Single[minecraft:village/snowy/houses/snowy_temple_1]", singleJigsawPiece("village/snowy/houses/snowy_temple_1")));
            put("plains_zombie", ImmutableMap.of("Single[minecraft:village/plains/houses/plains_temple_3]", singleJigsawPiece("village/plains/houses/plains_temple_3", plainsZombieProcessor), "Single[minecraft:village/plains/houses/plains_temple_4]", singleJigsawPiece("village/plains/houses/plains_temple_4", plainsZombieProcessor)));
            put("desert_zombie", ImmutableMap.of("Single[minecraft:village/desert/houses/desert_temple_1]", singleJigsawPiece("village/desert/houses/desert_temple_1", desertZombieProcessor), "Single[minecraft:village/desert/houses/desert_temple_2]", singleJigsawPiece("village/desert/houses/desert_temple_2", desertZombieProcessor)));
            put("savanna_zombie", ImmutableMap.of("Single[minecraft:village/savanna/houses/savanna_temple_1]", singleJigsawPiece("village/savanna/houses/savanna_temple_1", savannaZombieProcessor), "Single[minecraft:village/savanna/houses/savanna_temple_2]", singleJigsawPiece("village/savanna/houses/savanna_temple_2", savannaZombieProcessor)));
            put("taiga_zombie", ImmutableMap.of("Single[minecraft:village/taiga/zombie/houses/taiga_temple_1]", singleJigsawPiece("village/taiga/houses/taiga_temple_1", taigaZombieProcessor)));
            put("snowy_zombie", ImmutableMap.of("Single[minecraft:village/snowy/houses/snowy_temple_1]", singleJigsawPiece("village/snowy/houses/snowy_temple_1", snowyZombieProcessor)));
        }};
        //biome string -> list of all JigsawPieces with weight
        Map<String, List<Pair<JigsawPiece, Integer>>> buildings = Maps.newHashMapWithExpectedSize(patterns.size());
        //fill buildings with modifiable lists from the JigsawPattern's
        patterns.forEach((biome, pattern) -> buildings.put(biome, Lists.newArrayList(pattern.field_214952_d)));

        //biome string -> pair of new JigsawPiece & old JigsawPiece with weight
        Map<String, List<Pair<JigsawPiece, Pair<JigsawPiece, Integer>>>> allPieces = Maps.newHashMap();

        //saves replaceable JigsawPieces with weight to map with Replace
        temples.forEach((biome, replacer) -> buildings.get(biome).removeIf(house -> {
            if (replacer.containsKey(house.getFirst().toString())) {
                allPieces.computeIfAbsent(biome, key -> Lists.newArrayList()).add(Pair.of(replacer.get(house.getFirst().toString()), house));
                return true;
            }
            return false;
        }));

        allPieces.forEach((string, sd) -> sd.forEach(pair -> LOGGER.info(string + " " + pair.getFirst().toString())));

        //add old and new JigsawPieces with half weight to the JigsawPattern lists
        //if NullPointerException a ResourceLocation of an JigsawPiece could not be found
        temples.forEach((biome, replacer) -> replacer.values().forEach(piece -> allPieces.get(biome).forEach(pair -> {
            if (pair.getFirst() == piece) {
                int weight = Math.max((int) (pair.getSecond().getSecond() * 0.6), 1);
                buildings.get(biome).add(Pair.of(piece, weight));
                buildings.get(biome).add(Pair.of(pair.getSecond().getFirst(), weight));
            }
        })));

        //add hunter trainer house to all village JigsawPattern lists with weight
        buildings.get("desert").add(Pair.of(singleJigsawPiece("village/desert/houses/hunter_trainer"), VampirismConfig.BALANCE.viHunterTrainerWeight.get()));
        buildings.get("plains").add(Pair.of(singleJigsawPiece("village/plains/houses/hunter_trainer"), VampirismConfig.BALANCE.viHunterTrainerWeight.get()));
        buildings.get("savanna").add(Pair.of(singleJigsawPiece("village/savanna/houses/hunter_trainer"), VampirismConfig.BALANCE.viHunterTrainerWeight.get()));
        buildings.get("snowy").add(Pair.of(singleJigsawPiece("village/snowy/houses/hunter_trainer"), VampirismConfig.BALANCE.viHunterTrainerWeight.get()));
        buildings.get("taiga").add(Pair.of(singleJigsawPiece("village/taiga/houses/hunter_trainer"), VampirismConfig.BALANCE.viHunterTrainerWeight.get()));
        buildings.get("desert_zombie").add(Pair.of(singleJigsawPiece("village/desert/houses/hunter_trainer", desertZombieProcessor), VampirismConfig.BALANCE.viHunterTrainerWeight.get()));
        buildings.get("plains_zombie").add(Pair.of(singleJigsawPiece("village/plains/houses/hunter_trainer", plainsZombieProcessor), VampirismConfig.BALANCE.viHunterTrainerWeight.get()));
        buildings.get("savanna_zombie").add(Pair.of(singleJigsawPiece("village/savanna/houses/hunter_trainer", savannaZombieProcessor), VampirismConfig.BALANCE.viHunterTrainerWeight.get()));
        buildings.get("snowy_zombie").add(Pair.of(singleJigsawPiece("village/snowy/houses/hunter_trainer", snowyZombieProcessor), VampirismConfig.BALANCE.viHunterTrainerWeight.get()));
        buildings.get("taiga_zombie").add(Pair.of(singleJigsawPiece("village/taiga/houses/hunter_trainer", taigaZombieProcessor), VampirismConfig.BALANCE.viHunterTrainerWeight.get()));
        //totem JigsawPiece
        StructureProcessor totemProcessor = new RandomStructureProcessor(ImmutableList.of(new RandomBlockState(new RandomBlockMatchRuleTest(ModBlocks.totem_top, VampirismConfig.BALANCE.viTotemPreSetPercentage.get().floatValue()), AlwaysTrueRuleTest.INSTANCE, ModBlocks.totem_top_vampirism_hunter.getDefaultState(), ModBlocks.totem_top_vampirism_vampire.getDefaultState())));
        StructureProcessor totemTopBlock = new BiomeTopBlockProcessor(Blocks.BRICK_WALL.getDefaultState());
        JigsawPiece totem = singleJigsawPiece("village/totem", Lists.newArrayList(totemProcessor, totemTopBlock), JigsawPattern.PlacementBehaviour.RIGID);
        //add totem to all village JigsawPattern lists
        buildings.values().forEach(list -> list.add(Pair.of(totem, VampirismConfig.BALANCE.viTotemWeight.get())));

        //write all Lists back to the specific JigsawPattern
        buildings.forEach((biome, list) -> patterns.get(biome).field_214952_d = ImmutableList.copyOf(list));

        //sync all JigsawPattern JigsawPattern#field_214952_d (pairs piece with weight) with JigsawPattern#jigsawPieces (list of pieces * weights)
        patterns.values().forEach(pattern -> {
            pattern.jigsawPieces.clear();
            pattern.field_214952_d.forEach(pair -> {
                for (int i = 0; i < pair.getSecond(); i++) {
                    pattern.jigsawPieces.add(pair.getFirst());
                }
            });
        });

    }

    private static SingleJigsawPiece singleJigsawPiece(@Nonnull String path) {
        return singleJigsawPiece(path, ImmutableList.of());
    }

    private static SingleJigsawPiece singleJigsawPiece(@Nonnull String path, @Nonnull List<StructureProcessor> processors) {
        return new SingleJigsawPiece(REFERENCE.MODID + ":" + path, processors, JigsawPattern.PlacementBehaviour.RIGID);
    }

    private static SingleJigsawPiece singleJigsawPiece(@Nonnull String path, @Nonnull List<StructureProcessor> processors, @Nonnull JigsawPattern.PlacementBehaviour placement) {
        return new SingleJigsawPiece(REFERENCE.MODID + ":" + path, processors, placement);
    }

}
