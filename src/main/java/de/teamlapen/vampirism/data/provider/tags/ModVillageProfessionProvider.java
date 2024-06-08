package de.teamlapen.vampirism.data.provider.tags;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.core.ModVillage;
import de.teamlapen.vampirism.core.tags.ModProfessionTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModVillageProfessionProvider extends TagsProvider<VillagerProfession> {

    public ModVillageProfessionProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, Registries.VILLAGER_PROFESSION, lookupProvider, REFERENCE.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider holderProvider) {
        tag(ModProfessionTags.HAS_FACTION).add(ModVillage.HUNTER_EXPERT.getKey(), ModVillage.VAMPIRE_EXPERT.getKey());
        tag(ModProfessionTags.IS_VAMPIRE).add(ModVillage.VAMPIRE_EXPERT.getKey());
        tag(ModProfessionTags.IS_HUNTER).add(ModVillage.HUNTER_EXPERT.getKey());
    }
}
