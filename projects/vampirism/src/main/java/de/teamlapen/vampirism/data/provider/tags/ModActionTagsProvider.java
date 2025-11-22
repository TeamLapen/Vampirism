package de.teamlapen.vampirism.data.provider.tags;

import de.teamlapen.factions.api.FactionRegistries;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.factions.api.actions.IAction;
import de.teamlapen.vampirism.common.entity.player.vampire.actions.VampireActions;
import de.teamlapen.vampirism.common.tags.ModActionTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.KeyTagProvider;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class ModActionTagsProvider extends KeyTagProvider<IAction<?>> {

    public ModActionTagsProvider(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pLookupProvider) {
        super(pOutput, FactionRegistries.Keys.ACTION, pLookupProvider, VReference.MODID);
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
