package de.teamlapen.vampirism.data.provider.tags;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.core.tags.ModActionTags;
import de.teamlapen.vampirism.entity.player.vampire.actions.VampireActions;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModActionTagsProvider extends TagsProvider<IAction<?>> {


    protected ModActionTagsProvider(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pLookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(pOutput, VampirismRegistries.Keys.ACTION, pLookupProvider, VReference.MODID, existingFileHelper);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    protected void addTags(HolderLookup.@NotNull Provider pProvider) {
        tag(ModActionTags.DISABLE_BY_HOLY_WATER).addTags(ModActionTags.DISABLE_BY_NORMAL_HOLY_WATER, ModActionTags.DISABLE_BY_ENHANCED_HOLY_WATER, ModActionTags.DISABLE_BY_ULTIMATE_HOLY_WATER);
        tag(ModActionTags.DISABLE_BY_ULTIMATE_HOLY_WATER).addTags(ModActionTags.DISABLE_BY_ENHANCED_HOLY_WATER, ModActionTags.DISABLE_BY_NORMAL_HOLY_WATER);
        tag(ModActionTags.DISABLE_BY_ENHANCED_HOLY_WATER).addTag(ModActionTags.DISABLE_BY_NORMAL_HOLY_WATER);

        tag(ModActionTags.DISABLE_BY_NORMAL_HOLY_WATER).add((ResourceKey) VampireActions.DISGUISE_VAMPIRE.getKey(), (ResourceKey) VampireActions.VAMPIRE_INVISIBILITY.getKey());
        tag(ModActionTags.DISABLE_BY_ENHANCED_HOLY_WATER).add((ResourceKey) VampireActions.BAT.getKey(), (ResourceKey) VampireActions.REGEN.getKey(), (ResourceKey) VampireActions.SUNSCREEN.getKey());
        tag(ModActionTags.DISABLE_BY_ULTIMATE_HOLY_WATER).add((ResourceKey) VampireActions.HALF_INVULNERABLE.getKey(), (ResourceKey) VampireActions.VAMPIRE_RAGE.getKey());
    }
}
