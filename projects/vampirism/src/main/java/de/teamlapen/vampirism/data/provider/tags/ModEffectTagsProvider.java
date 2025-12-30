package de.teamlapen.vampirism.data.provider.tags;

import de.teamlapen.faction.common.tags.FactionEffectTags;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.common.core.ModEffects;
import de.teamlapen.vampirism.common.tags.ModEffectTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.KeyTagProvider;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class ModEffectTagsProvider extends KeyTagProvider<MobEffect> {

    public ModEffectTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
        super(output, Registries.MOB_EFFECT, provider, REFERENCE.MODID);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider pProvider) {
        this.tag(ModEffectTags.HUNTER_POTION_RESISTANCE).add(MobEffects.BLINDNESS.unwrapKey().orElseThrow(), MobEffects.NAUSEA.unwrapKey().orElseThrow(), MobEffects.HUNGER.unwrapKey().orElseThrow(), MobEffects.POISON.unwrapKey().orElseThrow(), ModEffects.FREEZE.getKey());
        this.tag(ModEffectTags.DISABLES_ACTIONS_HUNTER)
                .addTag(FactionEffectTags.DISABLES_ACTIONS);
        this.tag(ModEffectTags.DISABLES_ACTIONS_VAMPIRE)
                .addTag(FactionEffectTags.DISABLES_ACTIONS)
                .add(ModEffects.CRUCIFIX_SUPPRESSION.getKey());
    }
}
