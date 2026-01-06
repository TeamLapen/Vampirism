package de.teamlapen.faction.data.provider.model;

import de.teamlapen.faction.api.util.FIdentifier;
import de.teamlapen.faction.common.core.FactionBlocks;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;

public class ModBlockModelGenerators extends BlockModelGenerators {

    public ModBlockModelGenerators(BlockModelGenerators generators) {
        super(generators.blockStateOutput, generators.itemModelOutput, generators.modelOutput);
    }

    @Override
    public void run() {
        createTotem();
    }

    protected void createTotem() {
        createNonTemplateModelBlock(FactionBlocks.TOTEM_BASE.get());
        createNonTemplateModelBlock(FactionBlocks.TOTEM_TOP.get());
        this.blockStateOutput.accept(createSimpleBlock(FactionBlocks.TOTEM_TOP_CRAFTED.get(), plainVariant(FactionsModelTemplates.TOTEM_TOP.create(FactionBlocks.TOTEM_TOP_CRAFTED.get(), new TextureMapping().putForced(TextureSlot.BOTTOM, FIdentifier.mod("block/totem_top_crafted_bottom")).putForced(TextureSlot.SIDE, FResourceLocation.mod("block/totem_top_crafted_side")).putForced(TextureSlot.PARTICLE, FResourceLocation.mc("block/obsidian")), this.modelOutput))));
    }
}
