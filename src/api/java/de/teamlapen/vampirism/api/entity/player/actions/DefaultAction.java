package de.teamlapen.vampirism.api.entity.player.actions;

import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.effect.EffectInstanceWithSource;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.util.SkillCallbacks;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Default implementation for an action
 */
public abstract class DefaultAction<T extends IFactionPlayer<T>> implements IAction<T> {
    private Component name;

    public void addEffectInstance(@NotNull T player, @NotNull MobEffectInstance instance) {
        ((EffectInstanceWithSource) instance).setSource(this.getRegistryName());
        player.getRepresentingPlayer().addEffect(instance);
    }

    /**
     * Can be overridden to check additional requirements
     */
    public boolean canBeUsedBy(T player) {
        return true;
    }

    @Override
    public final IAction.@NotNull PERM canUse(@NotNull T player) {
        if (!isEnabled()) {
            return IAction.PERM.DISABLED;
        }
        if (this.getFaction().map(f -> f.getFactionPlayerInterface().isInstance(player)).orElse(true)) {
            return (canBeUsedBy(player) ? IAction.PERM.ALLOWED : IAction.PERM.DISALLOWED);
        } else {
            throw new IllegalArgumentException("Faction player instance is of wrong class " + player.getClass() + " instead of " + this.getFaction().get().getFactionPlayerInterface());
        }

    }

    @Override
    public @NotNull Component getName() {
        return name == null ? name = Component.translatable(getTranslationKey()) : name;
    }

    @Deprecated
    @Override
    public @NotNull String getTranslationKey() {
        return "action." + getRegistryName().getNamespace() + "." + getRegistryName().getPath();
    }

    /**
     * @return Should return false if deactivated in configs
     */
    public abstract boolean isEnabled();

    @Override
    public boolean onActivated(@NotNull T player, ActivationContext context) {
        if (this.getFaction().map(f -> f.getFactionPlayerInterface().isInstance(player)).orElse(true)) {
            return activate(player, context);
        } else {
            throw new IllegalArgumentException("Faction player instance is of wrong class " + player.getClass() + " instead of " + this.getFaction().get().getFactionPlayerInterface());
        }
    }

    public void removePotionEffect(@NotNull T player, @NotNull MobEffect effect) {
        MobEffectInstance ins = player.getRepresentingPlayer().getEffect(effect);
        while (ins != null) {
            EffectInstanceWithSource insM = ((EffectInstanceWithSource) ins);
            if (insM.hasSource()) {
                if (insM.getSource().equals(this.getRegistryName())) {
                    insM.removeEffect();
                    break;
                }
            }
            ins = insM.getHiddenEffect();
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
    protected abstract boolean activate(T player, ActivationContext context);

    private @Nullable ResourceLocation getRegistryName() {
        return VampirismRegistries.ACTIONS.get().getKey(this);
    }
}
