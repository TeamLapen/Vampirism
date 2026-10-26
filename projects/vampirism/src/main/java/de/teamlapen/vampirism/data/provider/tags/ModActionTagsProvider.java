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

        tag(ModActionTags.DISABLE_BY_NORMAL_HOLY_WATER).add((ResourceKey) VampireActions.DISGUISE.getKey(), (ResourceKey) VampireActions.INVISIBILITY.getKey());
        tag(ModActionTags.DISABLE_BY_ENHANCED_HOLY_WATER).add((ResourceKey) VampireActions.BAT_FORM.getKey(), (ResourceKey) VampireActions.REGENERATION.getKey(), (ResourceKey) VampireActions.SUN_PROTECTION.getKey());
        tag(ModActionTags.DISABLE_BY_ULTIMATE_HOLY_WATER).add((ResourceKey) VampireActions.DAMAGE_LIMITER.getKey(), (ResourceKey) VampireActions.VAMPIRE_RAGE.getKey());

        tag(FactionActionTags.SHOW_COOLDOWN_IN_HUD)
                .add((ResourceKey) HunterActions.SIXTH_SENSE.getKey())
                .add(HunterActions.POTION_RESISTANCE.getKey())
                .add(VampireActions.DARK_BLOOD_PROJECTILE.getKey())
                .add(VampireActions.DARK_STALKER.getKey())
                .add(VampireActions.DISGUISE.getKey())
                .add(VampireActions.FREEZE.getKey())
                .add(VampireActions.DAMAGE_LIMITER.getKey())
                .add(VampireActions.HISSING.getKey())
                .add(VampireActions.INVISIBILITY.getKey())
                .add(VampireActions.VAMPIRE_RAGE.getKey())
                .add(VampireActions.REGENERATION.getKey())
                .add(VampireActions.SUMMON_BATS.getKey())
                .add(VampireActions.SUN_PROTECTION.getKey())
                .add(VampireActions.TELEPORT.getKey())
                ;

        tag(FactionActionTags.SHOW_DURATION_IN_HUD)
                .add((ResourceKey) HunterActions.CAMOUFLAGE.getKey())
                .add(HunterActions.SIXTH_SENSE.getKey())
                .add(HunterActions.POTION_RESISTANCE.getKey())
                .add(VampireActions.BAT_FORM.getKey())
                .add(VampireActions.DARK_STALKER.getKey())
                .add(VampireActions.DISGUISE.getKey())
                .add(VampireActions.DAMAGE_LIMITER.getKey())
                .add(VampireActions.VAMPIRE_RAGE.getKey())
                .add(VampireActions.REGENERATION.getKey())
                .add(VampireActions.SUN_PROTECTION.getKey())
                ;
        tag(ModActionTags.VAMPIRE_FORM_ACTIONS)
                .add((ResourceKey) VampireActions.BAT_FORM.getKey())
                .add(VampireActions.MIST.getKey())
                ;
    }
}
