package de.teamlapen.faction.common.factions.actions;

import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.factions.IFaction;
import de.teamlapen.faction.api.factions.actions.IAction;
import de.teamlapen.faction.api.factions.actions.IActionResult;
import de.teamlapen.faction.api.factions.skills.ISkill;
import de.teamlapen.faction.api.factions.skills.ISkillPlayer;
import de.teamlapen.faction.common.factions.skills.SkillCallbacks;
import de.teamlapen.faction.misc.extensions.IEffectInstanceWithSource;
import net.minecraft.core.Holder;
import net.minecraft.util.Util;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import org.jetbrains.annotations.Nullable;

/**
 * Default implementation for an action
 */
public abstract class DefaultAction<T extends ISkillPlayer<T>> implements IAction<T> {
    @Nullable
    private String descriptionId;

    public void addEffectInstance(T player, MobEffectInstance instance) {
        instance.factions$addProperty(FactionRegistries.ACTION.get().getKey(this));
        player.asEntity().addEffect(instance);
    }

    /**
     * Can be overridden to check additional requirements
     */
    public IActionResult canBeUsedBy(T player) {
        return IActionResult.SUCCESS;
    }

    @Override
    public final IActionResult canUse(T player) {
        if (!isEnabled()) {
            return IActionResult.DISABLED_CONFIG;
        }
        if (IFaction.is(player.getFaction(), this.factions())) {
            return canBeUsedBy(player);
        } else {
            return IActionResult.DISALLOWED_FACTION;
        }

    }

    @Override
    public String getDescriptionId() {
        if (this.descriptionId == null) {
            this.descriptionId = Util.makeDescriptionId("action", FactionRegistries.ACTION.get().getKey(this));
        }
        return this.descriptionId;
    }

    /**
     * @return Should return false if deactivated in configs
     */
    public abstract boolean isEnabled();

    @Override
    public IActionResult onActivated(T player, ActivationContext context) {
        if (IFaction.is(player.getFaction(), this.factions())) {
            return activate(player, context);
        } else {
            throw new IllegalArgumentException("Faction player is not allowed to use action");
        }
    }

    public void removePotionEffect(T player, Holder<MobEffect> effect) {
        //noinspection DataFlowIssue
        IEffectInstanceWithSource.removePotionEffect(player.asEntity(), effect, FactionRegistries.ACTION.get().getKey(this));
    }

    @Override
    public ISkill<T> asSkill() {
        return SkillCallbacks.<T>getActionSkillMap().get(this);
    }

    /**
     * Called when the action is activated. Only called server side
     *
     * @return Whether the action was successfully activated. !Does not give any feedback to the user!
     */
    protected abstract IActionResult activate(T player, ActivationContext context);

}
