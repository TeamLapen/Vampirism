package de.teamlapen.vampirism.data.provider.tags;

import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.factions.actions.IAction;
import de.teamlapen.faction.common.tags.FactionActionTags;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.common.tags.ModActionTags;
import de.teamlapen.vampirism.common.world.entity.player.hunter.actions.HunterActions;
import de.teamlapen.vampirism.common.world.entity.player.hunter.skills.HunterSkills;
import de.teamlapen.vampirism.common.world.entity.player.vampire.actions.VampireActions;
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

        tag(FactionActionTags.SHOW_COOLDOWN_IN_HUD)
                .add((ResourceKey) HunterActions.AWARENESS_HUNTER.getKey())
                .add(HunterActions.POTION_RESISTANCE_HUNTER.getKey())
                .add(VampireActions.DARK_BLOOD_PROJECTILE.getKey())
                .add(VampireActions.DARK_STALKER.getKey())
                .add(VampireActions.DISGUISE_VAMPIRE.getKey())
                .add(VampireActions.FREEZE.getKey())
                .add(VampireActions.HALF_INVULNERABLE.getKey())
                .add(VampireActions.HISSING.getKey())
                .add(VampireActions.VAMPIRE_INVISIBILITY.getKey())
                .add(VampireActions.VAMPIRE_RAGE.getKey())
                .add(VampireActions.REGEN.getKey())
                .add(VampireActions.SUMMON_BAT.getKey())
                .add(VampireActions.SUNSCREEN.getKey())
                .add(VampireActions.TELEPORT.getKey())
                ;

        tag(FactionActionTags.SHOW_DURATION_IN_HUD)
                .add((ResourceKey) HunterActions.AWARENESS_HUNTER.getKey())
                .add(HunterActions.POTION_RESISTANCE_HUNTER.getKey())
                .add(HunterActions.DISGUISE_HUNTER.getKey())
                .add(VampireActions.BAT.getKey())
                .add(VampireActions.DARK_STALKER.getKey())
                .add(VampireActions.DISGUISE_VAMPIRE.getKey())
                .add(VampireActions.HALF_INVULNERABLE.getKey())
                .add(VampireActions.VAMPIRE_RAGE.getKey())
                .add(VampireActions.REGEN.getKey())
                .add(VampireActions.SUNSCREEN.getKey())
                ;
    }
}
