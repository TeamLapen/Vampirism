package de.teamlapen.vampirism.data.provider.tags;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.common.core.ModDimensions;
import de.teamlapen.vampirism.common.tags.ModDimensionTypeTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.KeyTagProvider;
import net.minecraft.data.worldgen.DimensionTypes;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;

import java.util.concurrent.CompletableFuture;

public class ModDimensionTypeTagsProvider extends KeyTagProvider<DimensionType> {


    public ModDimensionTypeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, Registries.DIMENSION_TYPE, lookupProvider, REFERENCE.MODID);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(ModDimensionTypeTags.HAS_NO_SUNDAMAGE)
                .add(BuiltinDimensionTypes.NETHER, BuiltinDimensionTypes.OVERWORLD_CAVES, BuiltinDimensionTypes.END)
                .add(ModDimensions.VELMORRA_DIMENSION_TYPE);
    }
}
