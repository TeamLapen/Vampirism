package de.teamlapen.factions.data.provider.model;

import de.teamlapen.factions.api.util.FResourceLocation;
import de.teamlapen.factions.common.core.FactionBlocks;
import de.teamlapen.factions.common.core.FactionItems;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;

public class ModItemModelGenerators extends ItemModelGenerators {

    public ModItemModelGenerators(ItemModelGenerators generator) {
        super(generator.itemModelOutput, generator.modelOutput);
    }

    @Override
    public void run() {
        generateFlatItem(FactionItems.OBLIVION_POTION.get(), ModelTemplates.FLAT_ITEM);
        generateFlatItem(FactionItems.SYRINGE_EMPTY.get(), ModelTemplates.FLAT_ITEM);
        generateFlatItemWithTexture(FactionBlocks.MED_CHAIR.get().asItem(), FResourceLocation.mod("item/med_chair"));
    }

    protected void generateFlatItemWithTexture(Item item, Identifier texture) {
        this.itemModelOutput.accept(item, ItemModelUtils.plainModel(createFlatItemWithTexture(item, texture)));
    }

    protected Identifier createFlatItemWithTexture(Item item, Identifier texture) {
        return ModelTemplates.FLAT_ITEM.create(ModelLocationUtils.getModelLocation(item), TextureMapping.layer0(texture), this.modelOutput);
    }
}
