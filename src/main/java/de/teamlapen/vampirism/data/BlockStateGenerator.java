package de.teamlapen.vampirism.data;

import de.teamlapen.vampirism.blocks.AltarPillarBlock;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.client.model.generators.VariantBlockStateBuilder;


public class BlockStateGenerator extends BlockStateProvider {


    public BlockStateGenerator(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, REFERENCE.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
//        simpleBlock(ModBlocks.alchemical_cauldron, modelFile -> {
//            return ConfiguredModel.allYRotations(modelFile,0,false);
//        });
        VariantBlockStateBuilder builder = getVariantBuilder(ModBlocks.altar_pillar);
        builder
                .partialState().with(AltarPillarBlock.TYPE_PROPERTY, AltarPillarBlock.EnumPillarType.NONE).modelForState().modelFile(models().getExistingFile(modLoc("block/altar_pillar"))).addModel()
                .partialState().with(AltarPillarBlock.TYPE_PROPERTY, AltarPillarBlock.EnumPillarType.BONE).modelForState().modelFile(models().getExistingFile(modLoc("block/altar_pillar_filled_bone"))).addModel()
                .partialState().with(AltarPillarBlock.TYPE_PROPERTY, AltarPillarBlock.EnumPillarType.GOLD).modelForState().modelFile(models().getExistingFile(modLoc("block/altar_pillar_filled_gold"))).addModel()
                .partialState().with(AltarPillarBlock.TYPE_PROPERTY, AltarPillarBlock.EnumPillarType.STONE).modelForState().modelFile(models().getExistingFile(modLoc("block/altar_pillar_filled_stone_bricks"))).addModel()
                .partialState().with(AltarPillarBlock.TYPE_PROPERTY, AltarPillarBlock.EnumPillarType.IRON).modelForState().modelFile(models().getExistingFile(modLoc("block/altar_pillar_filled_iron"))).addModel();

    }
}
