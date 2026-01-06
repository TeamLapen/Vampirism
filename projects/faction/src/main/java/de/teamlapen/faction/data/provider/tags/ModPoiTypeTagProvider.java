package de.teamlapen.faction.data.provider.tags;

import de.teamlapen.faction.api.util.REFERENCE;
import de.teamlapen.faction.common.tags.FactionPoiTypeTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.KeyTagProvider;
import net.minecraft.world.entity.ai.village.poi.PoiType;

import java.util.concurrent.CompletableFuture;

public class ModPoiTypeTagProvider extends KeyTagProvider<PoiType> {

    public ModPoiTypeTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
        super(output, Registries.POINT_OF_INTEREST_TYPE, provider, REFERENCE.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(FactionPoiTypeTags.HAS_FACTION);
    }
}
