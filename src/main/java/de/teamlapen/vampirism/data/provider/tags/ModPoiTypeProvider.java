package de.teamlapen.vampirism.data.provider.tags;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.core.ModVillage;
import de.teamlapen.vampirism.core.tags.ModPoiTypeTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.PoiTypeTagsProvider;
import net.minecraft.tags.PoiTypeTags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModPoiTypeProvider extends PoiTypeTagsProvider {

    public ModPoiTypeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, REFERENCE.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider holderProvider) {
        tag(ModPoiTypeTags.HAS_FACTION).add(ModVillage.NO_FACTION_TOTEM.getKey(), ModVillage.HUNTER_TOTEM.getKey(), ModVillage.VAMPIRE_TOTEM.getKey());
        tag(ModPoiTypeTags.IS_HUNTER).add(ModVillage.HUNTER_TOTEM.getKey());
        tag(ModPoiTypeTags.IS_VAMPIRE).add(ModVillage.VAMPIRE_TOTEM.getKey());
        tag(PoiTypeTags.ACQUIRABLE_JOB_SITE).add(ModVillage.HUNTER_TOTEM.getKey(), ModVillage.VAMPIRE_TOTEM.getKey(), ModVillage.ALTAR_CLEANSING.getKey());
        tag(PoiTypeTags.VILLAGE).add(ModVillage.NO_FACTION_TOTEM.getKey(), ModVillage.HUNTER_TOTEM.getKey(), ModVillage.VAMPIRE_TOTEM.getKey(), ModVillage.ALTAR_CLEANSING.getKey());
    }
}
