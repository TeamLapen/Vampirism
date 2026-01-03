package de.teamlapen.factions.data.provider.model;

import de.teamlapen.factions.api.util.FResourceLocation;
import de.teamlapen.factions.common.core.FactionBlocks;
import de.teamlapen.factions.common.world.blocks.MedChairBlock;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.core.Direction;

public class ModBlockModelGenerators extends BlockModelGenerators {

    public ModBlockModelGenerators(BlockModelGenerators generators) {
        super(generators.blockStateOutput, generators.itemModelOutput, generators.modelOutput);
    }

    @Override
    public void run() {
        createTotem();
        createMedChair();
    }

    protected void createTotem() {
        createNonTemplateModelBlock(FactionBlocks.TOTEM_BASE.get());
        createNonTemplateModelBlock(FactionBlocks.TOTEM_TOP.get());
        this.blockStateOutput.accept(createSimpleBlock(FactionBlocks.TOTEM_TOP_CRAFTED.get(), plainVariant(FactionsModelTemplates.TOTEM_TOP.create(FactionBlocks.TOTEM_TOP_CRAFTED.get(), new TextureMapping().putForced(TextureSlot.BOTTOM, FResourceLocation.mod("block/totem_top_crafted_bottom")).putForced(TextureSlot.SIDE, FResourceLocation.mod("block/totem_top_crafted_side")).putForced(TextureSlot.PARTICLE, FResourceLocation.mc("block/obsidian")), this.modelOutput))));
    }

    protected void createMedChair() {
        this.blockStateOutput.accept(MultiVariantGenerator.dispatch(FactionBlocks.MED_CHAIR.get())
                .with(PropertyDispatch.initial(MedChairBlock.PART)
                        .select(MedChairBlock.EnumPart.BOTTOM, plainVariant(FResourceLocation.mod("block/medchairbase")))
                        .select(MedChairBlock.EnumPart.TOP, plainVariant(FResourceLocation.mod("block/medchairhead")))
                )
                .with(PropertyDispatch.modify(MedChairBlock.FACING)
                        .select(Direction.NORTH, NOP)
                        .select(Direction.EAST, Y_ROT_90)
                        .select(Direction.SOUTH, Y_ROT_180)
                        .select(Direction.WEST, Y_ROT_270)
                )
        );
    }
}
