package de.teamlapen.vampirism.data.provider.tags;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.common.tags.ModDataComponentTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.KeyTagProvider;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class ModDataComponentTagsProvider extends KeyTagProvider<DataComponentType<?>> {

    public ModDataComponentTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, Registries.DATA_COMPONENT_TYPE, lookupProvider, REFERENCE.MODID);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        //noinspection unchecked
        this.tag(ModDataComponentTags.FACTION_FOOD).addTags(ModDataComponentTags.HUNTER_FOOD, ModDataComponentTags.VAMPIRE_FOOD, ModDataComponentTags.BASE_FOOD);
        this.tag(ModDataComponentTags.VAMPIRE_FOOD);
        this.tag(ModDataComponentTags.HUNTER_FOOD);
        this.tag(ModDataComponentTags.BASE_FOOD).add(BuiltInRegistries.DATA_COMPONENT_TYPE.getResourceKey(DataComponents.FOOD).orElseThrow());
    }
}
