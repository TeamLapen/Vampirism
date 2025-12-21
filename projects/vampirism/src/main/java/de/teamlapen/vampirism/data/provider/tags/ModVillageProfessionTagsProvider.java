package de.teamlapen.vampirism.data.provider.tags;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.common.core.ModVillage;
import de.teamlapen.vampirism.common.tags.ModProfessionTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.KeyTagProvider;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class ModVillageProfessionTagsProvider extends KeyTagProvider<VillagerProfession> {

    public ModVillageProfessionTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, Registries.VILLAGER_PROFESSION, lookupProvider, REFERENCE.MODID);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider holderProvider) {
        tag(ModProfessionTags.HAS_FACTION).add(ModVillage.HUNTER_EXPERT.getKey(), ModVillage.VAMPIRE_EXPERT.getKey());
        tag(ModProfessionTags.IS_VAMPIRE).add(ModVillage.VAMPIRE_EXPERT.getKey());
        tag(ModProfessionTags.IS_HUNTER).add(ModVillage.HUNTER_EXPERT.getKey());
    }
}
