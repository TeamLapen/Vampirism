package de.teamlapen.factions.common.actions;

import de.teamlapen.factions.api.FactionRegistries;
import de.teamlapen.factions.api.actions.IAction;
import de.teamlapen.factions.api.actions.IActionResult;
import de.teamlapen.factions.api.factions.IFaction;
import de.teamlapen.factions.api.skills.ISkill;
import de.teamlapen.factions.api.skills.ISkillPlayer;
import de.teamlapen.factions.common.skills.SkillCallbacks;
import de.teamlapen.factions.misc.extensions.IEffectInstanceWithSource;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
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
        MobEffectInstance ins = player.asEntity().getEffect(effect);
        while (ins != null) {
            IEffectInstanceWithSource insM = ins;
            if (insM.factions$hasProperties()) {
                if (insM.factions$hasProperty(FactionRegistries.ACTION.get().getKey(this))) {
                    insM.factions$removeEffect();
                    break;
                }
            }
            ins = insM.factions$getHiddenEffect();
        }
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
