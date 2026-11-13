package de.teamlapen.vampirism.common.world.entity.player.vampire.actions;

import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.factions.IFaction;
import de.teamlapen.faction.api.factions.actions.IActionResult;
import de.teamlapen.faction.common.factions.actions.DefaultAction;
import de.teamlapen.vampirism.api.VampirismTags;
import de.teamlapen.vampirism.api.world.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.common.core.ModEffects;
import de.teamlapen.vampirism.common.tags.ModActionTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;

/**
 * Basic implementation of IAction<IVampirePlayer>. It is recommended to extend this
 */
public abstract class DefaultVampireAction extends DefaultAction<IVampirePlayer> {

    @Override
    public TagKey<? extends IFaction<?>> factions() {
        return VampirismTags.Factions.IS_VAMPIRE;
    }

    /**
     * Checks restrictions that apply to all vampire actions. Override {@link #canBeUsedByVampire(IVampirePlayer)} for action specific requirements
     */
    @Override
    public final IActionResult canBeUsedBy(IVampirePlayer player) {
        MobEffectInstance suppression = player.asEntity().getEffect(ModEffects.CRUCIFIX_SUPPRESSION);
        if (suppression != null && FactionRegistries.ACTION.get().wrapAsHolder(this).is(ModActionTags.disabledByCrucifix(suppression.getAmplifier()))) {
            return IActionResult.DISABLED_EFFECT;
        }
        return canBeUsedByVampire(player);
    }

    /**
     * Can be overridden to check additional requirements
     */
    protected IActionResult canBeUsedByVampire(IVampirePlayer player) {
        return IActionResult.SUCCESS;
    }
}
