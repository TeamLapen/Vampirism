package de.teamlapen.faction.data.provider.tags;

import de.teamlapen.faction.api.util.REFERENCE;
import de.teamlapen.faction.common.tags.FactionProfessionTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.KeyTagProvider;
import net.minecraft.world.entity.npc.villager.VillagerProfession;

import java.util.concurrent.CompletableFuture;

public class ModProfessionTagProvider extends KeyTagProvider<VillagerProfession> {

    public ModProfessionTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
        super(output, Registries.VILLAGER_PROFESSION, provider, REFERENCE.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(FactionProfessionTags.HAS_FACTION);
    }
}
