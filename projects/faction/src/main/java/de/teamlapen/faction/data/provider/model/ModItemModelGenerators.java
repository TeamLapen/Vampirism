package de.teamlapen.faction.data.provider.model;

import de.teamlapen.faction.common.core.FactionItems;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ModelTemplates;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class ModItemModelGenerators extends ItemModelGenerators {

    public ModItemModelGenerators(ItemModelGenerators generator) {
        super(generator.itemModelOutput, generator.modelOutput);
    }

    @Override
    public void run() {
        generateFlatItem(FactionItems.OBLIVION_POTION.get(), ModelTemplates.FLAT_ITEM);
    }
}
