package de.teamlapen.vampirism.api.entity.player.actions;

import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.player.ISkillPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.util.SkillCallbacks;
import de.teamlapen.vampirism.misc.extension.IEffectInstanceWithSource;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Default implementation for an action
 */
public abstract class DefaultAction<T extends ISkillPlayer<T>> implements IAction<T> {
    private String translationId;

    public void addEffectInstance(T player, MobEffectInstance instance) {
        ((IEffectInstanceWithSource) instance).vampirism$addProperty(this.getRegistryName());
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

    @Deprecated
    @Override
    public String getTranslationKey() {
        if (this.translationId == null) {
            this.translationId = Util.makeDescriptionId("action", VampirismRegistries.ACTION.get().getKey(this));
        }
        return this.translationId;
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
            IEffectInstanceWithSource insM = ((IEffectInstanceWithSource) ins);
            if (insM.vampirism$hasProperties()) {
                if (insM.vampirism$hasProperty(this.getRegistryName())) {
                    insM.vampirism$removeEffect();
                    break;
                }
            }
            ins = insM.vampirism$getHiddenEffect();
        }
    }

    @Override
    public ISkill<T> asSkill() {
        return SkillCallbacks.<T>getActionSkillMap().get(this);
    }

    @Override
    public @NotNull String toString() {
        return this.getRegistryName() + " (" + this.getClass().getSimpleName() + ")";
    }

    /**
     * Called when the action is activated. Only called server side
     *
     * @return Whether the action was successfully activated. !Does not give any feedback to the user!
     */
    protected abstract IActionResult activate(T player, ActivationContext context);

    private @Nullable ResourceLocation getRegistryName() {
        return VampirismRegistries.ACTION.get().getKey(this);
    }
}
