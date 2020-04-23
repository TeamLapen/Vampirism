package de.teamlapen.vampirism.data;

import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelProvider;

import javax.annotation.Nonnull;

public class ModelGenerator extends ItemModelProvider {
    public ModelGenerator(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, REFERENCE.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        withExistingParent("altar_tip","block/altar_tip");
    }

    @Nonnull
    @Override
    public String getName() {
        return "Vampirism Item Models";
    }
}
