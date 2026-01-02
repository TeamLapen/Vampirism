package de.teamlapen.factions.data.provider.model;

import de.teamlapen.factions.common.core.FactionItems;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ModelTemplates;

public class ModItemModelGenerators extends ItemModelGenerators {

    public ModItemModelGenerators(ItemModelGenerators generator) {
        super(generator.itemModelOutput, generator.modelOutput);
    }

    @Override
    public void run() {
        generateFlatItem(FactionItems.OBLIVION_POTION.get(), ModelTemplates.FLAT_ITEM);
        generateFlatItem(FactionItems.SYRINGE_EMPTY.get(), ModelTemplates.FLAT_ITEM);
    }
}
