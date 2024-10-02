package de.teamlapen.vampirism.data.provider.tags;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.core.tags.ModEffectTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModEffectTypeProvider extends TagsProvider<MobEffect> {

    public ModEffectTypeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, Registries.MOB_EFFECT, provider, REFERENCE.MODID, existingFileHelper);
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    protected void addTags(HolderLookup.@NotNull Provider pProvider) {
        this.tag(ModEffectTags.HUNTER_POTION_RESISTANCE).add(MobEffects.BLINDNESS.unwrapKey().orElseThrow(), MobEffects.CONFUSION.unwrapKey().orElseThrow(), MobEffects.HUNGER.unwrapKey().orElseThrow(), MobEffects.POISON.unwrapKey().orElseThrow(), ModEffects.FREEZE.getKey());
        this.tag(ModEffectTags.DISABLES_ACTIONS_HUNTER).addTag(ModEffectTags.DISABLES_ACTIONS);
        this.tag(ModEffectTags.DISABLES_ACTIONS_VAMPIRE).addTag(ModEffectTags.DISABLES_ACTIONS);
        this.tag(ModEffectTags.DISABLES_ACTIONS).add(ModEffects.RESURRECTION_FATIGUE.getKey());
    }
}
