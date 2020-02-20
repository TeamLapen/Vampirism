package de.teamlapen.vampirism.core;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;

import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.util.ASMHooks;
import de.teamlapen.vampirism.util.REFERENCE;
import de.teamlapen.vampirism.util.SRGNAMES;
import de.teamlapen.vampirism.world.gen.util.RandomBlockState;
import de.teamlapen.vampirism.world.gen.util.RandomStructureProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.feature.jigsaw.JigsawManager;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraft.world.gen.feature.jigsaw.SingleJigsawPiece;
import net.minecraft.world.gen.feature.structure.DesertVillagePools;
import net.minecraft.world.gen.feature.structure.PlainsVillagePools;
import net.minecraft.world.gen.feature.structure.SavannaVillagePools;
import net.minecraft.world.gen.feature.structure.SnowyVillagePools;
import net.minecraft.world.gen.feature.structure.TaigaVillagePools;
import net.minecraft.world.gen.feature.template.AlwaysTrueRuleTest;
import net.minecraft.world.gen.feature.template.RandomBlockMatchRuleTest;
import net.minecraft.world.gen.feature.template.StructureProcessor;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.registries.ObjectHolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

        //hunter trainer
        JigsawManager.REGISTRY.register(new JigsawPattern(new ResourceLocation(REFERENCE.MODID, "village/entities/hunter_trainer"), new ResourceLocation("empty"), Lists.newArrayList(Pair.of(new SingleJigsawPiece(REFERENCE.MODID + ":village/entities/hunter_trainer"), 1)), JigsawPattern.PlacementBehaviour.RIGID));

        //hunter trainer house
        JigsawPiece trainerDesert = new SingleJigsawPiece(REFERENCE.MODID + ":village/desert/houses/hunter_trainer");
        JigsawPiece trainerPlains = new SingleJigsawPiece(REFERENCE.MODID + ":village/plains/houses/hunter_trainer");
        JigsawPiece trainerSavanna = new SingleJigsawPiece(REFERENCE.MODID + ":village/savanna/houses/hunter_trainer");
        JigsawPiece trainerSnowy = new SingleJigsawPiece(REFERENCE.MODID + ":village/snowy/houses/hunter_trainer");
        JigsawPiece trainerTaiga = new SingleJigsawPiece(REFERENCE.MODID + ":village/taiga/houses/hunter_trainer");
        JigsawPattern desertHouses = JigsawManager.REGISTRY.get(new ResourceLocation("village/desert/houses"));
        JigsawPattern plainsHouses = JigsawManager.REGISTRY.get(new ResourceLocation("village/plains/houses"));
        JigsawPattern savannaHouses = JigsawManager.REGISTRY.get(new ResourceLocation("village/savanna/houses"));
        JigsawPattern snowyHouses = JigsawManager.REGISTRY.get(new ResourceLocation("village/snowy/houses"));
        JigsawPattern taigaHouses = JigsawManager.REGISTRY.get(new ResourceLocation("village/taiga/houses"));

        for (int i = VampirismConfig.BALANCE.viHunterTrainerWeight.get(); i > 0; i--) {
            desertHouses.jigsawPieces.add(trainerDesert);
            plainsHouses.jigsawPieces.add(trainerPlains);
            savannaHouses.jigsawPieces.add(trainerSavanna);
            snowyHouses.jigsawPieces.add(trainerSnowy);
            taigaHouses.jigsawPieces.add(trainerTaiga);
        }

        //totem
        StructureProcessor totemProcessor = new RandomStructureProcessor(ImmutableList.of(new RandomBlockState(new RandomBlockMatchRuleTest(ModBlocks.totem_top, VampirismConfig.BALANCE.viTotemPreSetPercentage.get().floatValue()), AlwaysTrueRuleTest.INSTANCE, ModBlocks.totem_top_vampirism_hunter.getDefaultState(), ModBlocks.totem_top_vampirism_vampire.getDefaultState())));
        JigsawPiece totem = new SingleJigsawPiece(REFERENCE.MODID + ":village/totem", Lists.newArrayList(totemProcessor), JigsawPattern.PlacementBehaviour.RIGID);
        for (int i = VampirismConfig.BALANCE.viTotemWeight.get(); i > 0; i--) {
            desertHouses.jigsawPieces.add(totem);
            plainsHouses.jigsawPieces.add(totem);
            savannaHouses.jigsawPieces.add(totem);
            snowyHouses.jigsawPieces.add(totem);
            taigaHouses.jigsawPieces.add(totem);
        }
    }



}
