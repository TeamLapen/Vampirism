package de.teamlapen.faction.data.provider.tags;

import de.teamlapen.faction.api.util.REFERENCE;
import de.teamlapen.faction.common.core.FactionDamageTypes;
import de.teamlapen.faction.common.world.ModDamageSources;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.KeyTagProvider;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageType;

import java.util.concurrent.CompletableFuture;

public class ModDamageTypeTagsProvider extends KeyTagProvider<DamageType> {

    public ModDamageTypeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
        super(output, Registries.DAMAGE_TYPE, provider, REFERENCE.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(DamageTypeTags.BYPASSES_ARMOR).add(FactionDamageTypes.LEAVE_FACTION);
        tag(DamageTypeTags.BYPASSES_EFFECTS).add(FactionDamageTypes.LEAVE_FACTION);
    }
}
