package de.teamlapen.faction.data.provider.tags;

import de.teamlapen.faction.api.util.REFERENCE;
import de.teamlapen.faction.common.core.FactionEffects;
import de.teamlapen.faction.common.tags.FactionEffectTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.KeyTagProvider;
import net.minecraft.world.effect.MobEffect;

import java.util.concurrent.CompletableFuture;

public class ModEffectsProvider extends KeyTagProvider<MobEffect> {

    public ModEffectsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
        super(output, Registries.MOB_EFFECT, provider, REFERENCE.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(FactionEffectTags.DISABLES_ACTIONS).add(FactionEffects.RESURRECTION_FATIGUE.getKey());
    }
}