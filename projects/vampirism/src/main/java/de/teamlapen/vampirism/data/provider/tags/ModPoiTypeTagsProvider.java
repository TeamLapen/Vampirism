package de.teamlapen.vampirism.data.provider.tags;

import de.teamlapen.faction.common.tags.FactionPoiTypeTags;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.common.core.ModVillage;
import de.teamlapen.vampirism.common.tags.ModPoiTypeTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.PoiTypeTagsProvider;
import net.minecraft.tags.PoiTypeTags;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class ModPoiTypeTagsProvider extends PoiTypeTagsProvider {

    public ModPoiTypeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
        super(output, provider, REFERENCE.MODID);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider holderProvider) {
        tag(FactionPoiTypeTags.HAS_FACTION).add(ModVillage.NO_FACTION_TOTEM.getKey(), ModVillage.HUNTER_TOTEM.getKey(), ModVillage.VAMPIRE_TOTEM.getKey());
        tag(ModPoiTypeTags.IS_HUNTER).add(ModVillage.HUNTER_TOTEM.getKey());
        tag(ModPoiTypeTags.IS_VAMPIRE).add(ModVillage.VAMPIRE_TOTEM.getKey());
        tag(PoiTypeTags.ACQUIRABLE_JOB_SITE).add(ModVillage.HUNTER_TOTEM.getKey(), ModVillage.VAMPIRE_TOTEM.getKey(), ModVillage.ALTAR_CLEANSING.getKey());
        tag(PoiTypeTags.VILLAGE).add(ModVillage.NO_FACTION_TOTEM.getKey(), ModVillage.HUNTER_TOTEM.getKey(), ModVillage.VAMPIRE_TOTEM.getKey(), ModVillage.ALTAR_CLEANSING.getKey());
    }
}
