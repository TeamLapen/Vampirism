package de.teamlapen.factions.data.provider.model;

import de.teamlapen.factions.api.util.FResourceLocation;
import de.teamlapen.factions.common.core.FactionBlocks;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.model.TextureMapping;

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
        this.blockStateOutput.accept(createSimpleBlock(FactionBlocks.TOTEM_TOP.get(), plainVariant(FactionsModelTemplates.TOTEM.getDefaultModelLocation(FactionBlocks.TOTEM_TOP.get()))));
        this.blockStateOutput.accept(createSimpleBlock(FactionBlocks.TOTEM_TOP_CRAFTED.get(), plainVariant(FactionsModelTemplates.TOTEM.create(FactionBlocks.TOTEM_TOP_CRAFTED.get(), new TextureMapping().put(FactionsTextureSlot.OUTER, FResourceLocation.mc("block/obsidian")), this.modelOutput))));

    }
}
