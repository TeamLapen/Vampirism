package de.teamlapen.vampirism.data;

import de.teamlapen.vampirism.blocks.AlchemicalCauldronBlock;
import de.teamlapen.vampirism.blocks.AltarPillarBlock;
import de.teamlapen.vampirism.blocks.SieveBlock;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.client.model.generators.ModelFile;


public class BlockStateGenerator extends BlockStateProvider {


    public BlockStateGenerator(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, REFERENCE.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {

        getVariantBuilder(ModBlocks.altar_pillar)
                .partialState().with(AltarPillarBlock.TYPE_PROPERTY, AltarPillarBlock.EnumPillarType.NONE).modelForState().modelFile(models().getExistingFile(modLoc("block/altar_pillar"))).addModel()
                .partialState().with(AltarPillarBlock.TYPE_PROPERTY, AltarPillarBlock.EnumPillarType.BONE).modelForState().modelFile(models().getExistingFile(modLoc("block/altar_pillar_filled_bone"))).addModel()
                .partialState().with(AltarPillarBlock.TYPE_PROPERTY, AltarPillarBlock.EnumPillarType.GOLD).modelForState().modelFile(models().getExistingFile(modLoc("block/altar_pillar_filled_gold"))).addModel()
                .partialState().with(AltarPillarBlock.TYPE_PROPERTY, AltarPillarBlock.EnumPillarType.STONE).modelForState().modelFile(models().getExistingFile(modLoc("block/altar_pillar_filled_stone_bricks"))).addModel()
                .partialState().with(AltarPillarBlock.TYPE_PROPERTY, AltarPillarBlock.EnumPillarType.IRON).modelForState().modelFile(models().getExistingFile(modLoc("block/altar_pillar_filled_iron"))).addModel();

        ModelFile cauldronLiquid = models().getExistingFile(modLoc("block/alchemy_cauldron_liquid"));
        ModelFile cauldronLiquidBoiling = models().getBuilder("cauldron_boiling").parent(cauldronLiquid).texture("liquid", modLoc("block/blank_liquid_boiling"));

        getMultipartBuilder(ModBlocks.alchemical_cauldron)
                .part().modelFile(models().getExistingFile(modLoc("block/alchemy_cauldron"))).addModel().end()
                .part().modelFile(models().getExistingFile(modLoc("block/alchemy_cauldron_fire"))).addModel().condition(AlchemicalCauldronBlock.LIT, true).end()
                .part().modelFile(cauldronLiquid).addModel().condition(AlchemicalCauldronBlock.LIQUID, 1).end()
                .part().modelFile(cauldronLiquidBoiling).addModel().condition(AlchemicalCauldronBlock.LIQUID, 2).end();

        horizontalBlock(ModBlocks.blood_grinder, models().getExistingFile(modLoc("block/grinder")));

        ModelFile sieve = models().getExistingFile(modLoc("block/blood_sieve"));
        ModelFile activeSieve = models().getBuilder("active_blood_sieve").parent(sieve).texture("filter", modLoc("block/blood_sieve_filter_active"));

        getVariantBuilder(ModBlocks.blood_sieve)
                .partialState().with(SieveBlock.PROPERTY_ACTIVE, true).modelForState().modelFile(activeSieve).addModel()
                .partialState().with(SieveBlock.PROPERTY_ACTIVE, false).modelForState().modelFile(sieve).addModel();

        horizontalBlock(ModBlocks.hunter_table, models().getExistingFile(modLoc("block/hunter_table")));
        horizontalBlock(ModBlocks.garlic_beacon_normal, models().getExistingFile(modLoc("block/garlic_beacon_normal")));
        horizontalBlock(ModBlocks.garlic_beacon_weak, models().getExistingFile(modLoc("block/garlic_beacon_weak")));
        horizontalBlock(ModBlocks.garlic_beacon_improved, models().getExistingFile(modLoc("block/garlic_beacon_improved")));

    }
}
